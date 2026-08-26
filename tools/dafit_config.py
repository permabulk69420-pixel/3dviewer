#!/usr/bin/env python3
"""Decrypt and inspect the device database embedded in current Da Fit APKs.

Da Fit 2.9.19 stores the database as ``assets/config.txt``.  The APK code
Base64-decodes that file, then decrypts it with 3-key Triple DES in CBC mode
and PKCS#5/PKCS#7 padding.  The key construction and IV below are literal
reconstructions of the current Android code, not guesses.

This utility performs no BLE operations and sends no network requests.
"""

from __future__ import annotations

import argparse
import base64
import hashlib
import json
from pathlib import Path
from typing import Any


DAFIT_2919_KEY = b"CBCDEFGHIJKLMNOPQRSTUVWX"
DAFIT_2919_IV = b"20160808"


class DaFitConfigError(ValueError):
    """Raised when an input is not a valid Da Fit configuration database."""


def _triple_des_algorithm(key: bytes):
    try:
        from cryptography.hazmat.decrepit.ciphers.algorithms import TripleDES
    except ImportError:  # cryptography < 43
        from cryptography.hazmat.primitives.ciphers.algorithms import TripleDES
    return TripleDES(key)


def decrypt_config_text(encoded: bytes | str) -> bytes:
    """Return the unpadded JSON bytes from an encrypted ``config.txt``."""
    try:
        from cryptography.hazmat.primitives import padding
        from cryptography.hazmat.primitives.ciphers import Cipher, modes
    except ImportError as exc:  # pragma: no cover - depends on host setup
        raise DaFitConfigError(
            "the 'cryptography' package is required to decrypt config.txt"
        ) from exc

    if isinstance(encoded, str):
        encoded = encoded.encode("ascii")
    try:
        ciphertext = base64.b64decode(b"".join(encoded.split()), validate=True)
    except (ValueError, base64.binascii.Error) as exc:
        raise DaFitConfigError("config.txt is not valid Base64") from exc

    if not ciphertext or len(ciphertext) % 8:
        raise DaFitConfigError("decoded ciphertext is not a non-empty DES block stream")

    decryptor = Cipher(
        _triple_des_algorithm(DAFIT_2919_KEY), modes.CBC(DAFIT_2919_IV)
    ).decryptor()
    padded = decryptor.update(ciphertext) + decryptor.finalize()
    unpadder = padding.PKCS7(64).unpadder()
    try:
        plaintext = unpadder.update(padded) + unpadder.finalize()
        parsed = json.loads(plaintext)
    except (ValueError, json.JSONDecodeError) as exc:
        raise DaFitConfigError("Triple-DES output is not padded JSON") from exc
    if not isinstance(parsed, dict) or not isinstance(parsed.get("list"), list):
        raise DaFitConfigError("JSON does not contain the expected top-level list")
    return plaintext


def load_config(path: Path) -> tuple[dict[str, Any], bytes, bool]:
    """Load plaintext JSON or decrypt an encrypted Da Fit config file."""
    source = path.read_bytes()
    stripped = source.lstrip()
    encrypted = not stripped.startswith(b"{")
    plaintext = decrypt_config_text(source) if encrypted else source
    try:
        config = json.loads(plaintext)
    except json.JSONDecodeError as exc:
        raise DaFitConfigError("input is not valid JSON") from exc
    if not isinstance(config, dict) or not isinstance(config.get("list"), list):
        raise DaFitConfigError("JSON does not contain the expected top-level list")
    return config, plaintext, encrypted


def select_records(
    config: dict[str, Any], *, record_id: int | None, name: str | None,
    version: str | None
) -> list[dict[str, Any]]:
    """Select exact IDs/versions and case-insensitive exact device names."""
    records = config["list"]
    selected = []
    for record in records:
        if record_id is not None and record.get("id") != record_id:
            continue
        if name is not None and str(record.get("name", "")).casefold() != name.casefold():
            continue
        if version is not None and record.get("version") != version:
            continue
        selected.append(record)
    return selected


def summary(config: dict[str, Any], plaintext: bytes, encrypted: bool) -> dict[str, Any]:
    return {
        "code": config.get("code"),
        "database_version": config.get("v"),
        "base_url": config.get("url"),
        "language_version": config.get("lang"),
        "record_count": len(config["list"]),
        "deleted_count": len(config.get("deleted", [])),
        "plaintext_size": len(plaintext),
        "plaintext_sha256": hashlib.sha256(plaintext).hexdigest(),
        "input_was_encrypted": encrypted,
    }


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("config", type=Path, help="encrypted config.txt or plaintext JSON")
    parser.add_argument("--id", type=int, dest="record_id")
    parser.add_argument("--name", help="case-insensitive exact device name")
    parser.add_argument("--version", help="exact three-character scan/config version")
    parser.add_argument("--output", type=Path, help="write normalized plaintext JSON")
    parser.add_argument("--summary", action="store_true", help="print database metadata")
    args = parser.parse_args()

    config, plaintext, encrypted = load_config(args.config)
    if args.output:
        args.output.write_text(
            json.dumps(config, ensure_ascii=False, indent=2, sort_keys=True) + "\n",
            encoding="utf-8",
        )

    filters_used = any(
        value is not None for value in (args.record_id, args.name, args.version)
    )
    result: Any
    if filters_used:
        result = select_records(
            config,
            record_id=args.record_id,
            name=args.name,
            version=args.version,
        )
    elif args.summary or not args.output:
        result = summary(config, plaintext, encrypted)
    else:
        return
    print(json.dumps(result, ensure_ascii=False, indent=2, sort_keys=True))


if __name__ == "__main__":
    main()
