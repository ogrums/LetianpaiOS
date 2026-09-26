# App mobile LetianPai 1.2.2406051

APK `LeTianPai_1.2.2406051_APKPure.apk` (Flutter, package `com.example.letianpai_l81robot_app`).
Analyse : `libapp.so` AOT + dex natif (Matter/CHIP, WeChat, TRTC Tencent).

## MQTT : l’app n’en parle pas

Aucun client Paho/EMQX, aucun topic `cmd/L81`. Le téléphone fait du **HTTP** vers le cloud ; le cloud **publie MQTT** vers le robot (vu dans `mqtt-rux.pcap`).

Chaîne :

```
App  --HTTPS mini_api-->  robot-api / global-robot-api
                              |
                              v MQTT qos1
                         cmd/L81/<clientId>/<cmd>/<ts>
```

Champ Dart `iotUrl` / `getIotUrl` = URL API, pas le broker 1883.

## Hosts

| Rôle | URL |
|---|---|
| API CN | `https://robot-api.letianpai.com` |
| API global | `https://global-robot-api.letianpai.com` |
| Compte | `https://account.letianpai.com` / `https://global-account.letianpai.com` |
| CDN | `cdn.file.letianpai.com` + CloudFront `d4owc89rbj03p` |
| WeChat login | `/wx_api/v1/user/appLogin` |

## Contrôle (ce qui devient du MQTT)

Prefixe `https://<host>/mini_api/v1/`

| Path | Effet fil MQTT probable |
|---|---|
| `device/control/motion` | `controlMotion` |
| `device/control/sendWord` | `controlSendWord` |
| `device/control/sendDemoCmd` | `speechDance` / demo |
| `device/control/getPostureList` | **liste officielle des number** |
| `device/control/getDemoCmdList` | liste demo |
| `device/control/changeSoundVolume` | volume |
| `device/control/changeBrightnessVolume` | luminosité |
| `device/mode/change` | `changeMode` |
| `device/module/change` | `changeShowModule` |
| `device/remote/msgPush` | `deviceRemoteMsgPush` |
| `device/common/msgPush` | push générique |

Appeler `getPostureList` avec un token session (Charles / PolarProxy sur le téléphone) donnerait tous les `number` manquants (1–4, 7…).

## Reste mini_api (config écran, CN à ignorer)

clock, weather, stock, news, fans, tomato, meditation, anniversary, photo, video, remind, sleepMode, awake, wifi, ota, share, mijia, trtc, mine/*, app/*.
L730 = autre SKU (`/mini_api/l730/...`).

## Native hors Flutter

Matter `libCHIPController.so`, TRTC `libliteavsdk.so`, Huawei GRS, Firebase Messaging. Pas nécessaire pour l’offline locomotion.
