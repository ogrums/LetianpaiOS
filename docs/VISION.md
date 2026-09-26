# Vision RUX / GeeUI (ogrums)

Date : 2026-09-26
Statut : cadrage + mocks locaux. Pas encore de changement runtime robot.
Robot : Letianpai RUX, Android 11 + Debian 10, MCU GD32L233RC.

État détaillé : [STATUS.md](STATUS.md).
Docs : [AIDL-CONTRACTS.md](AIDL-CONTRACTS.md) · [MCU-VOCAB.md](MCU-VOCAB.md) · [EMQX-APK.md](EMQX-APK.md) · [MOCK-STACK.md](MOCK-STACK.md).

## Objectif

Reprendre les forks `ogrums/*`, analyser chaque module, livrer des PR testables (ADB + ROM root) contre un mock local avant le cloud officiel.

## Choix git

Rester **multi-repos**. Pas de monorepo ROM+MCU+apps.

## Backlog

1. AIDL — fait.
2. Vocab MCU — fait.
3. Mock HTTP `third_party_demo/mock` :8080 — fait (pointage `Constants.kt` encore à faire).
4. Mock MQTT Mosquitto :1883 + `pub.sh` `cmd/L81/…` — fait (connexion robot encore à faire).
5. Boot launcher.
6. Locomotion AT.
7. DispatchService.
8. IA / speech.
9. Toolchain AGP 8.
10. Tests.
11. `sharedUserId`.
