# Capture PolarProxy (polarproxy.pcap)

229 Ko, 720 paquets. Trafic **HTTPS déchiffré** vu en HTTP:80 via le proxy. SN dans cette capture : `2006014140043` (≠ robot actuel `2006014140527`).

Pas de MQTT 1883 dans ce fichier — PolarProxy ne voit que le TLS HTTP.

## Hosts

| Host | Hits |
|---|---|
| `global-robot-api.letianpai.com` | 74 |
| `robot-api.letianpai.com` | 7 |
| `h.trace.qq.com` | 1 (télémétrie QQ, ignorer) |

CDN réponses : `d4owc89rbj03p.cloudfront.net` (OTA, icônes AppStore, logos).

## Endpoints à mocker (vu sur le fil)

| n | Méthode | Chemin |
|---|---|---|
| 24 | POST | `/robot_api/v1/device/uploadStatus` |
| 14 | GET | `/robot_api/v1/common/getConfig` (`robot_upload_status_config`, `api_robot_menu_list`, `deep_sleep_config_en`) |
| 8 | GET | `/robot_api/v1/device/getAllConfig` |
| 7 | GET | `/robot_api/v1/user/generalInfo` |
| 6 | POST | `/robot_api/v1/device/module/change` |
| 5 | GET | `/robot_api/v1/device/getChannelLogo` |
| 5 | POST | `/robot_api/v1/device/uploadBatteryStatus` |
| 2 | GET | `/robot_api/v1/bind/getIotTriplet` |
| 2 | GET | `/robot_api/v1/ota/getLatestPackage` |
| 1 | GET | `/robot_api/v1/bind/getDeviceBindInfo` |
| 1 | GET | `/robot_api/v1/config/serverTimestamp` |
| 1 | GET | `/robot_api/v1/device/app/getAllAppList` |
| 1 | GET | `/robot_api/v1/device/app/getUserAppList` |
| 1 | GET | `/robot_api/v1/device/app/ota/getLatestPackage` |
| 1 | POST | `/robot_api/v1/device/app/uploadAppStatus` |
| 1 | GET | `/robot_api/v1/device/automatic/recharge/getConfig` |
| 1 | GET | `/robot_api/v1/user/clockList` |

Nouveau par rapport à LtpNetWork : `uploadBatteryStatus`, `getChannelLogo`, `getDeviceBindInfo`, `serverTimestamp`, `app/*`, `automatic/recharge/getConfig`.
