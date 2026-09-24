# LetianpaiOS

The robot system shell. Gradle project name is `LeTianPaiRobot`. The old README said there are two modules (launcher and audio). The tree is larger than that.

## What it does

`LauncherActivity` is the HOME activity (`com.renhejia.robot.launcher`, system uid). If the device is already activated it opens `LeTianPaiMainActivity`. If not, it starts the Wi-Fi connector (`com.letianpai.robot.wificonnet`) and `guideLib`'s `BleService`.

`LeTianPaiMainActivity` is the full-screen shell. It starts speech (`com.rhj.speech` for Chinese, `com.geeui.lex` for English), the app store, and `GeeUIDesktopService`, and switches views: time, charging, sleep, remote control, shutdown. A comment notes that the boot screen is only the hello-world GIF layout; the snoring sound is not played here. Another comment says AISpeech (思必池) was split out and should be started from `RobotService`.

`LeTianPaiLauncherApp` starts `RobotService`, which starts the companion processes: resource service, EMQX long-connect, bugreport, MiIoT (unless region is `en`), MCU, task service, dispatch, alarm, and the sound-effect service.

`DispatchService` binds `ILetianpaiService` and handles gestures, shutdown, unbind, and time sync. `deviceGuideFinish` is the mini-program "guide finished" button. `removeDevice` is unbind. `sendShutdownCmd` can release the servos (舵机卸力).

## Modules that Gradle includes

| Module | Role |
|---|---|
| `LeTianPaiLauncher` | HOME app, display modes, starts other services |
| `LeTianPaiLauncherBaseLib` / `BusinessLib` | Init, battery, Wi-Fi, timer broadcasts |
| `CommandLib` / `GestureFactory` | Command constants and gesture steps |
| `expression` / `LeTianPaiExpression` | Face animation (GIF, Lottie, frames) |
| `displayview` | Clock and skin drawing |
| `guideLib` | BLE and Wi-Fi during first launch |
| `LetianpaiAudioService` | Sound-effect player. Action `android.intent.action.LTPAUDIOPLAYER`. Package `com.letianpai.robot.audioservice` |
| `audio` | `RhjAudioManager` speech wrapper |
| `message` | Speech result beans (text, music, weather, widget) |
| `player` | Play-music skill (`PlayerService`) |
| `GeeUINotice` | Alarm receiver and notice parsers |
| `FmodSound` | JNI `playTts` through FMOD. A comment says the voice-change save path supports WAV only |
| `library` | Vendored serial-port helper |

`LetianpaiService` and `LetianpaiMcuService` are still in the tree but commented out of `settings.gradle`. The MCU copy opens `/dev/ttyS5`. The live MCU app is the separate `GeeUIMcuService` repository. The live bus is the separate `LetianpaiService` repository.

`ViewModeConsts` mode ids: auto, standby, display, charging, sleep, function, ChatGPT, remote, demo, auto-play, audio-wakeup, one-shot.

## Packages this shell starts

`com.renhejia.robot.letianpaiservice` (action `android.intent.action.LETIANPAI`), `com.letianpai.robot.desktop`, `com.letianpai.robot.mcuservice`, `com.letianpai.robot.taskservice`, `com.letianpai.otaservice`, `com.letianpai.robot.geeuiresources` (action `android.intent.action.LETIANPAI.RESOURCE`), `com.geeui.face` (`DispatchService.ROBOT_PACKAGE_NAME`), alarm, EMQX, bugreport, factory test.

`LTPAudioService.playSoundEffect` plays a `raw` resource by name. That is sound effects, not the cloud voice assistant (`GeeUIAIAudioService`).

## Comment glossary

| Where | Chinese | English |
|---|---|---|
| `ViewModeConsts` | 模式常量 / 语音唤醒 / 一次性执行 | Mode constants / voice wakeup / one-shot |
| `RobotRemoteConsts` | OTA升级；更新显示模式；绑定到米家；悬空开始；倒下开始；单击 | OTA; update display mode; bind to Mi Home; hang start; fall start; single tap |
| `LeTianPaiMainActivity` | 思必池语音独立出来了，启动放在RobotService即可 | AISpeech was split out; start it from `RobotService` |
| `LeTianPaiMainActivity` | 开机的hello world GIF | Boot hello-world GIF |
| `LeTianPaiMainActivity` | 这里只是显示view布局，打呼噜声音不在这里 | Layout only; the snoring sound is not here |
| `DispatchService` | 小程序点击了引导的完成按钮 / 解除绑定 / 舵机卸力 | Mini-program finished the guide / unbind / release the servos |
| `PlayerService` | 播放音乐技能 | Play-music skill |
| `FmodSound.kt` | 变声保存，只支持WAV格式 | Voice-change save, WAV only |

## Build

System app. Submodules must be checked out. Several library projects are shared with `GeeUIBase`.
