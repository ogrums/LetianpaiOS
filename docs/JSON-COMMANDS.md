# Payloads JSON des commandes RUX

Date : 2026-09-26
Sources : CommandLib (`Motion`, `AntennaLight`, `AntennaMotion`, `RobotRemoteConsts`, `MCUCommandConsts`), EmqxService.apk, GeeUITaskService.

Trois enveloppes empilées. Ne pas les mélanger.

```
MQTT fil     { cmd, d|data, et }
     |
     v  EmqxService.setLongConnectCommand(cmd, data.toString())
AIDL         setXxx(command: String, data: String)
     |
     v  DispatchService / McuService parse CommandLib
data interne { motion, number, … }   ou string nue
```

## 1. Enveloppe MQTT (fil)

```json
{
  "cmd": "controlMotion",
  "d": { "motion": "forward", "number": 2 },
  "et": 1730000120000
}
```

- `cmd` = type AIDL (`RobotRemoteConsts` / `MCUCommandConsts`)
- `d` **ou** `data` = objet ou string
- `et` = epoch ms ; jeté si passé
- Topic : `cmd/L81/<sn>/+/+` — le type n’est **pas** dans le topic

OTA uniquement : `cmd` = `otaUpdate` | `otaUpdateBackend` → intent `com.letianpai.otaservice`, pas l’AIDL.

ACK : topic `cmd_resp/L81/<sn>/…` body `{'message':'get success'}`.

## 2. Enveloppe AIDL (locale)

Toujours deux strings. `data` est souvent un JSON **sérialisé en string**, parfois une string nue (`happy`, `on`).

Canal → méthode :

| Canal | Méthode | Qui écoute typiquement |
|---|---|---|
| long-connect / cloud | `setLongConnectCommand` | TaskService DispatchService |
| MCU / pieds / oreilles / LED | `setMcuCommand` | GeeUIMcuService |
| visage | `setExpression` | GeeUIFace / launcher |
| SFX | `setAudioEffect` | AudioService |
| TTS | `setTTS` (`speakText`, texte) | launcher |
| speech | `setSpeechCmd` | AI audio |
| app / mode | `setAppCmd` | TaskService |
| status | `setRobotStatusCmd` | TaskService |
| BLE | `setBleCmd` | WiFi/BLE |
| capteur remonte | `setSensorResponse` | TaskService |

Bind : intent `android.intent.action.LETIANPAI`, process `LetianpaiService`, `uid.system`.

Bus parallèle AT : `ISensorService.writeAtCommand("AT+MOTORW,1,1,1500\r\n")` dans GeeUIMcuService. Pas de JSON.

## 3. Schémas `data` (CommandLib)

### controlMotion → `Motion`

Champs Java : `motion`, `number`, `speed`, `desc`, `id`, `stepNum`.

**Attention** : `Motion.toString()` n’est **pas** du JSON strict (quotes simples). Les parseurs MCU acceptent souvent les deux. Pour le mock MQTT, envoyer du JSON strict.

```json
{"motion":"forward","number":2,"speed":0,"stepNum":0}
```

`motion` : `forward`, `backend` (typo back), `left`, `right`, `leftRound`, `rightRound`, `setStraight`, `takeEasy`, `turnRound`, `pettish`, `angry`, `run`, `cheers`, `tried`, `shakeLeg`, plus `FORWARD`/`BACK` ATCmdConsts.

Bug TaskService : voix « gauche/droite » mappe aussi sur `backend`.

### controlAntennaLight → `AntennaLight`

```json
{"antenna_light":"on","antenna_light_color":1}
```

`antenna_light` : `on` / `off` / `twinkle`.

### controlAntennaMotion → `AntennaMotion`

```json
{"cmd":3,"step":3,"speed":60,"angle":30}
```

`cmd` 1=oreilles gauche, 2=droite, 3=gauche-droite. Devient `AT+EARW,cmd,step,speed,angle`.

Ancien champ `antenna_motion:"turn"` encore cité dans MCU-VOCAB ; le `toString()` vivant est `cmd/step/speed/angle`.

### controlFace / controlSound

Souvent **string nue** : `"happy"`, nom SFX. Parfois `{"face":"happy"}`.

### changeMode / controlDisplayMode / controlAutoMode / changeShowModule

Valeur string nue (`sleep`, `time`, `follow`, `weather`) ou petit objet `ModeChange`.

### Config cloud (`updateWifiConfig`, `updateClockData`, …)

Objets dédiés sous `CommandLib/parser/` (ClockInfo, WeatherData, FansData, CountDown…). Non détaillés ici — à extraire fichier par fichier quand on mocke l’écran.

### trtc

```json
{"room_id":1,"user_id":"…","user_sig":"…"}
```

## 4. Exemples mock complets

MQTT (fil) :

```json
{"cmd":"controlMotion","d":{"motion":"forward","number":1},"et":1999999999999}
{"cmd":"changeMode","d":"sleep","et":1999999999999}
{"cmd":"controlAntennaLight","d":{"antenna_light":"twinkle","antenna_light_color":2},"et":1999999999999}
```

AIDL équivalent :

```
setLongConnectCommand("controlMotion", "{\"motion\":\"forward\",\"number\":1}")
setMcuCommand("controlMotion", "{\"motion\":\"forward\",\"number\":1}")
setExpression("controlFace", "happy")
setTTS("speakText", "bonjour")
```

## 5. Ce qui n’est pas une commande JSON

- AT bruts (`AT+MOVEW`, `AT+MOTORW`)
- Intents Android (OTA, launcher)
- HTTP `robot_api` (triplet, SN, package)
