# Protocole MQTT RUX

Sources : `mqtt-rux.pcap`, logs Emqx, `logcat6.txt`, APK EmqxService + app mobile.

## Transport

| | |
|---|---|
| Broker officiel | `tcp://43.153.69.45:1883` (pas de TLS) |
| Client | Paho dans `com.letianpai.emqxservice` uid.system |
| clientId | `l81_<32 hex>` — **pas** le SN |
| Auth | user/pass du triplet HTTP `getIotTriplet` |
| Keepalive | Paho défaut |

Le téléphone **n’ouvre pas** MQTT. Il POST `/mini_api/v1/device/control/*` ; le cloud republie.

## Topics

```
SUB  cmd/L81/<clientId>/+/+          qos 1
PUB  cmd_resp/L81/<clientId>/...     body {'message':'get success'}
```

Downlink réel : `cmd/L81/<clientId>/<cmd>/<unixTs>`.
Le type est **aussi** dans le JSON `cmd` — le 3e niveau du topic est informatif.
Wildcard `+` : n’importe quel cmd / timestamp.

Pas d’autres topics vus (pas de `status/`, pas de `#` côté robot).
Capteurs = UART `AT+INT`, pas MQTT.

## Envelope

```json
{"cmd":"controlMotion","d":{...},"et":1790435392000}
```

- `cmd` string AIDL (`controlMotion`, `controlFace`, …)
- `d` objet, string (`"remoteStroll"`) ou `null`
- `et` epoch ms de péremption — jeté si passé. Mettre now+300s pour le mock.
- qos 1

Emqx fait `setLongConnectCommand(cmd, JSON.stringify(d))`. Le `et` ne traverse pas l’AIDL.

## Cmds vues sur le fil

| cmd | d | effet |
|---|---|---|
| controlMotion | `{motion:"null", motion_name, number, step, speed}` | `AT+MOVEW,number,step,speed` |
| controlFace | `{face:"h00xx", face_name}` | écran, pas UART |
| controlSound | `{sound:"a00xx", sound_name}` | SFX Android |
| changeMode | `{mode:"demo", mode_status:0\|1}` | |
| changeShowModule | `{select_module_tag_list:["time"\|"weather"\|pkg]}` | |
| controlSendWord | `{word}` | TTS |
| speechDance / speechMusic / enterAIDialog | `null` | |
| deviceRemoteMsgPush | `"remoteStroll"` | |
| controllGyroscope | `{cmd_value:"AT+FiAGW,2,10"}` | AT dans le JSON |
| trtc / trtcMonitor / exitTrtc | room Tencent | ignorer |
| updateVoiceAideEventData | url aide vocale | |

## Chaîne

```
cloud ou mock
   PUB qos1 cmd/L81/<cid>/<cmd>/<ts>
        |
   EmqxService messageArrived
        |
   ILetianpaiService.setLongConnectCommand
        |
   TaskService commandDistribute
        |
   LTPMcuService  →  /dev/ttyS5  →  AT+
```

## Mock

`third_party_demo/mock` + DNAT `43.153.69.45:1883` → PC.
`pub.sh` / `artifacts/pub-rux.ps1` — clientId `l81_9aa8495f6c9ddf95920428e3c2352d4c`.
