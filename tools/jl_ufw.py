#!/usr/bin/env python3
"""Inspect and extract JieLi ``.ufw`` firmware containers.

The format and byte cipher in this module are literal reconstructions of the
public JieLi iOS ``JL_OTALib`` implementation.  No watch access is involved.

Container layout:

* a 64-byte obfuscated ``UFW_SYD_HEAD_V1`` header;
* ``file_count`` raw 80-byte directory records, individually obfuscated;
* aligned file payloads;
* a ``tail.bin`` record containing the encoded 16-bit payload key.

The directory itself is checksummed before its records are decoded.  Payload
records may designate a subrange encrypted in independently keyed 32-byte
blocks.  The CRC stored in each record covers the decrypted logical payload.
"""

from __future__ import annotations

import argparse
import hashlib
import json
import struct
from dataclasses import asdict, dataclass
from pathlib import Path
from typing import Any


HEADER_SIZE = 64
ENTRY_SIZE = 80
DEFAULT_KEY = 0xFFFF
TAIL_MAGIC = bytes.fromhex("13 92 65 36 73 42")
TAIL_NAME = b"JLUFW"

CRC16_NIBBLE_TABLE = (
    0x0000,
    0x1021,
    0x2042,
    0x3063,
    0x4084,
    0x50A5,
    0x60C6,
    0x70E7,
    0x8108,
    0x9129,
    0xA14A,
    0xB16B,
    0xC18C,
    0xD1AD,
    0xE1CE,
    0xF1EF,
)

FILE_TYPES = {
    0x00: "flash",
    0x01: "otp",
    0x02: "info-log",
    0x03: "burn-config",
    0x04: "script-data",
    0x05: "source",
    0x06: "name",
    0x07: "otp-version",
    0x08: "otp-config",
    0x09: "otp-chip-control",
    0x0A: "otp-mini-uboot",
    0x0B: "otp-fix-uboot",
    0x0C: "second-flash",
    0x0D: "efuse-config",
    0x0E: "resource",
    0x0F: "key-info",
    0x10: "burner-customer-data",
    0x11: "burner-dv15-flash",
    0x12: "burner-app-flash",
    0x13: "burner-data-flash",
    0x14: "burner-br21-flash",
    0x20: "alternate-flash-2",
    0x21: "alternate-flash-3",
    0x22: "alternate-flash-4",
    0x31: "loader",
    0x64: "test-box-ota",
    0x71: "third-party-data",
    0xA0: "ota-target-device-info",
    0xA1: "burn-count-limit",
    0xEE: "additional",
    0xEF: "passthrough",
    0xFA: "hidden-config",
    0xFB: "invisible-data",
    0xFE: "visible-data",
    0xFF: "tail",
}


class UfwError(ValueError):
    """Raised when a UFW structural or checksum invariant fails."""


def crc16(data: bytes | bytearray, seed: int = 0) -> int:
    """JieLi's nibble-table CRC-16/CCITT implementation."""
    value = seed & 0xFFFF
    for byte in data:
        value = ((value << 4) & 0xFFFF) ^ CRC16_NIBBLE_TABLE[
            ((value >> 12) ^ (byte >> 4)) & 0x0F
        ]
        value = ((value << 4) & 0xFFFF) ^ CRC16_NIBBLE_TABLE[
            ((value >> 12) ^ (byte & 0x0F)) & 0x0F
        ]
    return value


def _next_cd03_state(state: int) -> int:
    """Advance the 16-bit state used by ``cd03_crc_encode``."""
    state &= 0xFFFF
    bit15 = (state >> 15) & 1
    return (
        ((state << 1) & 0xEFC0)
        | ((((state >> 11) & 1) ^ bit15) << 12)
        | ((((state >> 4) & 1) ^ bit15) << 5)
        | ((state << 1) & 0x001E)
        | bit15
    ) & 0xFFFF


def cd03_xor(data: bytes | bytearray, seed: int = DEFAULT_KEY, offset: int = 0) -> bytes:
    """Apply the symmetric JieLi CD03 byte stream operation."""
    output = bytearray(data)
    state = (seed ^ (offset >> 2)) & 0xFFFF
    for index in range(len(output)):
        output[index] ^= state & 0xFF
        state = _next_cd03_state(state)
    return bytes(output)


def decrypt_payload(data: bytes | bytearray, key: int, absolute_offset: int) -> bytes:
    """Decrypt a UFW payload range using independently seeded 32-byte blocks."""
    output = bytearray(data)
    for relative in range(0, len(output), 32):
        end = min(relative + 32, len(output))
        output[relative:end] = cd03_xor(
            output[relative:end], key, absolute_offset + relative
        )
    return bytes(output)


