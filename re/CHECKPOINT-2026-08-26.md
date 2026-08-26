# Firmware reverse-engineering checkpoint — 2026-08-26

This file is a factual resume point for the Anko 43568185 firmware work. The
checkpoint branch is `firmware-re-checkpoint-20260826`, based on repository
commit `6972eca`.

## Target identity

| Field | Observed value |
|---|---|
| Product | Anko 43568185 / JLR-79411 |
| BLE name | `Anko43568185` |
| OTA firmware identity | `MOY-8H62-2.0.1` |
| Firmware revision | `JLQFNHID1.3` |
| Manufacturer string | `MOYOUNG-V2` |
| Platform evidence | JieLi |

The watch remains functional. Physical access and additional watch/BLE probing
are outside the current work. No private watch address is stored in this
repository.

## Persisted results

- `re/dafit_current/README.md` records the current Da Fit 2.9.19 database and
  firmware-updater analysis.
- `re/dafit_current/target-band-config.json` contains both complete current-app
  records for `Anko43568185`.
- `tools/dafit_config.py` decrypts current Da Fit `assets/config.txt` files and
  selects device records without using a watch or network connection.
- `re/jieli_samples/README.md` records two independently obtained, validated
  MoYoung/JieLi UFW samples and their matching Da Fit metadata.
- `re/jieli_samples/*-manifest.json` contains machine-readable UFW manifests.
- `tools/jl_ufw.py` validates and extracts the outer JieLi UFW container.
- `tools/ghidra/DumpTargetRefs.java` is a Ghidra helper for enumerating direct
  references to selected addresses.
- `tests/` contains deterministic tests for the Da Fit decryptor and UFW parser.

## Established target metadata

The current Da Fit database contains two `Anko43568185` records. They use scan
keys `8H6` and `9GW`; both specify `chip=JLI`, `mcu=344`, `pid=21108`, `lcm=62`
and `shape=3`. Except for record identity, scan key and update timestamp, their
normalized values match. The app maps installed OTA component `8H62` to scan
key `8H6` by removing its final character.

The current app still checks only the MoYoung `/v2/upgrade/factory` and
`/v2/upgrade/beta` endpoints with `version`, `mac` and hard-coded
`app_version=1.0.2`. Testing with synthetic addresses found no package response
for the installed version, `MOY-8H62-2.0.0`, or the tested `1.0.x` values.

The server was independently shown to require an exact registered predecessor:
`MOY-VSW4-2.0.0` returns the VSW4 2.0.1 UFW, while arbitrary lower VSW4 version
strings return `Currently the latest version`.

## Firmware evidence

Two public packages were validated and extracted:

| Identity | DB MCU | Container chip | Application entry |
|---|---:|---|---:|
| `MOY-V373-2.0.3` | 323 | `JL701N` | `0x06000120` |
| `MOY-VSW4-2.0.1` | 339 | `JL701N` | `0x06000120` |

Both pass their UFW header, directory and entry CRC checks. Because distinct Da
Fit MCU values 323 and 339 both map to confirmed `JL701N` packages, database
value `mcu=344` does not by itself identify the target SoC.

## Current unknown boundary

The exact `MOY-8H62-2.0.1` URL and firmware image have not been recovered. No
public search, indexed archive, current-app asset, or tested firmware-server
request has yielded that target package. Consequently its exact SoC, flash
layout, executable image and OTA authentication behavior remain unverified.

## Reproduction checks

```sh
python3 -m unittest discover -s tests -v
python3 -m py_compile tools/dafit_config.py tools/jl_ufw.py
python3 tools/dafit_config.py path/to/assets/config.txt --summary
python3 tools/jl_ufw.py path/to/firmware.ufw --json
```
