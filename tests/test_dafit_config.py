import sys
import unittest
from pathlib import Path


sys.path.insert(0, str(Path(__file__).resolve().parents[1] / "tools"))

from dafit_config import decrypt_config_text, select_records  # noqa: E402


class DaFitConfigTests(unittest.TestCase):
    def test_known_2919_ciphertext(self) -> None:
        encoded = b"oV+zQiTFORap0o2Zw4Wqq43BQVwNvKiV"
        self.assertEqual(decrypt_config_text(encoded), b'{"code":0,"list":[]}\n')

    def test_exact_record_filters(self) -> None:
        config = {
            "list": [
                {"id": 1, "name": "Anko43568185", "version": "8H6"},
                {"id": 2, "name": "Anko43568185", "version": "9GW"},
                {"id": 3, "name": "Other", "version": "8H6"},
            ]
        }
        matches = select_records(
            config, record_id=None, name="anko43568185", version="8H6"
        )
        self.assertEqual(matches, [config["list"][0]])


if __name__ == "__main__":
    unittest.main()
