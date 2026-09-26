# Vocabulaire MCU RUX

Date : 2026-09-26
Sources : `CommandLib` (LetianpaiOS), `GeeUIMcuService`.

Trois couches empilées. Une commande AIDL n’est pas un AT.

```
setMcuCommand("controlMotion", "{\"motion\":\"forward\",\"number\":2}")
        |
        v
AT+MOVEW,1,2,2\r\n     ou     AT+MOTORW,<servo>,1,<pulse>\r\n
        |
        v
MCU GD32  — 6 servos + cliff + IMU
```

`McuCommandControlManager` (chemin AIDL → AT+MOVEW) est **presque tout commenté**. Le chemin vivant aujourd’hui : `Servo.sendATCommand` → `ATCommandControler` → `SerialAllJNI.writeData`.

## 1. Types AIDL (`setMcuCommand` command)

| command | data JSON | Effet attendu |
|---|---|---|
| `controlMotion` | `{motion, number}` | Marche / figure |
| `controlFace` | `{face}` | Visage (souvent relayé Face, pas MCU) |
| `controlSound` | `{sound}` | SFX |
| `controlAntennaMotion` | `{antenna_motion}` valeur `turn` | Oreilles |
| `controlAntennaLight` | `{antenna_light}` on/off/twinkle | LED antennes |
| `trtc` | `{room_id, user_id, user_sig}` | Appel vidéo Tencent |

Valeurs `motion` côté GeeUIMcuService : `forward`, `backend` (typo), `left`, `right`, `leftRound`, `rightRound`, `setStraight`, `takeEasy`, `turnRound`, `pettish`, `angry`, `run`, `cheers`, `tried` (typo tired), `shakeLeg`.

Valeurs `ATCmdConsts` (plus riches, CommandLib) : `FORWARD`/`BACK`/`TURN_LEFT`… **et** camelCase `shakeLeftLeg`, `takeEasy`, `setStraight`, `random29`…`random42`, plus 43–60 (`twist`, `inclineL`, …). Deux dictionnaires coexistent.

`open_mcu` / `close_mcu` ouvrent le TTY, ce ne sont pas des pas.

## 2. AT firmware

Fin de trame : `\r\n`. Séparateur : `,`.
ACK attendu : `AT+RES,ACK\r\n` (la constante Java double-escape `\\r\\n`, à vérifier sur fil).

| AT | Args | Sens |
|---|---|---|
| `AT+MOVEW` | `cmd,steps,speed` | Figure firmware. cmd 0=stand, 1=fwd, 2=back, 3=left, 4=right, 5–6 crab, 7–24 figures, 29–60 « random » |
| `AT+MOTORW` | `servo,type,value` | Servo 1–6. type 0=angle, 1=pulse. Pulse 500=0°, 1500=90°, 2500=180° |
| `AT+MOTORR` | `servo` | Lire position |
| `AT+EARW` | `dir,?,?` | Oreilles (ex. `AT+EARW,1,2,2`) — code commenté |
| `AT+LEDOn` / `AT+LEDon` | `color?` | LED. Casse incohérente |
| `AT+LEDOff` | — | LED off |
| `AT+AG` | `0/1/2` | IMU acc / gyro / les deux |
| `AT+CLIFFR` / `AT+CLIFFD` | — | Lire / config cliff |
| `AT+VerR` | — | Version MCU |
| `AT+SNR` | — | SN |
| `AT+AGID` | — | ID IMU |
| `AT+FunCtr` | PowerMotion JSON | function 3 = power pieds, 5 = cliff/hang/ToF |

## 3. Mapping servos

| Moteur | Pièce |
|---|---|
| 1 | pied gauche |
| 2 | pied droit |
| 3 | jambe gauche |
| 4 | jambe droite |
| 5 | oreille droite |
| 6 | oreille gauche |

Marche « bigFoot » (`McuControlManager`) compose des `AT+MOTORW` par angles relatifs. `AT+MOVEW` délègue la figure au firmware — plus fiable si le code commenté est réactivé.

## 4. À corriger plus tard

- Dictionnaire AIDL `forward` vs AT string `FORWARD` vs int `1`.
- Typos `backend`, `tried`, `STAND_AT_EASE ` (espace), `onMcuCommandCommand`.
- `AT+LEDon` vs `AT+LEDOn`.
- Queue AT existe (`ATCommandControler`) mais `McuCommandControlManager.walks` est mort.
- Deux copies de `MCUConsts` (package `com.letianpai` et `...foot.consts`).
