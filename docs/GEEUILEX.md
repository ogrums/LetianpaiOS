# GeeUILex — analyse APK

Date : 2026-09-26
Source : Drive `GeeUILex.apk` 85.6 Mo. **Pas de repo source** (ni ogrums ni Letianpai-Robot).

## Qu’est-ce que c’est

Pas un lexique chinois isolé. C’est un **client vocal multi-moteur** + lecteur vidéo + copie des APIs écran.

| Couche | Preuve dans l’APK |
|---|---|
| AWS Lex Runtime V2 + Models V2 | SDK Kotlin `aws.sdk.kotlin.services.lexruntimev2` / `lexmodelsv2` |
| AWS Polly (TTS) | `SynthesizeSpeech`, `presignSynthesizeSpeech` |
| PocketSphinx offline | `libpocketsphinx_jni.so` + `assets/sync/` (cmudict, grammars `digits`/`menu`, acoustic `en-us-ptm`) |
| IJKPlayer | `libijkffmpeg/ijkplayer/ijksdl.so` + `assets/video/h0001.mp4`… |
| AIDL speech | `registerSpeechCallback` / `onSpeechCommandReceived` vers `com.renhejia.robot.letianpaiservice` |
| Cloud Letianpai | host dur `https://robot-api.letianpai.com` + chemins `/robot_api/v1/…` |

Package app : widgets embarqués dans le dex (`com.letianpai.robot.news`, `alarm`, `fans`, `message`, `racelight`, `geeuicustom`, `geeuistock`) — l’APK est un **gros bundle** écran+voix, pas seulement « Lex ».

## Hors ligne vs cloud

- Sphinx : reconnaissance **locale EN** (chiffres, menu, weather.dmp). Alternative possible : Vosk / Whisper.cpp on-device.
- Lex V2 : NLU cloud AWS (bot alias / locale / intent). Alternative : Rasa, Home Assistant conversation, LLM local.
- Polly : TTS cloud AWS. Alternative : Piper, Android TTS, Coqui.

Les endpoints AWS dans le dex sont des **préfixes de service** (`https://runtime-v2-lex`, `https://polly`, `https://sts`) résolus par le SDK (région + credentials), pas des URL complètes hardcodées.

## Alternative de remplacement (plus tard)

1. Couper AWS : ne plus livrer Lex/Polly SDK (90 Mo).
2. Garder PocketSphinx ou le remplacer par un moteur on-device.
3. Router `setSpeechCmd` / `setTTS` vers GeeUIAIAudioService uniquement.
4. Les `/robot_api/v1/user/*` de cet APK doivent pointer le mock comme LtpNetWork.
