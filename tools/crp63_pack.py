#!/usr/bin/env python3
"""Reference implementation of the MoYoung/CRP command-0x63 firmware transport.

This does NOT talk to a watch. It reproduces the byte stream Da Fit builds for
firmware transfer so candidate JieLi images can be validated offline.

Recovered behavior for the target's legacy CRP path:
  * start control frame: FE EA 20 09 63 + 4-byte big-endian file size
  * logical source chunks: 256 bytes for MOY-8H62-2.0.1
  * each bulk record: FE + CRC16_BE + one-byte payload length + raw payload
    (a 256-byte chunk stores length as 00, matching Java byte truncation)
  * CRC seed: 0xFEEA
  * whole-file CRC: same algorithm, continuous over the complete file
  * final success control frame: FE EA 20 09 63 00 00 00 00

The BLE layer subsequently fragments each bulk record to the negotiated ATT
write payload before writing FEE5; this utility intentionally stops one layer
above ATT fragmentation.
"""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
from typing import Iterable

CRC_SEED = 0xFEEA
DEFAULT_LOGICAL_CHUNK = 256


def crc16_crp(data: bytes, seed: int = CRC_SEED) -> int:
    """Literal port of com.crrepa.c0.b.a(byte[], int)."""
    crc = seed & 0xFFFF
    for value in data:
        n2 = ((((crc & 0xFF00) >> 8) | ((crc & 0x00FF) << 8)) ^ value) & 0xFFFF
        n3 = (n2 ^ ((n2 & 0xFF) >> 4)) & 0xFFFF
        n4 = (n3 ^ (((n3 & 0xFF) << 8) << 4)) & 0xFFFF
        crc = (n4 ^ (((n4 & 0xFF) << 4) << 1)) & 0xFFFF
    return crc


def control_frame(command: int, payload: bytes = b"") -> bytes:
    total = 5 + len(payload)
    if total > 0xFF:
        # u1.a() encodes extended length in byte 2, but command-0x63 control
        # messages used here are all short.
        mode = ((total >> 8) + 0x20) & 0xFF
    else:
        mode = 0x20
    return bytes((0xFE, 0xEA, mode, total & 0xFF, command & 0xFF)) + payload


def start_frame(file_size: int) -> bytes:
    if not 0 <= file_size <= 0xFFFFFFFF:
        raise ValueError("firmware size must fit in 32 bits")
    return control_frame(0x63, file_size.to_bytes(4, "big"))


def success_frame() -> bytes:
    return control_frame(0x63, b"\x00\x00\x00\x00")


def failure_frame() -> bytes:
    return control_frame(0x63, b"\xFF\xFF\xFF\xFF")


def bulk_record(payload: bytes, marker: int = 0xFE) -> bytes:
    """Build the logical FEE5 record for the normal (non-64-byte) path."""
    if not 0 < len(payload) <= 256:
        raise ValueError("payload must be 1..256 bytes")
    crc = crc16_crp(payload)
    return bytes((marker & 0xFF, (crc >> 8) & 0xFF, crc & 0xFF, len(payload) & 0xFF)) + payload


def iter_payload_chunks(blob: bytes, logical_chunk: int = DEFAULT_LOGICAL_CHUNK) -> Iterable[bytes]:
    if not 1 <= logical_chunk <= 256:
        raise ValueError("logical_chunk must be 1..256")
    for offset in range(0, len(blob), logical_chunk):
        yield blob[offset : offset + logical_chunk]


def iter_bulk_records(blob: bytes, logical_chunk: int = DEFAULT_LOGICAL_CHUNK) -> Iterable[bytes]:
    for chunk in iter_payload_chunks(blob, logical_chunk):
        yield bulk_record(chunk)


def att_fragments(record: bytes, att_payload: int) -> list[bytes]:
    if att_payload <= 0:
        raise ValueError("att_payload must be positive")
    return [record[i : i + att_payload] for i in range(0, len(record), att_payload)]


def hexline(data: bytes) -> str:
    return data.hex(" ")


def inspect(path: Path, logical_chunk: int, att_payload: int) -> dict:
    blob = path.read_bytes()
    chunks = list(iter_payload_chunks(blob, logical_chunk))
    first = bulk_record(chunks[0]) if chunks else b""
    last = bulk_record(chunks[-1]) if chunks else b""
    whole_crc = crc16_crp(blob)
    return {
        "file": path.name,
        "size": len(blob),
        "sha256": hashlib.sha256(blob).hexdigest(),
        "md5": hashlib.md5(blob).hexdigest(),
        "logical_chunk": logical_chunk,
        "logical_chunk_count": len(chunks),
        "whole_file_crc16": f"0x{whole_crc:04X}",
        "start_frame": hexline(start_frame(len(blob))),
        "success_frame": hexline(success_frame()),
        "failure_frame": hexline(failure_frame()),
        "first_bulk_record": hexline(first),
        "last_bulk_record": hexline(last),
        "first_record_att_fragments": [hexline(x) for x in att_fragments(first, att_payload)] if first else [],
        "last_record_att_fragments": [hexline(x) for x in att_fragments(last, att_payload)] if last else [],
        "att_payload": att_payload,
    }


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("firmware", type=Path)
    ap.add_argument("--logical-chunk", type=int, default=DEFAULT_LOGICAL_CHUNK)
    ap.add_argument("--att-payload", type=int, default=244)
    ap.add_argument("--json", action="store_true")
    args = ap.parse_args()

    result = inspect(args.firmware, args.logical_chunk, args.att_payload)
    if args.json:
        print(json.dumps(result, indent=2))
        return

    print(f"file: {result['file']}")
    print(f"size: {result['size']} bytes")
    print(f"sha256: {result['sha256']}")
    print(f"logical chunks: {result['logical_chunk_count']} x <= {result['logical_chunk']} bytes")
    print(f"whole-file CRC16: {result['whole_file_crc16']}")
    print(f"start: {result['start_frame']}")
    print(f"success: {result['success_frame']}")
    print(f"first bulk record: {result['first_bulk_record']}")
    print(f"last bulk record: {result['last_bulk_record']}")
    print(f"ATT payload used for preview: {result['att_payload']}")
    print("first record fragments:")
    for frag in result["first_record_att_fragments"]:
        print("  " + frag)


if __name__ == "__main__":
    main()
