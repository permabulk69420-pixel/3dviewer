import sys
import unittest
from pathlib import Path


sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "tools"))

from jl_ufw import cd03_xor, crc16, derive_tail_key  # noqa: E402


class JieLiUfwTests(unittest.TestCase):
    def test_crc16_standard_vector(self) -> None:
        self.assertEqual(crc16(b"123456789"), 0x31C3)

    def test_cd03_decodes_known_moyoung_header(self) -> None:
        encoded = bytes.fromhex(
            "0b 9d b7 98 ff fe 73 f8 f9 c1 a7 67 ce bf 5b 97 "
            "45 52 0b 48 e0 cd 27 4e 9c 19 13 26 4c 98 30 60 "
            "c0 a1 42 84 08 31 43 86 0c 18 30 60 e1 c2 84 08 "
            "10 20 40 80 00 21 63 e7 ce bd 5b 97 0f 3f 5f 9f"
        )
        decoded = cd03_xor(encoded)
        self.assertEqual(decoded[:16].hex(), "f4422887e0c00f000900040000020000")
        self.assertEqual(decoded[16:22], b"JL701N")
        self.assertEqual(crc16(decoded[2:]), 0x42F4)

    def test_cd03_is_symmetric(self) -> None:
        plain = bytes(range(80))
        self.assertEqual(cd03_xor(cd03_xor(plain)), plain)

    def test_known_tail_key(self) -> None:
        tail_key_data = bytes.fromhex(
            "20 34 13 d6 59 97 fa 2e 6f 41 76 89 28 f0 1e b8 "
            "1c 4b b5 da 24 a5 8a 86 9e f9 94 a7 57 a3 f8 98"
        )
        self.assertEqual(derive_tail_key(tail_key_data), 0x1607)


if __name__ == "__main__":
    unittest.main()
