# Payloads JSON des commandes RUX

Sources : pcap `mqtt-rux.pcap` (fil réel) + CommandLib.
Trois couches : MQTT `{cmd,d,et}` → AIDL deux strings → objet interne / AT.

## Enveloppe fil (toujours)

```json
{ "cmd": "controlMotion", "d": { … }, "et": 1790435392000 }
```

- `et` = epoch ms de péremption (souvent now+300s)
- ACK robot : body `{'message':'get success'}` (quotes simples) sur `cmd_resp/…`
- Topic **contient** le nom : `cmd/L81/<clientId>/<cmd>/<ts>`

`d` peut être un objet, une string (`"remoteStroll"`) ou `null`.

## Vu sur le fil (2026-09-26)

### controlMotion — le champ utile est `number`, pas `motion`

Sur le cloud officiel, `motion` est **toujours** `"null"`. L’identifiant est `number` + libellé CN `motion_name`.

```json
{"cmd":"controlMotion","d":{"motion":"null","motion_name":"立正","number":0,"step":1,"speed":3},"et":…}
```

| number | motion_name | speed vu |
|---|---|---|
| 0 | 立正 (garde-à-vous) | 3 |
| 5 | 螃蟹左 (crabe G) | 3 |
| 6 | 螃蟹右 | 3 |
| 8 | 右抖腿 | 3 |
| 11 | 左跷脚 | 3 |
| 12 | 右跷脚 | 3 |
| 21 | 左转 | 3 |
| 22 | 右转 | 3 |
| 23 | 并脚 | 3 |
| 64 | 小后退 | 2 |
| 65 / 66 | 快抖左/右脚 | 3 |
| 98 | 交替向前走 (marche) | 2 |

`step` toujours 1 dans cette capture. CommandLib connaît aussi `forward`/`left` en string : l’app téléphone n’envoie pas ça ici.

### controlFace

```json
{"cmd":"controlFace","d":{"face":"h0001","face_name":"愤怒"},"et":…}
```

IDs vus : h0001 愤怒, h0005 笑, h0006 失恋?, h0011 爱心, h0017 皱眉, h0024 摇头, h0025 右倾听, h0027 左倾听, h0034 眩晕 / 芭比Q.

### controlSound

```json
{"cmd":"controlSound","d":{"sound":"a0001","sound_name":"嘟"},"et":…}
```

a0001–a0014 (嘟, 激光, 冲锋枪, 打铁…).

### changeMode

```json
{"cmd":"changeMode","d":{"mode":"demo","mode_status":1},"et":…}
```

`mode_status` 1=on 0=off. Seul `demo` vu.

### changeShowModule

```json
{"cmd":"changeShowModule","d":{"select_module_tag_list":["time"]},"et":…}
```

Tags : `time`, `weather`, `stock`, `robot`, packages `com.letianpai.robot.expression` / `.spectrum`.

### Autres

| cmd | d |
|---|---|
| controlSendWord | `{word:"hello"}` aussi FR |
| speechDance / speechMusic / enterAIDialog | `null` |
| deviceRemoteMsgPush | string `"remoteStroll"` |
| controllGyroscope | `{cmd_value:"AT+FiAGW,2,10", update_time}` |
| trtc / trtcMonitor | `{room_id, user_id, user_sig, expire_ts}` — Tencent |
| exitTrtc | `{room_id, send_client_id}` |
| updateVoiceAideEventData | `{action:"edit", voice_type, app_id, api_*, url}` url vu `https://www.ruxbot.duckdns.org/ai/` |

## AIDL après Emqx

`setLongConnectCommand(cmd, JSON.stringify(d))` — le `et` ne part pas dans l’AIDL.

## Mock minimal locomotion

```json
{"cmd":"controlMotion","d":{"motion":"null","motion_name":"交替向前走","number":98,"step":1,"speed":2},"et":1999999999999}
```
