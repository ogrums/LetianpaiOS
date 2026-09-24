# LetianpaiOS

Android 11 home screen for the GeeUI robot. Gradle project name: `LeTianPaiRobot`. This repository builds the launcher and the sound-effect app. It does not build the motion MCU or the AIDL bus.

## What Gradle compiles

| Module | Role |
|---|---|
| `LeTianPaiLauncher` | HOME shell (`com.renhejia.robot.launcher`). Starts the other system apps |
| `LetianpaiAudioService` | Sound effects (`com.letianpai.robot.audioservice`). Not the voice assistant |
| `audio`, `player`, `message` | Speech wrappers and the music skill used by the launcher |
| `CommandLib`, `GestureFactory` | Command models and gesture steps |
| `expression`, `displayview`, `guideLib`, `GeeUINotice` | Face, clock skins, first-boot Wi-Fi, alarms |
| `FmodSound`, `library` | Optional FMOD playback and a small log helper |

`RobotService` starts these packages, in this order:

1. Immediately: resources (`com.letianpai.robot.geeuiresources`)
2. After 200 ms: sound effects (`com.letianpai.robot.audioservice`)
3. After 1 s: EMQX, bugreport, Mi IoT (unless region is `en`), MCU, task service, alarm, then in-process `DispatchService`

The voice assistant is not in that list. `LeTianPaiMainActivity` starts `com.rhj.speech` for Chinese and `com.geeui.lex` for English.

The MCU package in that list is `com.letianpai.robot.mcuservice`, which is the separate repository [GeeUIMcuService](https://github.com/Letianpai-Robot/GeeUIMcuService). The AIDL bus is [LetianpaiService](https://github.com/Letianpai-Robot/LetianpaiService). Older copies of both used to sit in this tree and were not in `settings.gradle`. They are removed so this project is only what it compiles.

`PowerMotion.toString()` is JSON (`{"function":3, "status":0}`) so Gson can read it. Function 3 is leg/foot servo power. Function 5 is cliff / hang / time-of-flight. GeeUIMcuService turns those into `AT+FunCtr`.

## Build

JDK 17. See [MIGRATION-ANDROID11.md](MIGRATION-ANDROID11.md).

```text
./gradlew :CommandLib:testDebugUnitTest
./gradlew :LeTianPaiLauncher:assembleDebug :LetianpaiAudioService:assembleDebug
```

Signing uses `keystore/letianpai.jks` only when that file is present. Passwords come from Gradle properties `LETIANPAI_STORE_PASSWORD` and `LETIANPAI_KEY_PASSWORD`, not from the build file. Without the keystore, debug APKs are unsigned.

## Tests

`:CommandLib:testDebugUnitTest` checks:

- `PowerMotion` JSON round-trip
- the companion-app list, including that English skips Mi IoT and that the MCU class is `LTPMcuService` in `com.letianpai.robot.mcuservice`