def derive_tail_key(tail: bytes) -> int:
    """Reproduce ``byteArrayDataToKey`` from the first 32 tail bytes."""
    if len(tail) < 32:
        raise UfwError("tail is shorter than the 32-byte encoded key")
    threshold = sum(tail[:16]) & 0xFF
    if threshold >= 0xE0:
        threshold = 0xAA
    elif threshold < 0x11:
        threshold = 0x55

    key = 0
    for bit in range(16):
        if threshold > (tail[15 - bit] ^ tail[16 + bit]):
            key |= 1 << bit
    return key


def _c_string(data: bytes) -> str:
    return data.split(b"\0", 1)[0].decode("ascii", "replace")


@dataclass(frozen=True)
class UfwHeader:
    stored_crc16: int
    calculated_crc16: int
    stored_directory_crc16: int
    calculated_directory_crc16: int
    file_length: int
    file_count: int
    version: int
    chip_name: str

    @property
    def crc_ok(self) -> bool:
        return self.stored_crc16 == self.calculated_crc16

    @property
    def directory_crc_ok(self) -> bool:
        return self.stored_directory_crc16 == self.calculated_directory_crc16


@dataclass(frozen=True)
class UfwEntry:
    file_type: int
    type_name: str
    index: int
    stored_crc16: int
    calculated_crc16: int
    version: int
    address: int
    length: int
    aligned_length: int
    encrypted_offset: int
    encrypted_length: int
    name: str

    @property
    def crc_ok(self) -> bool:
        return self.stored_crc16 == self.calculated_crc16


class UfwContainer:
    def __init__(self, blob: bytes):
        if len(blob) < HEADER_SIZE:
            raise UfwError("file is shorter than the 64-byte UFW header")
        self.blob = blob
        self._decoded_header = cd03_xor(blob[:HEADER_SIZE])
        (
            stored_crc,
            directory_crc,
            file_length,
            file_count,
            version,
            _reserved,
            chip_name,
            *_rest,
        ) = struct.unpack("<HHIHHI16s4I4I", self._decoded_header)

        directory_end = HEADER_SIZE + file_count * ENTRY_SIZE
        if file_length > len(blob):
            raise UfwError(
                f"header length {file_length} exceeds input length {len(blob)}"
            )
        if directory_end > len(blob):
            raise UfwError("directory extends beyond the input file")

        raw_directory = blob[HEADER_SIZE:directory_end]
        self.header = UfwHeader(
            stored_crc16=stored_crc,
            calculated_crc16=crc16(self._decoded_header[2:]),
            stored_directory_crc16=directory_crc,
            calculated_directory_crc16=crc16(raw_directory),
            file_length=file_length,
            file_count=file_count,
            version=version,
            chip_name=_c_string(chip_name),
        )
        if not self.header.crc_ok:
            raise UfwError(
                f"header CRC mismatch: stored 0x{stored_crc:04x}, "
                f"calculated 0x{self.header.calculated_crc16:04x}"
            )
        if not self.header.directory_crc_ok:
            raise UfwError(
                f"directory CRC mismatch: stored 0x{directory_crc:04x}, "
                f"calculated 0x{self.header.calculated_directory_crc16:04x}"
            )

        self._entry_records = [
            self._decode_entry(raw_directory[i : i + ENTRY_SIZE])
            for i in range(0, len(raw_directory), ENTRY_SIZE)
        ]
        self.chip_key = self._find_chip_key()
        self.entries = [self._materialize_entry(record) for record in self._entry_records]

    @staticmethod
    def _decode_entry(raw: bytes) -> dict[str, Any]:
        decoded = cd03_xor(raw)
        (
            file_type,
            _reserved8,
            index,
            stored_crc,
            version,
            address,
            length,
            aligned_length,
            encrypted_offset,
            encrypted_length,
            _reserved32,
            *_remaining,
        ) = struct.unpack("<BBHHHIIIIII4I4I16s", decoded)
        name = _c_string(decoded[64:80])
        return {
            "file_type": file_type,
            "index": index,
            "stored_crc": stored_crc,
            "version": version,
            "address": address,
            "length": length,
            "aligned_length": aligned_length,
            "encrypted_offset": encrypted_offset,
            "encrypted_length": encrypted_length,
            "name": name,
        }

    def _raw_record_payload(self, record: dict[str, Any]) -> bytes:
        address = record["address"]
        aligned_length = record["aligned_length"]
        end = address + aligned_length
        if end > len(self.blob):
            raise UfwError(
                f"entry {record['index']} ({record['name']!r}) extends beyond input"
            )
        if record["length"] > aligned_length:
            raise UfwError(
                f"entry {record['index']} logical length exceeds aligned length"
            )
        return self.blob[address:end]

    def _find_chip_key(self) -> int:
        for record in self._entry_records:
            if record["file_type"] != 0xFF or record["name"] != "tail.bin":
                continue
            tail = self._raw_record_payload(record)
            if len(tail) < 64:
                raise UfwError("tail.bin is shorter than 64 bytes")
            stored_key_crc = struct.unpack_from("<H", tail, 32)[0]
            calculated_key_crc = crc16(tail[:32])
            if stored_key_crc != calculated_key_crc:
                raise UfwError(
                    f"tail key CRC mismatch: stored 0x{stored_key_crc:04x}, "
                    f"calculated 0x{calculated_key_crc:04x}"
                )
            if tail[34:40] != TAIL_MAGIC:
                raise UfwError(f"unexpected tail magic {tail[34:40].hex(' ')}")
            if not tail[48:].startswith(TAIL_NAME):
                raise UfwError("tail.bin does not contain the JLUFW name")
            return derive_tail_key(tail)
        raise UfwError("no tail.bin directory record was found")

    def payload_for_record(self, record: dict[str, Any]) -> bytes:
        payload = bytearray(self._raw_record_payload(record))
        encrypted_offset = record["encrypted_offset"]
        encrypted_length = record["encrypted_length"]
        encrypted_end = encrypted_offset + encrypted_length
        if encrypted_end > len(payload):
            raise UfwError(
                f"entry {record['index']} encrypted range extends beyond payload"
            )
        if encrypted_length:
            payload[encrypted_offset:encrypted_end] = decrypt_payload(
                payload[encrypted_offset:encrypted_end],
                self.chip_key,
                record["address"] + encrypted_offset,
            )
        return bytes(payload[: record["length"]])

    def _materialize_entry(self, record: dict[str, Any]) -> UfwEntry:
        payload = self.payload_for_record(record)
        return UfwEntry(
            file_type=record["file_type"],
            type_name=FILE_TYPES.get(record["file_type"], "unknown"),
            index=record["index"],
            stored_crc16=record["stored_crc"],
            calculated_crc16=crc16(payload),
            version=record["version"],
            address=record["address"],
            length=record["length"],
            aligned_length=record["aligned_length"],
            encrypted_offset=record["encrypted_offset"],
            encrypted_length=record["encrypted_length"],
            name=record["name"],
        )

    def to_dict(self, source: Path | None = None) -> dict[str, Any]:
        result: dict[str, Any] = {
            "size": len(self.blob),
            "sha256": hashlib.sha256(self.blob).hexdigest(),
            "header": asdict(self.header)
            | {
                "crc_ok": self.header.crc_ok,
                "directory_crc_ok": self.header.directory_crc_ok,
            },
            "chip_key": f"0x{self.chip_key:04x}",
            "entries": [
                asdict(entry) | {"crc_ok": entry.crc_ok} for entry in self.entries
            ],
        }
        if source is not None:
            result["source"] = str(source)
        return result

    def extract(self, destination: Path) -> list[Path]:
        destination.mkdir(parents=True, exist_ok=True)
        written: list[Path] = []
        for record in self._entry_records:
            safe_name = Path(record["name"]).name or f"entry-{record['index']:03d}.bin"
            path = destination / f"{record['index']:03d}-{safe_name}"
            path.write_bytes(self.payload_for_record(record))
            written.append(path)
        manifest = destination / "manifest.json"
        manifest.write_text(json.dumps(self.to_dict(), indent=2) + "\n")
        written.append(manifest)
        return written


