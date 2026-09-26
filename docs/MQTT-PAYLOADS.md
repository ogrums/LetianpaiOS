# Payloads MQTT RUX

Date : 2026-09-26

## Limite source

Le client MQTT n’est **pas** dans les 19 forks. Le launcher démarre `com.letianpai.emqxservice.EmqxService`. Ce package n’est publié ni chez Letianpai-Robot ni chez ogrums.

Ce qu’on peut affirmer : le déballage côté robot. Ce qu’on ne peut pas : les noms exacts des topics et un éventuel wrapper chiffré sur le fil. Ça se confirme par dump Mosquitto une fois le broker mock en place.

## Chaîne

```
Cloud / téléphone
    MQTT  -->  EmqxService (APK fermé)
                 |
                 v
    ILetianpaiService.setLongConnectCommand(command, data)
                 |
                 v
    LtpLongConnectCallback.onLongConnectCommand(command, data)
                 |
                 v
    GeeUITaskService.DispatchService  →  setMcuCommand / setExpression / setAppCmd / …
```

Auth broker : HTTP `GET /robot_api/v1/bind/getIotTriplet`

```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "client_id": "mock-client",
    "user_name": "mock-device",
    "password_hash": "mock-password",
    "remote_host": "127.0.0.1",
    "remote_port": "1883"
  }
}
```

## Enveloppe applicative (certaine)

Après passage AIDL, le message est toujours :

```json
{"command": "<type>", "data": "<json ou string>"}
```

Même forme que `LtpCommand` / `CmdInfo`. `data` est souvent un **JSON sérialisé en string**, pas un objet imbriqué.

## `command` connus (cloud → robot)

Issus de `RobotRemoteConsts` + `MCUCommandConsts`. Ce sont les valeurs que DispatchService sait router. Un mock MQTT utile publie **ces** commandes, pas un format inventé.

### Actuateurs

| command | data typique |
|---|---|
| `controlMotion` | `{"motion":"forward","number":2}` |
| `controlFace` | nom de face (`happy`, …) |
| `controlSound` | nom SFX |
| `controlAntennaMotion` | `turn` / JSON oreille |
| `controlAntennaLight` | `on` / `off` / `twinkle` |
| `trtc` | `{"room_id":1,"user_id":"…","user_sig":"…"}` |

### Config / modes (téléphone)

| command | data |
|---|---|
| `otaUpgrade` | info package |
| `updateWifiConfig` | ssid/pwd |
| `updateBleConfig` | |
| `updateShowModeConfig` | |
| `updateSleepModeConfig` | |
| `updateAwakeConfig` | |
| `updateGeneralConfig` | |
| `updateDateConfig` | |
| `updateCalendarConfig` | |
| `updateFansConfig` | |
| `updateCountDownConfig` | |
| `updateDisplaySwitchConfig` | |
| `updateWeatherConfig` | |
| `updateClockData` | |
| `changeShowModule` | `event` / `weather` / `time` / `fans` |
| `changeMode` | `transform` / `show` / `sleep` / `auto` / `demo` / `reset` / `static` / `robot` |
| `startBindMijia` | |
| `resetDeviceInfo` | |
| `removeDevice` | |
| `deviceGuideFinish` | |
| `updateDeviceTimeZone` | |
| `controlSoundVolume` | |
| `controlDisplayMode` | `time` / `weather` / `countdown` / `fans` / `schedule` / `empty` / `darkScreen` / `exitDarkScreen` |
| `controlAutoMode` | `follow` / `exitFollow` / `random` |
| `addFaceFeature` | |
| `controlSendPic` / `controlSendWord` | |
| `set_app_mode` / `show_text` / `hide_text` / `show_charging` / `show_all` | |
| `get_channel_logo` | |

### Capteurs remontés (robot → cloud, même vocabulaire AIDL)

| command | Sens |
|---|---|
| `controlStartPrecipice` / `controlStopPrecipice` | Suspendu |
| `controlStartFallDown` / `controlStopFallDown` | Tombé |
| `controlTap` / `controlDoubleTap` / `controlLongPressTap` | Toucher tête |
| `fallBackend` / `fallForward` / `fallLeft` / `fallRight` | Anti-chute |

## Topics (non confirmés)

Hypothèses à valider au dump, **ne pas coder en dur** avant :

- `device/{sn}/down` ou `robot/{sn}/command` — cloud → robot
- `device/{sn}/up` ou `robot/{sn}/status` — robot → cloud
- éventuel `$SYS` / will LWT

`sn` vient de `getSnByMac` (`EMULATOR00000000` dans le mock).

## Implications mock

1. Mosquitto accepte `mock-device` / `mock-password` / client `mock-client`.
2. Logger **tous** les topics (`#`) au premier boot robot.
3. Publisher de test : payload AIDL `{command,data}` sur le topic down découvert.
4. Si rien n’arrive dans DispatchService : wrapper extra (encryption `GeeUITaskService/encryption`) ou topic faux — alors décompiler `emqxservice` depuis la ROM.
