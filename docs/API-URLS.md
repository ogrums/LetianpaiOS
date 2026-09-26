# Catalogue URL et appels API RUX

Date : 2026-09-26
Objectif : liste à remplacer par mock / self-host. Hosts production souvent **redactés** dans les forks (`your-server.com`). L’APK GeeUILex fixe `https://robot-api.letianpai.com`.

## Hosts

| Host | Où | Rôle |
|---|---|---|
| `RELEASE_HOST_TEST` = `https://your-server.com` | LtpNetWork `Constants.kt` | CN (`persist.sys.region.language` = zh ou vide) |
| `GLOBAL_IotUrl` = `https://your-global-server.com` | idem | hors CN |
| `https://robot-api.letianpai.com` | GeeUILex.apk | host réel encore dans le binaire ROM |
| MQTT `remote_host`/`remote_port` | réponse `getIotTriplet` | broker EMQX (Paho) |
| `https://sts.amazonaws.com` | GeeUILex | STS AWS (Lex/Polly) |
| `https://runtime-v2-lex` / `https://polly` | SDK AWS (région) | NLU / TTS |
| `http://jira.xgrobotics.com:8090/…` | LtpNetWork `Contents.java` (commentaire) | wiki interne headers |

Mock actuel : HTTP `127.0.0.1:8080`, MQTT `1883`.

## HTTP `robot_api` (LtpNetWork + GeeUILex + mock)

Auth : headers `Sn`, `Mac`, `Sign`, `Time`, `Ak`/`Sk`, `Model`, `Devid` (`Contents.HEADER`). Query `sn` + `ts` souvent.

### Bind / identité / OTA / fichiers — LtpNetWork `NewApi`/`Api`

| Méthode | Chemin | Mock |
|---|---|---|
| GET | `/robot_api/v1/bind/getIotTriplet` | oui |
| GET | `/robot_api/v1/bind/getSnByMac` | oui |
| GET | `/robot_api/v1/ota/getLatestPackage` | oui |
| POST | `/robot_api/v1/device/upgrade/status` | oui |
| POST | `/robot_api/v1/device/addRecord` | oui |
| GET | `/robot_api/v1/cloudFile/getToken` | oui |
| GET | `/robot_api/v1/cloudFile/getSessionToken` | oui (S3-like) |

Test dead : `POST /index/addorder`, `POST addons/shop/checkout/submit`, `GET index/hello`, `GET index/getorder`.

### Écran / user — GeeUILex dex (forks ont encore `"your interface url"`)

| Chemin | Sens |
|---|---|
| `/robot_api/v1/common/getConfig` | config |
| `/robot_api/v1/device/getAllConfig` | tout |
| `/robot_api/v1/device/module/change` | POST module écran |
| `/robot_api/v1/device/reset/updateResetStatus` | reset |
| `/robot_api/v1/device/uploadStatus` | télémétrie |
| `/robot_api/v1/news/getNewsList` | news |
| `/robot_api/v1/user/calenderList` | calendrier (typo calender) |
| `/robot_api/v1/user/clockList` | alarmes |
| `/robot_api/v1/user/cutdownList` | countdown (typo) |
| `/robot_api/v1/user/fansInfoList` | fans |
| `/robot_api/v1/user/generalInfo` | général |
| `/robot_api/v1/user/getCommDayList` | commémorations |
| `/robot_api/v1/user/getCustomList` | custom |
| `/robot_api/v1/user/getLampCustomInfo` | lamp / race light |
| `/robot_api/v1/user/stockInfoList` | bourse |
| `/robot_api/v1/user/weatherInfo` | météo |

Mock a aussi (noms **device/**, pas **user/**) : `/device/logo|general|weather|calendar|countdown|fans|clock|bindInfo|serverTime`. **Écart de chemin** à aligner.

## MQTT

| | |
|---|---|
| Subscribe | `cmd/L81/<sn>/+/+` |
| Publish ACK | `cmd_resp/L81/<sn>/` |
| Payload | `{cmd, d\|data, et}` |
| Auth | triplet IoT |

## AWS (GeeUILex seulement)

Lex Runtime V2, Lex Models V2, Polly, STS. Credentials sans doute via session Letianpai (`getSessionToken`) ou IAM embed — **à extraire plus tard**, ne pas committer de clés.

## À ajouter au mock (prochaine passe)

Chemins `user/*` + `common/getConfig` + `device/getAllConfig` + `uploadStatus` + `module/change` + `updateResetStatus` + `news` + `stock` + `getCommDayList` + `getCustomList` + `getLampCustomInfo`.

Alias : accepter à la fois `/user/weatherInfo` et `/device/weather`.
