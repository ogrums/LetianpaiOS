# Vision RUX / GeeUI (ogrums)

Date : 2026-09-26
Statut : cadrage — pas encore de changement runtime.
Robot : Letianpai RUX, Android 11 + Debian 10, MCU GD32L233RC, bipède bureau.

## Objectif

Reprendre l’ensemble des forks `ogrums/*`, analyser chaque module, garder une carte globale, puis livrer des améliorations en PR testables sur robot (ADB + ROM root).

Docs détaillés : [AIDL-CONTRACTS.md](AIDL-CONTRACTS.md) · [MCU-VOCAB.md](MCU-VOCAB.md) · [MOCK-STACK.md](MOCK-STACK.md).

## Repos pris en compte

| Repo | Rôle | Couche |
|---|---|---|
| LetianpaiOS | Launcher HOME + AudioService (SFX) | Shell |
| GeeUIDesktop | Entrée apps / bureau | UI |
| GeeUIFace | Visage / modes robot | UI |
| GeeUITime | Horloge | UI |
| GeeUIGuide | QR / premier boot | UI |
| GeeUISetting | Réglages | UI |
| GeeUIWiFiConnector | Wi-Fi | UI |
| GeeUIInstaller | Installation APK | Système |
| GeeUIBase | Lib de base | Shared |
| GeeUIComponents | Widgets / canal comm (nom officiel : GeeUIComponets) | Shared |
| LetianpaiService | Bus AIDL inter-apps | Bus |
| GeeUITaskService | Orchestration tâches | Bus |
| GeeUIMcuService | UART / AT / marche / oreilles | Motion |
| MCU | Firmware GD32 + PDF protocole SOC↔MCU | Motion |
| GeeUIAIAudioService | Assistant vocal | IA |
| LtpNetWork | Client HTTP cloud | Cloud |
| third_party_demo | Démo LLM + mock HTTP :8080 | Cloud / IA |
| DemoForRobotSDK | SDK Kotlin | SDK |
| GeeUI_ROM | Image / docs ROM | OS |

LetianpaiOS compile le launcher et les SFX. Il ne compile pas le MCU ni le bus AIDL. `RobotService` démarre des packages externes (MCU, task, EMQX, speech).

## Architecture runtime

```
[App téléphone]     [HTTP mock :8080]  [MQTT :1883]  [LLM :8012]
        |                    |                 |            |
        +--------------------+--------+--------+------------+
                                  |
                           GeeUI Android 11
                                  |
                         LetianpaiService (AIDL)
                                  |
                    GeeUIMcuService → AT → GD32
```

## Choix git

Rester **multi-repos**. Détail dans la version précédente de ce fichier : pas de monorepo ROM+MCU+apps.

## Backlog (priorisé)

1. Contrats AIDL — fait, voir AIDL-CONTRACTS.md.
2. Vocabulaire MCU / AT — fait, voir MCU-VOCAB.md.
3. **Mock HTTP** : pointer `Constants.kt` vers `third_party_demo/mock` :8080 ; compléter les 404 au boot.
4. **Broker MQTT** : Mosquitto :1883 aligné sur `getIotTriplet` ; dump des topics ; scénarios `controlMotion`.
5. Boot launcher (délais 200 ms / 1 s, liste d’apps).
6. Locomotion : réactiver `AT+MOVEW` ou fiabiliser `AT+MOTORW` ; une seule queue UART.
7. IA / speech éclaté.
8. Toolchain AGP 8 sur les forks restants.
9. Tests CommandLib + parseurs AT + mock contract tests.
10. `sharedUserId` release vs debug.

## Méthode

1. Vision + docs contrat.
2. Un repo à la fois.
3. PR sur le fork.
4. Validation robot (ADB) **et** mock HTTP/MQTT avant de dépendre du cloud officiel.
