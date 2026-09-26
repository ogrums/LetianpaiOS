# Statut RUX / GeeUI — 2026-09-26

Méthode : vision → analyse → décision → PR. Pas encore de changement runtime flashé.

## Fait

| Étape | Livrable |
|---|---|
| Carte repos + multi-repos | [VISION.md](VISION.md) |
| Contrats AIDL locaux | [AIDL-CONTRACTS.md](AIDL-CONTRACTS.md) |
| Vocabulaire MCU | [MCU-VOCAB.md](MCU-VOCAB.md) |
| Payloads JSON commandes | [JSON-COMMANDS.md](JSON-COMMANDS.md) |
| APK EmqxService | [EMQX-APK.md](EMQX-APK.md) |
| Mock HTTP + MQTT | `third_party_demo/mock` |

## Drive (copie ROM en cours)

Racine : `device` `frameworks` `hardware` `packages` `prebuilts` `vendor`.
`packages/apps` **noms** présents : EmqxService, GeeUIFace, GeeUIMessage, GeeUITaskService, LTPAudioService, LTPLauncher2, LTPMcuService, LTPOtaService, LTPService, LTPTestLauncher + apps AOSP.
Contenu de ces dossiers encore **vide** (upload). HAL Rockchip visible (`rknn_server`, `power_aidl`, `light_aidl`).

Relancer l’exploration Drive quand EmqxService/LTPService ont des fichiers.

## Bloqué robot

Pointer `Constants.kt`, boot 404, dump MQTT `#`.

## Ensuite

Launcher boot · locomotion AT · DispatchService · speech · toolchain · tests.
