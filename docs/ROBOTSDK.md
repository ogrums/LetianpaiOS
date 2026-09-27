# RobotSDK officiel RUX

Source boutique : [Rux RobotSDK](https://store.letianpai.com/blogs/%E6%96%B0%E9%97%BB/rux-robotsdk) (2026-09-27).
AAR analysé : `artifacts/RobotSdk-release-2.2.aar.zip` = CDN 2.2.

SDK **on-device** (AIDL `LETIANPAI`). Zéro réseau. TTS = `setTTS` → Lex/Polly aujourd’hui.

## Téléchargements

Base : `https://cdn.file.letianpai.com/b765d7f0250777afe5745ddc46d0c234/RobotSDK/`

| Version | Fichier | Notes page |
|---|---|---|
| 2.5 | RobotSdk-release-2.5.aar | |
| 2.3 | RobotSdk-release-2.3.aar | |
| 2.2 | RobotSdk-release-2.2.aar.zip | copie locale |
| 2.0 | RobotSdk-release.aar.2.0.zip | + expressions |
| 1.0 | RobotSdk-release.aar.1.0.zip | motion, oreilles, sons, TTS, capteurs |

Demo : [Letianpai-Robot/DemoForRobotSDK](https://github.com/Letianpai-Robot/DemoForRobotSDK) (fork ogrums).

## API RobotService

`robotOpenMotor` / `CloseMotor` (pas sur le UI thread). `robotActionCommand(number,speed,stepNum)`.
Oreilles : cmd 1=gauche 2=droite 3=gauche ; speed=ms ; angle 0–90.
Sons `a0xxx`, faces `h0xxx`, TTS texte, capteurs tap/chute/ToF.
Status bar = deprecated.

Cliff boutique : commentaires inversés vs noms Java (`onFallBackend` = « cliff devant »). Croiser UART `AT+INT,cliff`.

## number 1–80 (boutique) vs fil réel

SDK / doc : 1=avant, 2=arrière, 3/4=pivot, 5/6=crabe, 63/64=« forward/back 2 ».
AAR 2.2 constantes : avant=63 arrière=64 crabe 5/6 pivot 3/4 jambe 7.
Fil 2026-09-26 : **0**=garde-à-vous, **98**=marche, 21/22≈20°. Les deux catalogues coexistent. Voir MOTION-CODES.

Liste complète + sons + faces : copie artefacts `ROBOTSDK.md` (même contenu détaillé).

Exemple officiel : `ActionMessage.set(63, 2, 3)` → `AT+MOVEW,63,<step>,<speed>`.

## Décision

Offline motion/faces/sons = ces tags via AIDL ou MQTT mock. Pas de cloud.
`robotPlayTTs` reste Lex tant que la voix locale n’est pas branchée.
