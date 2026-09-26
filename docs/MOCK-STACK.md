# Mock cloud + MQTT

Date : 2026-09-26
But : tester le robot sans le cloud Letianpai. Deux process, pas un monolithe.

## État actuel

HTTP déjà là : `ogrums/third_party_demo/mock` — `go run ./mock` → `127.0.0.1:8080`.

Il répond `{code,msg,data}` pour bind, OTA, upload, logo, weather, calendar, countdown, fans, clock, bindInfo, serverTime.
`getIotTriplet` pointe déjà `remote_host=127.0.0.1` `remote_port=1883`. **Aucun broker n’écoute encore ce port.**

LLM séparé : `third_party_demo` `POST /sse` port 8012. Ne pas fusionner avec le mock cloud.

Le robot lit l’hôte dans `LtpNetWork` `Constants.kt` (`https://your-server.com`) et dans les consts launcher encore placeholder. Tant que ce n’est pas `http://<pc>:8080`, le mock est invisible.

## Architecture cible

```
Robot / émulateur
    HTTP  -->  mock :8080   /robot_api/v1/...
    MQTT  -->  broker :1883  topics device/{sn}/#
    SSE   -->  LLM demo :8012  (optionnel)
```

`getIotTriplet` doit renvoyer les identifiants du broker local (`user_name`, `password_hash`, `client_id`, host, port).

## MQTT — proposition

Broker : **Mosquitto** en local (ou EMQX si on veut coller au package `emqx` démarré par le launcher). Mosquitto suffit pour le mock.

Étape 1 — broker nu + logs de tous les topics (`#`).
Étape 2 — extraire les topics réels depuis le client EMQX du robot (sn, command/status).
Étape 3 — publisher de scénarios : `controlMotion`, config weather, OTA, precipice.

Les commandes MQTT doivent réutiliser le vocabulaire AIDL (`controlMotion` + JSON), pas inventer un 3e dictionnaire.

Auth : user/password = valeurs renvoyées par `getIotTriplet`. Pas d’anonyme en dur sur le robot si le client refuse.

## HTTP — étapes restantes

1. Pointer `Constants.kt` + consts launcher vers `http://<ip-lan>:8080` (debug only).
2. Écouter le mock (`log.Printf`) pendant un boot robot → lister les chemins 404 manquants.
3. Ajouter ces routes (signature headers `LTPSignInterceptor` si le robot refuse les 200 sans sign).
4. Fichiers factices `/files/mock.zip` pour ne pas planter l’OTA check.

## Hors périmètre du mock cloud

- UART / AT : tester avec `ISensorService.writeAtCommand` sur le vrai robot, ou un stub JNI plus tard.
- AIDL : tests instrumentés Android, pas HTTP.

## Ordre de travail (ajouté au backlog vision)

1. Vocabulaire MCU (doc).
2. Branch `Constants.kt` debug → mock HTTP.
3. Boot robot + journal des 404 HTTP.
4. Mosquitto :1883 + `getIotTriplet` cohérent.
5. Dump topics MQTT réels.
6. Scénarios MQTT (motion, config, cliff).
7. Optionnel : replay AT sans hardware.
