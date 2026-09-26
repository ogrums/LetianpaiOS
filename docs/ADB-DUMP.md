# Dump ADB 2026-09-26_1502 (sans root)

Device `2006014140527`. Build **user** `Letianpai_Robot-user 11 / 616` (16 dec 2024). `ro.secure=1` `ro.adb.secure=1` → `adb root` refusé (normal). Bootloader `flash.locked=0`, verifiedboot **orange**, SELinux permissive, `ro.debuggable=1`.

`prefs-grep` vide : pas d’accès `/data/data`.

## Identité

| | |
|---|---|
| SN HTTP | `2006014140527` (= `ro.serialno`) |
| hardcode | `persist.sys.hardcode=YIbUOYOg98Nc4A` |
| langue | `persist.sys.region.language=en` → host **global** |
| MQTT clientId | `l81_9aa8495f6c9ddf95920428e3c2352d4c` |

## Cloud vu dans logcat

HTTP : `https://global-robot-api.letianpai.com/robot_api/v1/common/getConfig?sn=…&config_key=deep_sleep_config_en`

OTA CloudFront :
- `https://d4owc89rbj03p.cloudfront.net/ota/geeui_1.2.12162.u/update.zip`
- `https://d4owc89rbj03p.cloudfront.net/ota/mcu/1.1.31/Project.hex`

MQTT : **déjà connecté au broker officiel** (Paho ping 60 s OK). Topic dump `#` encore impossible tant que le triplet pointe le cloud.

## UART

`/dev/ttyS5` `crw-rw-rw- system` — candidat MCU. `/dev/ttyS1` bluetooth.

## Services up (uid 1000)

EmqxService, DispatchService, GeeUISensorsService, LTPAudioService, GeeUpdateService, desktop, appstore, expression, launcher `com.renhejia.robot.launcher`.
AIDL `letianpai: ILeTianPaiService`.

## Root plus tard

Pas besoin de reflasher toute la ROM pour commencer : Magisk sur boot.img (orange + locked=0). En attendant : `adb pull /system/app/EmqxService/EmqxService.apk` marche sans root.
