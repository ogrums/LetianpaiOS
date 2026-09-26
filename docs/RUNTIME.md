# Runtime robot 2026-09-26 (logs + APK pull)

Sources locales : `dump_robot/EmqxService.apk` (28 Mo, ROM live), `LTPService.apk` (9 Mo), logs `emqxservice/` `taskservice/` `lex_log/`.

## MQTT (confirmé log INFO Emqx)

```
GET https://global-robot-api.letianpai.com/robot_api/v1/bind/getIotTriplet?sn=2006014140527
header country: global

remoteHost=43.153.69.45  remotePort=1883
Connected tcp://43.153.69.45:1883
subscribe cmd/L81/<clientId>/+/+
clientId = l81_9aa8495f6c9ddf95920428e3c2352d4c
```

Le 3e segment n’est **pas** le SN série : c’est le `clientId` du triplet.

Mock : publier sur `cmd/L81/l81_9aa8495f6c9ddf95920428e3c2352d4c/cmd/x` une fois le robot pointé sur le LAN (ou DNAT 43.153.69.45 → PC).

## TaskService

Vu surtout `changeShowModule` (desktop / Magisk / setting shortcut). Magisk (`com.topjohnwu.magisk`) est installé → `adb shell su` plutôt que `adb root`.

## Lex

Enregistre PCM local, AIDL OK, `dialog_language=fr-FR` déjà côté cloud. TTS commande `close_speech_audio`. Clés API cloud présentes dans le DEBUG — ne pas committer.
