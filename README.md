# 3dviewer

Web Bluetooth tooling and reverse-engineering records for the Anko 43568185
smartwatch and its MoYoung/JieLi firmware ecosystem.

## Firmware reverse engineering

- [Current checkpoint](re/CHECKPOINT-2026-08-26.md)
- [Current Da Fit database and updater analysis](re/dafit_current/README.md)
- [Public MoYoung/JieLi firmware sample analysis](re/jieli_samples/README.md)
- [`config.txt` decryptor and device-record query tool](tools/dafit_config.py)
- [JieLi UFW parser and extractor](tools/jl_ufw.py)

The target firmware identity is `MOY-8H62-2.0.1`. Its exact downloadable UFW
package has not been recovered. No private watch address is stored here.
