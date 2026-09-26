# MQTT sur le fil (mqtt-rux.pcap)

2176 paquets, Linux SLL2. Fil Wireshark : `mqtt` ou `tcp.port == 1883`.
Broker `43.153.69.45:1883`. QoS 1. Keepalive 60. Mot de passe et `user_sig` / clés vocale **non recopiés** ici.

## Session

- CONNECT `clientId=l81_9aa8495f6c9ddf95920428e3c2352d4c` user `u_…_global`
- SUBSCRIBE qos1 : `cmd/L81/<clientId>/+/+`
- PING 60 s

## Topics réels

Cloud → robot :

```
cmd/L81/<clientId>/<cmdName>/<ts>
```

Robot → cloud :

```
cmd_resp/L81/<clientId>/<cmdName>/<ts_cmd>/<ts_ack>
```

Body ACK : `{'message':'get success'}` (quotes simples).

Le 4e segment du topic **répète** le `cmd` du JSON. Le client ignore le suffixe (wildcard) mais le cloud l’utilise pour corréler l’ACK.

## Commandes vues (body JSON `cmd` + `d` + `et`)

| cmd | d (exemple) |
|---|---|
| controlMotion | `{motion, motion_name, number, step, speed}` ex. 立正 |
| controlFace | `{face:"h0001", face_name}` |
| controlSound | `{sound:"a0001", sound_name}` |
| changeMode | `{mode:"demo", mode_status:1}` |
| changeShowModule | `{select_module_tag_list:["com.letianpai.robot.expression"]}` |
| updateVoiceAideEventData | `{action, voice_type, app_id, api_*} ` — clés cloud |
| speechDance / speechMusic | `d: null` |
| controlSendWord | `{word:"hello"}` |
| enterAIDialog | `d: null` |
| deviceRemoteMsgPush | `"remoteStroll"` |
| controllGyroscope | `{cmd_value:"AT+FiAGW,2,10"}` |
| trtc / trtcMonitor / exitTrtc | room_id + user_sig Tencent |

Pour mocker : publier qos1 sur `cmd/L81/<clientId>/controlMotion/<unix>` le même JSON.
