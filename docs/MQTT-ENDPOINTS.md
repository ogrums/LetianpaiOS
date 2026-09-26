# Endpoints MQTT RUX

Date : 2026-09-26. Source : EmqxService.apk + `getIotTriplet` + Paho.

MQTT ici n’est **pas** une liste d’URL HTTP. Un « endpoint » = broker + topic + payload.

## Broker

Pas d’host hardcodé dans l’APK (sauf exemples de test `s_sn1002`).

```
GET /robot_api/v1/bind/getIotTriplet
 → data.remote_host, data.remote_port
 → data.client_id, data.user_name, data.password_hash
```

| | Production (cloud Letianpai) | Mock |
|---|---|---|
| URI | `tcp://<remote_host>:<port>` ou `ssl://` | `tcp://<LAN>:1883` |
| Port typique | 1883 / 8883 TLS | 1883 |
| Auth | user + password du triplet | `mock-device` / `mock-password` |
| clientId | triplet | `mock-client` |
| Session | clean + auto-reconnect Paho | idem |

TLS : `SSLUtils.getSingleSocketFactory` + cert `res/raw` si flag Connection SSL.

## Topics (bytecode confirmé)

Préfixe produit : **`L81`** (famille RUX). Identifiant = SN (ou clientId).

| Direction | Pattern | QoS (observé usage) |
|---|---|---|
| Cloud → robot | `cmd/L81/<id>/+/+` | subscribe wildcard 2 niveaux |
| Robot → cloud ACK | `cmd_resp/L81/<id>/` + suffixe `/` | publish |

Constantes de test encore dans le dex :

```
cmd/L81/s_sn1002/+/+
cmd_resp/L81/s_sn1002/
s_sn1003
```

Les deux `+` ne sont **pas** sémantiques dans le client : `messageArrived` lit le **body JSON**, pas le suffixe du topic. On peut donc publier :

```
cmd/L81/<sn>/cmd/control
cmd/L81/<sn>/app/x
cmd/L81/<sn>/ota/1
```

Tous matchent le subscribe.

Mock SN : `EMULATOR00000000` → `cmd/L81/EMULATOR00000000/+/+`.

## Payload sur le fil (descendant)

```json
{"cmd":"controlMotion","d":{"motion":"forward","number":2},"et":1999999999999}
```

| Champ | |
|---|---|
| `cmd` | string → `setLongConnectCommand` |
| `d` ou `data` | objet |
| `et` | epoch ms ; expiré → drop |

Exceptions : `otaUpdate` / `otaUpdateBackend` → start `com.letianpai.otaservice`, pas AIDL.

ACK montant (constante) : body `{'message':'get success'}` sur `cmd_resp/L81/<id>/...`.

Après AIDL le vocabulaire devient `{command, data}` — voir JSON-COMMANDS.md.

## Ce que MQTT ne transporte pas

HTTP `robot_api` (météo, OTA package, S3, news) ≠ MQTT. MQTT = commandes temps réel + ACK. Capteurs tête/chute passent d’abord AIDL ; remontée cloud non vue dans Emqx (pas de publish télémétrie évident hors ACK).

## Comment tester

```bash
# écoute
mosquitto_sub -h 127.0.0.1 -t '#' -v

# ordre motion
mosquitto_pub -h 127.0.0.1 -t 'cmd/L81/EMULATOR00000000/cmd/x' \
  -m '{"cmd":"controlMotion","d":{"motion":"forward","number":1},"et":1999999999999}'
```

Script : `third_party_demo/mock/pub.sh`.

## Trous

- Suffixe exact de `cmd_resp` (dump `#`).
- Autres familles que L81 (autres robots).
- Will / LWT / retain : non vus.
- Chiffrement applicatif au-delà de TLS : non vu dans Emqx ; `GeeUITaskService/encryption` est un autre sujet.
