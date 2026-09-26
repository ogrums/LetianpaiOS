# Statut RUX / GeeUI — 2026-09-26

Méthode : vision → analyse → décision → PR. Pas encore de changement runtime flashé.
Drive : stockage à optimiser plus tard. frameworks/hardware : upload stoppé pour cette phase.

## Fait

| Étape | Livrable |
|---|---|
| Vision + multi-repos | [VISION.md](VISION.md) |
| AIDL locaux | [AIDL-CONTRACTS.md](AIDL-CONTRACTS.md) |
| MCU / AT | [MCU-VOCAB.md](MCU-VOCAB.md) |
| JSON commandes | [JSON-COMMANDS.md](JSON-COMMANDS.md) |
| EmqxService APK (artifacts) | [EMQX-APK.md](EMQX-APK.md) |
| Mock HTTP :8080 + MQTT :1883 | `third_party_demo/mock` |

## En cours (vision / mocks / AIDL / MQTT)

1. Pointer `LtpNetWork` `Constants.kt` vers le mock LAN.
2. Boot robot + lister HTTP 404.
3. EmqxService → Mosquitto local ; `mosquitto_sub -t '#'`.
4. Comparer EmqxService.apk Drive (`packages/apps/EmqxService`, 7.5 Mo) vs artifacts.
5. DispatchService routage `setLongConnectCommand`.

## Todo APK (Drive, pas dans les forks ou fermés)

Priorité **haute** — pas de source fork, ou divergence ROM :

| APK | ~taille | Pourquoi |
|---|---|---|
| `IdentService.apk` | 77 Mo | `setIdentifyCmd` / visage, absent forks |
| `GeeUILex.apk` | 90 Mo | lexique / speech, absent forks |
| `LTPOtaService.apk` | 7.3 Mo | `otaUpdate` court-circuite AIDL |
| `MiIoT.apk` | 6 Mo | `startBindMijia` |
| `LTPAudioService.apk` | 8.7 Mo | comparer GeeUIAIAudioService |
| `LTPMcuService.apk` | 3 Mo | comparer fork GeeUIMcuService |
| `LTPService.apk` | 1.3 Mo | comparer LetianpaiService |
| `LTPLauncher.apk` | 29 Mo | boot / délais vs LetianpaiOS |

Priorité **moyenne** — widgets / vision :

| APK | Pourquoi |
|---|---|
| `GeeUICamera.apk` | caméra robot |
| `RechargeYOLO.apk` | 84 Mo, NPU / détection |
| `RKNNSSDApp.apk` | démo RKNN |
| `FactoryTest.apk` | banc usine servos / capteurs |
| `GeeUIMessage.apk` `GeeUICustom.apk` | pas dans la liste forks |
| `GeeUIRaceLight` `GeeUIMemorize` `GeeUIStock` `GeeUINews` `GeeUiFans` `GeeUiEventCountdown` `GeeUICommemoration` `GeeUiAlarm` | modules écran |

**Ignore** (RK TV/VR/player) : `RkVideoPlayer`, `RKVR*`, `ITVLauncher`, `RKTvLauncher`, `eHomeMediaCenter*`, `GoogleEmail`, `PinyinIME`.

Déjà vu : `EmqxService.apk` (Drive + artifacts).
Source fork prioritaire avant APK : GeeUIFace, GeeUISetting, GeeUITime, GeeUIWiFiConnector, GeeUITaskService.

## Ensuite

Launcher boot · locomotion AT · speech · toolchain · tests · ménage Drive.
