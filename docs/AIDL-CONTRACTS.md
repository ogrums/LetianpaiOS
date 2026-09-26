# Contrats AIDL RUX / GeeUI

Date : 2026-09-26
Sources : `ogrums/GeeUIComponents` (CommChannel), `ogrums/LetianpaiService`, `ogrums/GeeUIMcuService`.

Deux bus distincts. Ils ne partagent ni package ni process.

```
App A  --setXxx(cmd,data)-->  LetianpaiService (action LETIANPAI)
                                  |
                                  +-- RemoteCallbackList --> App B onXxx(cmd,data)

App / debug --writeAtCommand--> ISensorService (dans GeeUIMcuService)
                                  |
                                  +--> UART / AT+FunCtr
```

## 1. Bus principal — `ILetianpaiService`

- Package interface : `com.renhejia.robot.letianpaiservice`
- Callbacks thématiques : `com.letianpai.robot.letianpaiservice` (deux namespaces)
- Processus : `ogrums/LetianpaiService`, service exporté, `sharedUserId=android.uid.system`
- Intent : `android.intent.action.LETIANPAI`
- Définition canonique : `GeeUIComponents/CommChannel/.../ILetianpaiService.aidl`
- Copies (dette) : aussi dans LetianpaiOS (`guideLib`, `LetianpaiAudioService`) et GeeUIFace. Le repo `LetianpaiService` n’embarque **pas** les `.aidl` ; il consomme le submodule Components.

### API

| Méthode | Payload | Callback |
|---|---|---|
| get/setRobotStatus | `int` | `LtpCommandCallback.onRobotStatusChanged` |
| setCommand | `inout LtpCommand` | `LtpCommandCallback.onCommandReceived` |
| setLongConnectCommand | `(String command, String data)` | `LtpLongConnectCallback` |
| setMcuCommand | idem | `LtpMcuCommandCallback.onMcuCommandCommand` |
| setAudioEffect | idem | `LtpAudioEffectCallback` |
| setExpression | idem | `LtpExpressionCallback` |
| setAppCmd | idem | `LtpAppCmdCallback` |
| setRobotStatusCmd | idem | `LtpRobotStatusCallback` |
| setTTS | idem | `LtpTTSCallback` |
| setSpeechCmd | idem | `LtpSpeechCallback` |
| setSensorResponse | idem | `LtpSensorResponseCallback` |
| setMiCmd | idem | `LtpMiCmdCallback` |
| setIdentifyCmd | idem | `LtpIdentifyCmdCallback` |
| setBleCmd | `(cmd, data, boolean isNeedResponse)` | `LtpBleCallback` |
| setBleResponse | `(cmd, data)` | `LtpBleResponseCallback` |

Chaque canal a `register* / unregister*`.

`LtpCommand` est seulement `parcelable LtpCommand` — le schéma vit dans une classe Java hors AIDL. Les 13 autres canaux sont des paires de strings non typées.

`LtpMcuCommandCallback` expose `onMcuCommandCommand` (doublon « Command » dans le nom).

Paramètre `speechCallback` réutilisé par erreur sur `registerSensorResponseCallback`.

## 2. Bus capteur / AT — `ISensorService`

- Package : `com.letianpai.sensorservice`
- Processus : `GeeUIMcuService`
- Fichiers : `app/src/main/aidl/com/letianpai/sensorservice/`

| Méthode | Rôle |
|---|---|
| writeAtCommand(String) | Envoi brut AT vers le MCU |
| registerGeeUIWriteResListener | ACK texte `onSensorWriteRes(String)` |
| registerGeeUISensorIRDataListener | `onSensorDataChanged(int sensorData, String sensorType)` |

Typo figée : `IGeeUISensoWriteResListener` (Senso sans r).

Ce bus **court-circuite** LetianpaiService : une app qui bind ISensorService parle direct au JNI série. Le chemin « propre » pour la locomotion reste `setMcuCommand` → callback MCU → GeeUIMcuService → AT.

## 3. Faiblesses du contrat

1. **Pas de version AIDL.** Ajouter une méthode casse tous les stubs copiés.
2. **Copies multiples** des mêmes `.aidl` (Components + Face + guideLib + AudioService). Divergence silencieuse possible.
3. **Stringly typed.** `command` / `data` sans enum, sans JSON schema dans l’IDL. Le typage est dans `CommandLib` / parseurs MCU.
4. **Deux packages** `renhejia` vs `letianpai` pour le même bus.
5. **Fan-out seulement.** Aucun request/reply (sauf BLE `isNeedResponse`). Pas de corrélation id, pas de timeout.
6. **Service exported + system uid.** Tout APK system peut poster n’importe quel canal.
7. **ISensorService parallèle** : deux chemins vers le MCU, risque de commandes concurrentes sur l’UART.
8. **inout LtpCommand** : mutation côté stub rarement utile, coût parcel.

## 4. Améliorations possibles (pas encore de PR)

Court terme, sans casser le robot :
- Une seule source : GeeUIComponents. Supprimer les copies dans Face / guideLib / AudioService (dépendance module).
- Documenter le vocabulaire `command` de `setMcuCommand` à côté des AT (`AT+FunCtr`, PowerMotion function 3/5).
- Tests : round-trip parcel `LtpCommand` + enregistrement callback MCU.

Moyen terme :
- `int getInterfaceVersion()` sur ILetianpaiService.
- Un seul callback générique `onEvent(int channel, String command, String data)` en plus des canaux existants (ajout, pas remplacement).
- Sérialiser l’accès UART : ISensorService.writeAtCommand délègue à la même queue que setMcuCommand.
- Corriger les typos dans une version N+1 seulement (compat).

Ne pas fusionner les deux bus tant que GeeUIMcuService et le launcher n’ont pas une queue unique.
