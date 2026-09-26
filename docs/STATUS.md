# Statut RUX / GeeUI — 2026-09-26

## Fait

VISION, AIDL, MCU, JSON-COMMANDS, EMQX-APK, mock HTTP+MQTT.
[GEEUILEX.md](GEEUILEX.md) · [API-URLS.md](API-URLS.md)

## Mocks / AIDL / MQTT ensuite

1. Pointer `Constants.kt` vers le mock LAN.
2. Ajouter au mock les chemins `/robot_api/v1/user/*` (dex Lex) en plus de `/device/*`.
3. MQTT dump `#` sur le robot.

## Todo APK restants (Drive)

Haute : IdentService, LTPOtaService, MiIoT, LTPAudio/Mcu/Service/Launcher.
GeeUILex : fait (APK, pas de source).