def _hex16(value: int) -> str:
    return f"0x{value:04x}"


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("firmware", type=Path)
    parser.add_argument("--extract", type=Path, metavar="DIRECTORY")
    parser.add_argument("--json", action="store_true")
    args = parser.parse_args()

    container = UfwContainer(args.firmware.read_bytes())
    if args.extract:
        container.extract(args.extract)

    if args.json:
        print(json.dumps(container.to_dict(args.firmware), indent=2))
        return

    header = container.header
    print(f"file: {args.firmware}")
    print(f"size: {len(container.blob)} bytes")
    print(f"chip: {header.chip_name}")
    print(f"container version: {header.version}")
    print(f"chip key: {_hex16(container.chip_key)}")
    print(f"header CRC16: {_hex16(header.stored_crc16)} (ok)")
    print(f"directory CRC16: {_hex16(header.stored_directory_crc16)} (ok)")
    print("entries:")
    for entry in container.entries:
        encrypted = (
            f" encrypted={entry.encrypted_offset:#x}+{entry.encrypted_length:#x}"
            if entry.encrypted_length
            else ""
        )
        status = "ok" if entry.crc_ok else "MISMATCH"
        print(
            f"  {entry.index:3d} type={entry.file_type:#04x} "
            f"addr={entry.address:#x} len={entry.length:#x} "
            f"crc={_hex16(entry.stored_crc16)}:{status}{encrypted} {entry.name}"
        )
    if args.extract:
        print(f"extracted: {args.extract}")


if __name__ == "__main__":
    main()
