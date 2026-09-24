# Migration Android 11 — stack 2026

L'app **tourne sur Android 11** (`minSdk 30`) et compile contre l'API 36.

## Toolchain

| Avant | Après |
|---|---|
| AGP 7.3 / Gradle 7.4–7.5 | **AGP 8.13.0 / Gradle 8.13** |
| Kotlin 1.6–1.8 | **Kotlin 2.0.21** |
| compileSdk 27–33 | **36** |
| minSdk 19–26 | **30 (Android 11)** |
| targetSdk 27–33 | **36** |
| Java 8 | **Java 17** |
| Support Library 27 | **AndroidX + Jetifier** |
| jcenter() | **google() + mavenCentral()** |
| ExoPlayer 2.17 | **ExoPlayer 2.19.1** |
| OkHttp 3.9 / 4.10 | **OkHttp 4.12.0** |
| Gson 2.8.6 | **Gson 2.11.0** |

## Prérequis

- JDK **17**
- Android Studio Ladybug / Meerkat / plus récent
- SDK Platform **36**

Ouvrir le projet, laisser Gradle sync, puis `Build > Build Bundle(s) / APK(s)`.

## Notes robot GeeUI (ROM Android 11)

Sur le robot (API 30), `targetSdk 36` ne change **pas** les comportements 31–36
(ils ne s'appliquent que si l'OS est >= le niveau). L'app reste compatible Android 11.

Jetifier réécrit les JAR encore en Support Library (Glide 3.7, Lottie 2.6, ijkplayer).
Les imports Java `android.support.*` du source ont été migrés vers AndroidX.
