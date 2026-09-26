# Suite (2026-09-26 soir)

## P3 — plus tard
- Adapter l’app mobile LetianPai pour parler au mock (`mini_api` + republish MQTT). Pas prioritaire.

## MQTT / API : ce qui est déjà clos
- Topics : `cmd/L81/<clientId>/+/+` et `cmd_resp/…` (pcap + log Emqx).
- Broker : `43.153.69.45:1883`, envelope `{cmd,d,et}`.
- HTTP robot : `robot_api` (pcap PolarProxy).
- HTTP téléphone : `mini_api` (APK Flutter) — le téléphone ne fait pas MQTT.

## Ce qui manque encore (MQTT/API)
1. Mapping **number MQTT → AT+MOVEW** (table firmware). APK `LTPMcuService` + `SerialAllJNI`.
2. Liste complète des postures : réponse live `GET /mini_api/v1/device/control/getPostureList` (token app).
3. Topics **montants** hors ACK (batterie, capteurs) : un dump `#` plus long ou logcat MCU.
4. Mock HTTP : 404 réels une fois le robot sur le LAN.

## Code à analyser maintenant (APK déposés)
1. `LTPMcuService.apk` — `SerialAllJNI`, `AT+MOVEW` / `AT+EARW,%d,%d,%d,%d`.
2. `GeeUITaskService.apk` — `commandDistribute` (cmds AIDL jamais vues sur le fil : steering, precipice, fallDown).
3. `LTPAudioService` + `ChatGPTService` + `GeeUILex` — voix FR/EN, **après** locomotion offline.

Pas besoin : MiIoT, GeeUIAIAudio 252 Mo tant que la marche n’est pas mockée.
