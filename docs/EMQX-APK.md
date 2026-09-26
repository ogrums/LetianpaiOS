# EmqxService.apk (analysé 2026-09-26)

Fichier : `artifacts/EmqxService.apk` ~7.2 Mo. versionCode `2306015`. minSdk 26 / target 33.
`sharedUserId=android.uid.system`.

## Stack

- Kotlin `EmqxService.kt` (~407 lignes)
- Client **Eclipse Paho Android** (`MqttAndroidClient` + service `org.eclipse.paho.android.service.MqttService`)
- LtpNetWork embarqué : `getIotTriplet`, `getSnHardcode`
- Bind AIDL `com.renhejia.robot.letianpaiservice`
- TLS optionnel : `SSLUtils.getSingleSocketFactory` + cert `res/raw`
- Creds persistées (`xgbase.Preference`) : clientId, user, password, host, port

## Démarrage

`onStartCommand` → `connectService` (AIDL) → réseau up → `getIotTriplet` / `getSnByMac` → `connectMani(host, port, clientId, user, password)`.

URI : `tcp://` ou `ssl://` selon flag TLS du `Connection`.
Clean session + automatic reconnect.
Hardcode lu / écrit `persist.sys.hardcode`.

## Topics (confirmés dans le bytecode)

Après `onSuccess` connect :

- **Subscribe** : `cmd/L81/` + identifiant (SN / clientId) + `/+/+`
- **Publish réponse** (`dealResult`) : `cmd_resp/L81/` + même identifiant (+ suffixe via `/`)

`L81` = famille hardware RUX. Les `+` sont des wildcards MQTT (deux niveaux).

Exemple mock SN `EMULATOR00000000` :

```
cmd/L81/EMULATOR00000000/+/+
cmd_resp/L81/EMULATOR00000000/
```

(le suffixe exact de la réponse se concatène avec `/` — à figer au premier publish observé).

## Payload descendant (`messageArrived`)

JSON objet, pas `{command,data}` nu :

```json
{
  "cmd": "controlMotion",
  "d":  { },
  "data": { },
  "et":  1730000000000
}
```

- `cmd` : string → passé à `setLongConnectCommand` (sauf OTA)
- `d` **ou** `data` : objet payload
- `et` : long, expiration ; si passé, log `message is expired` et drop

Cas spéciaux `setCommand` : `otaUpdate` / `otaUpdateBackend` démarrent `com.letianpai.otaservice` au lieu de l’AIDL. Champ `otaUpdateType`.

ACK montant : topic `cmd_resp/L81/…` body `{'message':'get success'}` (quotes simples dans la constante).

## Implications mock

1. Broker :1883, user/pass/client du triplet.
2. Publier sur `cmd/L81/<sn>/cmd/x` (n’importe quels 2 niveaux après le SN).
3. Body JSON `cmd` + `d`/`data` + `et` dans le futur.
4. Écouter `cmd_resp/L81/#`.
