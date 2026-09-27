# API robot via LtpNetWork

Lib Retrofit. Hosts dans `Constants.kt` (fork : placeholders `your-server.com`).
Live : `https://robot-api.letianpai.com` / `https://global-robot-api.letianpai.com`.
Envelope `{code,msg,data}` — succès `code==0`.

## Endpoints déclarés (`NewApi.kt`)

| méthode | path | rôle |
|---|---|---|
| GET | `/robot_api/v1/bind/getIotTriplet` | MQTT user/pass/host/port/clientId |
| GET | `/robot_api/v1/bind/getSnByMac` | SN + hardcode |
| GET | `/robot_api/v1/ota/getLatestPackage` | OTA |
| POST | `/robot_api/v1/device/upgrade/status` | progression OTA |

Le reste du fichier (`/index/addorder`, shop) = démo, pas le robot.

`IotTripletM` : `remote_host`, `remote_port`, `user_name`, `password_hash`, `client_id`.

## Ce que LtpNetWork ne couvre PAS

- `/mini_api/v1/...` = app téléphone
- Lex / AWS / ChatGPT hosts
- PolarProxy : `/device/uploadStatus`, `/common/getConfig` — autres APK
- MQTT (EmqxService)

On ne peut pas « reconstruire tous les appels API » depuis ce seul repo. Juste le **bootstrap** bind + OTA.

Pour offline : mocker `getIotTriplet` → `remote_host=IP_PC` `port=1883` **ou** DNAT `43.153.69.45:1883` sans toucher HTTP.
