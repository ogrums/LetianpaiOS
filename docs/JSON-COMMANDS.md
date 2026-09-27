# Payloads JSON des commandes RUX

Sources : pcap `mqtt-rux.pcap` + CommandLib + [RobotSDK boutique](https://store.letianpai.com/blogs/%E6%96%B0%E9%97%BB/rux-robotsdk).
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

Catalogue 1–80 + constantes SDK 63/64 : [ROBOTSDK.md](ROBOTSDK.md) / [MOTION-CODES.md](MOTION-CODES.md).
Fil réel : 0 stand, 98 marche — ne pas écraser par la table marketing.

### controlFace

```json
{"cmd":"controlFace","d":{"face":"h0001","face_name":"愤怒"},"et":…}
```

IDs boutique (tag EN) : h0001 Angry, h0003 ashamed, h0005 Barbie Q, h0006 Happy, h0011 Dizziness, h0017 Frown, h0024 Look right, h0025 Look left, h0027 Love, h0034 Shake head, … h0210 Laugh, h0211 Cry. Liste : ROBOTSDK.md.
Les `face_name` CN du fil ne matchent pas toujours le libellé EN. Utiliser le **tag**.

### controlSound

```json
{"cmd":"controlSound","d":{"sound":"a0001","sound_name":"嘟"},"et":…}
```

SDK : `robotControlSound("a0003")`. Catalogue boutique a0001 Beep … a0133 Lost (ROBOTSDK.md).

### changeMode / changeShowModule / autres

Inchangé (demo, time/weather/stock, controlSendWord, trtc, …).

## AIDL après Emqx

`setLongConnectCommand(cmd, JSON.stringify(d))` — le `et` ne part pas dans l’AIDL.
SDK local équivalent : `RobotService.robotActionCommand` / `robotStartExpression` / `robotControlSound` / `robotPlayTTs`.

## Mock minimal locomotion

```json
{"cmd":"controlMotion","d":{"motion":"null","motion_name":"交替向前走","number":98,"step":1,"speed":2},"et":1999999999999}
```

Variante SDK doc : `number:63` (forward 2).
