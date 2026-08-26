# Current Da Fit device database and updater analysis

This directory records offline reverse engineering of the current Da Fit
2.9.19 Android package. No watch interaction is needed to reproduce this work.
The large APK, DEX files and decrypted 12 MB database are deliberately not
committed; their hashes are in `artifact-hashes.sha256`.

## Embedded database encryption

The loader `com.android.mltcode.paycertificationapi.qg5` reads
`assets/config.txt` and passes it to `mf2.decrypt`. The current obfuscated
implementation resolves to this exact pipeline:

1. Android Base64 decode.
2. Triple DES (`DESede/CBC/PKCS5Padding`).
3. IV: ASCII `20160808` from the `des_iv` resource.
4. Key seed: ASCII `CB` from `des_key`.
5. The app appends characters with values 67 through 88 inclusive.
6. Effective 24-byte key: ASCII `CBCDEFGHIJKLMNOPQRSTUVWX`.

`tools/dafit_config.py` reproduces the operation. For the extracted current
asset it yields:

| Field | Value |
|---|---|
| Plaintext size | 12,244,440 bytes |
| Plaintext SHA-256 | `5daab83592b88dfb13213032db9507f67e1367c63824b1a34a6b20e38b77f8c6` |
| Database version `v` | 12908 |
| Records | 14,551 |
| Deleted IDs | 235 |
| Base asset URL | `https://qcdn.moyoung.com/` |
| Language version | 62 |

Reproduction:

```sh
python3 tools/dafit_config.py assets/config.txt --summary
python3 tools/dafit_config.py assets/config.txt --name Anko43568185
```

## Exact target records

The database contains two records named `Anko43568185`. Their complete values
are committed in `target-band-config.json`.

| ID | Scan/config version | Chip label | MCU | PID | LCM | Shape |
|---:|---|---|---:|---:|---:|---:|
| 11057 | `8H6` | `JLI` | 344 | 21108 | 62 | 3 |
| 13220 | `9GW` | `JLI` | 344 | 21108 | 62 | 3 |

Apart from `id`, `version` and `updated_at`, the two records are byte-for-byte
equivalent as normalized JSON. They have the same name, logo, seven screens,
screen configuration, languages and every feature flag. Across all 14,551
records they are the only two records that match all 27 non-identity fields.

The installed OTA version is `MOY-8H62-2.0.1`. The app's
`BandFirmwareUtils.getFirmwareType` removes the final character of the middle
component before performing the band-config lookup. Thus `8H62` maps to the
database key `8H6`. Independently, `CRPScanRecordParser`/`dw7` extracts three
ASCII firmware-type bytes from advertisement service data. The advertisement
then contains separate platform and chip-ID bytes. The semantic purpose of the
fourth `8H62` character is not named by the inspected code and is not asserted
here.

## Closest white-label records

Ignoring identity, logo and screen assets, 13 records have the same 24
hardware/feature fields as the installed-generation record:

| Name | Config version | MCU | PID | LCM |
|---|---|---:|---:|---:|
| `ZL94B` | `B6K` | 344 | 21108 | 62 |
| `Anko43568185` | `9GW` | 344 | 21108 | 62 |
| `HZ-101PRO` | `9C0` | 344 | 21108 | 62 |
| `Swiss Code Sq 3` | `8ZU` | 344 | 21108 | 62 |
| `Anko43568185` | `8H6` | 344 | 21108 | 62 |
| `Swiss Code Sq 3` | `863` | 344 | 21108 | 62 |
| `ZL93J` | `82E` | 344 | 21108 | 62 |
| `DEVIA-ZL94` | `7QY` | 344 | 21108 | 62 |
| `Z149J` | `JTX` | 344 | 21108 | 62 |
| `DEVIA-ZL94` | `JLB` | 344 | 21108 | 62 |
| `ZL94JW` | `VGH` | 344 | 21108 | 62 |
| `PLAYFIT SW97` | `V6R` | 344 | 21108 | 62 |
| `Z101J` | `V6Q` | 344 | 21108 | 62 |

Only the two Anko records also have exactly the same seven screen assets.

## Current updater network path

The current package still uses only these firmware-check endpoints:

* `https://api.moyoung.com/v2/upgrade/factory`
* `https://api.moyoung.com/v2/upgrade/beta`

The request remains HTTP GET with `version`, `mac`, and hard-coded
`app_version=1.0.2`. A successful response contains a direct package URL and
MD5. `FirmwareUpgradePresenter` downloads the URL and verifies the file MD5.

The apparent `Currently the latest version` response is not a reliable test of
whether a package exists. The backend behaves like a table of exact upgrade
edges. A public, independently downloadable JL701N example demonstrated:

| Supplied version | Result |
|---|---|
| `MOY-VSW4-2.0.0` | Returns `MOY-VSW4-2.0.1` and its `.ufw` URL |
| `MOY-VSW4-1.9.9` | `Currently the latest version` |
| `MOY-VSW4-1.0.0` | `Currently the latest version` |
| `MOY-VSW4-0.0.0` | `Currently the latest version` |

Therefore arbitrary lower versions do not retrieve the current package; only
an exact registered predecessor does. Tests using a synthetic address found no
registered edge for `MOY-8H62-2.0.0` on factory or beta. The installed
`MOY-8H62-2.0.1` also returns no URL. Previously tested `1.0.x` values did not
return a URL either. No private watch address is stored in this repository.

The app's `BandFirmwareRestoreFragment` does not expose a separate recovery
package service. It reads a previously stored failed-upgrade version and calls
the same `FirmwareUpgradePresenter.checkFirmwareVersion` endpoint. The other
`RestoreFirmwareController` flow is specific to Goodix boot recovery.

## Public archive search state

Exact public searches for `MOY-8H62`, `JLQFNHID1.3`, `Anko43568185` and
`JLR-79411` found product/manual material but no firmware URL. Common Crawl and
Wayback CDX queries contained no captured MoYoung firmware objects. URLScan's
public index listed 33 scans referencing `qcdn.moyoung.com`; no `.ufw` page URL
was indexed. Two direct `.bin` objects were recovered (145,504 and 484,438
bytes), but neither has a JieLi UFW header or identifying `MOY-*` strings and
both have watchface/data-like layouts. They are not identified as firmware.

## Still not recovered

The exact `MOY-8H62-2.0.1` package URL and UFW image remain unknown. The current
database contains asset paths only; it has no `.ufw`, firmware, upgrade or
`/files/` field. The target's exact JieLi SoC is also not established by the
database's `mcu=344` value.
