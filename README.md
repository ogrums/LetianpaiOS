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

## Dependencies

Versions are declared once in [gradle/libs.versions.toml](gradle/libs.versions.toml). A module writes `implementation libs.gson` or `implementation libs.bundles.okhttp`, never a version number. Plugin ids use `alias(libs.plugins.android.library)`. SDK 36 / minSdk 30 / Java 17 come from [gradle/android-sdk.gradle](gradle/android-sdk.gradle), applied by every Android module.

Glide stays on 3.7.0 and Lottie on 2.6.0 because the source calls those APIs. Jetifier still rewrites those jars. ZXing stays the jar in `guideLib/libs`, not a Maven coordinate.

## Build

JDK 17. See [MIGRATION-ANDROID11.md](MIGRATION-ANDROID11.md).

```text
./gradlew :CommandLib:testDebugUnitTest
./gradlew :LeTianPaiLauncher:assembleDebug :LetianpaiAudioService:assembleDebug
```

Signing uses `keystore/letianpai.jks` only when that file is present. Passwords come from Gradle properties `LETIANPAI_STORE_PASSWORD` and `LETIANPAI_KEY_PASSWORD`, not from the build file. Without that keystore, release APKs are unsigned and debug APKs use the normal Android Studio debug key.

## Emulator

`android:sharedUserId="android.uid.system"` stays on the release manifest. That user id only installs if the APK is signed with the robot platform certificate, which a normal emulator does not have (`INSTALL_FAILED_SHARED_USER_INCOMPATIBLE`).

The debug source set removes `sharedUserId`, so Android Studio Run can install the launcher on an emulator. Debug is not a system app there: privileged permissions are refused, and the companion packages (MCU, sound, EMQX) are absent. The robot image still uses the release build with `sharedUserId`.

## Tests

`:CommandLib:testDebugUnitTest` checks:

- `PowerMotion` JSON round-trip
- the companion-app list, including that English skips Mi IoT and that the MCU class is `LTPMcuService` in `com.letianpai.robot.mcuservice`
