# Public MoYoung/JieLi firmware samples

These packages are public vendor/community examples. Neither is asserted to be
compatible with the Anko target and neither has been sent to it. They are used
to validate the UFW/JLFS tooling and map Da Fit metadata to firmware evidence.

## iTECH Active 3 / V373 sample

Public vendor URL:

`https://p.moyoung.com/uploads/jieli/V37_QIpBxMKILvdR5SYRGAhsUVx3CTAxdIxV/fZaBQ5zVwUMPiM4mxHBWBEGe6XzeC5P6.ufw`

| Property | Value |
|---|---|
| Size | 1,032,416 bytes |
| SHA-256 | `d66143816817e897fcff5a346976676599710fa9a5ae694bd984ba897ca156ad` |
| UFW container version | 4 |
| UFW chip name | `JL701N` |
| Payload/chip key | `0x1607` |
| UFW entries | 9 |
| Flash payload | 1,015,808 bytes |
| JLFS entry point | `0x06000120` |
| Extracted `app.bin` | 930,236 bytes |
| OTA identity in app | `MOY-V373-2.0.3` |
| Firmware revision in app | `JLQFNGUO` |
| Product identity in app | `iTECH Active 3` |

The current Da Fit config record for `iTECH Active 3` has scan/config version
`V37`, `chip=JLI`, `mcu=323`, `pid=10131`, and `lcm=30`.

## Zeblaze GTS 3 / VSW4 sample

The URL was published in `kagaimiq/jielie` issue 7 and is also returned by the
official factory API when queried with the exact registered predecessor
`MOY-VSW4-2.0.0`:

`https://qcdn.moyoung.com/files/DHdJG80gtjagB3ezS9WoHyN81jdeddtN.ufw`

| Property | Value |
|---|---|
| Size | 1,261,792 bytes |
| API MD5 | `459ffddba59d8a91cafbb7d9507dfceb` |
| SHA-256 | `7bb89f67dcc0b99f6b0548a915c81c3cde09e687f5eb7b3a124fd216b6ea254a` |
| UFW container version | 4 |
| UFW chip name | `JL701N` |
| Payload/chip key | `0x1607` |
| UFW entries | 9 |
| Flash payload | 1,245,184 bytes |
| JLFS entry point | `0x06000120` |
| Extracted `app.bin` | 1,083,028 bytes |
| OTA identity in app | `MOY-VSW4-2.0.1` |
| Firmware revision in app | `JLQFNGXJ` |
| Product identity in app | `GTS 3` |
| SDK build string | `JL701N_V211-@20230627-$4654c13` |

The current Da Fit config record for `GTS 3` / `VSW` has `chip=JLI`,
`mcu=339`, `pid=21108`, and `lcm=55`.

## What the two samples establish

Both packages pass their UFW header CRC, raw-directory CRC and every entry
payload CRC. Both use the same container structure, `JL701N` chip name,
`0x1607` key and `0x06000120` application entry point. Their plaintext
application payloads expose Pi32v2/JieLi SDK strings and MoYoung update code.

The Da Fit database values `mcu=323` and `mcu=339` both correspond to confirmed
`JL701N` package samples. Therefore the database `mcu` field is not a unique
JieLi SoC model number. In particular, target value `mcu=344` cannot be mapped
to an exact SoC solely by treating 344 as a chip identifier.

`tools/jl_ufw.py` is a dependency-free implementation of the outer UFW parser,
CD03 cipher, tail-key recovery, payload block decryption, checksums and
extraction. It was reconstructed from the public JieLi iOS `JL_OTALib` logic.

```sh
python3 tools/jl_ufw.py firmware.ufw --json
python3 tools/jl_ufw.py firmware.ufw --extract extracted
```
