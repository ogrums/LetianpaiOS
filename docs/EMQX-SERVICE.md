# EmqxService — pas de source dans les forks

Date : 2026-09-26

## Verdict

`com.letianpai.emqxservice.EmqxService` **n’a pas de dépôt public ni privé** dans :

- org `Letianpai-Robot` (19 repos) ;
- forks `ogrums/*` listés pour ce projet ;
- `GeeUI_ROM` (docs + lien Drive uniquement, pas d’APK) ;
- GitHub / Gitee indexés sous ce nom de package.

On ne peut pas « analyser le code source » tant que l’APK n’est pas sorti du robot. Inventer un client Paho serait faux.

## Ce qui est connu sans l’APK

Démarrage launcher (`CompanionApps.after1s`) :

- package : `com.letianpai.emqxservice`
- classe : `com.letianpai.emqxservice.EmqxService`
- délai : +1 s après HOME

Rôle observé par les voisins :

1. Appelle `GET /robot_api/v1/bind/getIotTriplet` (via LtpNetWork) → `client_id`, `user_name`, `password_hash`, `remote_host`, `remote_port`.
2. Ouvre un client MQTT vers ce broker.
3. Au message descendant : `ILetianpaiService.setLongConnectCommand(command, data)`.
4. Les apps (surtout `GeeUITaskService.DispatchService`) reçoivent `LtpLongConnectCallback.onLongConnectCommand`.

Envelope après unwrap : `{command, data}` — voir [MQTT-PAYLOADS.md](MQTT-PAYLOADS.md).

Topics, QoS, LWT, TLS, et un éventuel wrapper chiffré : **inconnus**.

## Comment avoir le source (robot root + ADB)

```bash
adb shell pm path com.letianpai.emqxservice
# ex. package:/system/priv-app/EmqxService/EmqxService.apk

adb pull /system/priv-app/EmqxService/EmqxService.apk .
# ou le chemin renvoyé par pm path

# jadx-gui EmqxService.apk
# chercher : subscribe, publish, setLongConnectCommand, topic
```

Si `pm path` échoue : `adb shell ls /system/priv-app /system/app | grep -i emqx`.

Une fois jadx sous les yeux, mettre à jour cette page : topics, payload brut vs JSON, lib MQTT (Paho / HiveMQ / maison).

## Ce qu’on ne fait pas en attendant

- Ne pas créer un faux repo `GeeUIEmqxService` avec un client inventé.
- Le mock Mosquitto peut quand même écouter `#` : dès que l’APK se connecte, les topics apparaissent.
