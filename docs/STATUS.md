# Statut RUX / GeeUI — 2026-09-26

Méthode : vision → analyse → décision → PR. Pas encore de changement runtime flashé.

## Fait

| Étape | Livrable |
|---|---|
| Carte repos + choix multi-repos | [VISION.md](VISION.md) |
| Contrats AIDL | [AIDL-CONTRACTS.md](AIDL-CONTRACTS.md) |
| Vocabulaire MCU / AT / servos | [MCU-VOCAB.md](MCU-VOCAB.md) |
| Plan mock HTTP+MQTT | [MOCK-STACK.md](MOCK-STACK.md) |
| Payload MQTT (avant APK) | [MQTT-PAYLOADS.md](MQTT-PAYLOADS.md) |
| EmqxService absent des forks | [EMQX-SERVICE.md](EMQX-SERVICE.md) |
| Analyse APK EmqxService | [EMQX-APK.md](EMQX-APK.md) — topics `cmd/L81/<id>/+/+`, JSON `cmd`/`d`/`et` |
| Mock HTTP | `third_party_demo/mock` :8080 |
| Mock MQTT | `third_party_demo/mock` Mosquitto compose + `pub.sh` |

## En cours / bloqué robot

| Étape | État |
|---|---|
| Pointer `Constants.kt` vers le mock | pas commencé (besoin IP LAN + rebuild LtpNetWork) |
| Boot robot + 404 HTTP | attend le pointage |
| Connexion EmqxService au Mosquitto local | attend pointage + broker up |
| Dump topics réels `mosquitto_sub '#'` | attend le robot |

## Ensuite (backlog)

5. Launcher boot (200 ms / 1 s, liste d’apps)
6. Locomotion `AT+MOVEW` commenté vs `AT+MOTORW`
7. DispatchService / TaskService (routage)
8. IA / speech éclaté
9. Toolchain AGP 8 forks restants
10. Tests parseurs + mock
11. `sharedUserId` debug vs release

## Comment lancer le mock maintenant

```
git clone git@github.com:ogrums/third_party_demo.git
cd third_party_demo
docker compose -f mock/docker-compose.yml up -d
(cd mock && go run .)
./mock/pub.sh EMULATOR00000000 controlMotion '{"motion":"forward","number":1}'
```
