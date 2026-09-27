# Codes motion RUX

Confirmé `logcat6.txt` (2026-09-27) : `SerialPortLog write data`.

```
MQTT {number, step, speed}  ==  AT+MOVEW,<number>,<step>,<speed>\r\n
```

Le `number` MQTT **est** le cmd firmware.

## Vu sur UART (vrai runtime)

| number | motion_name | FR | AT écrit |
|---:|---|---|---|
| 0 | 立正 | stand | `AT+MOVEW,0,1,3` |
| 5 | 螃蟹左 | crabe G | `AT+MOVEW,5,1,3` |
| 6 | 螃蟹右 | crabe D | `AT+MOVEW,6,1,3` |
| 7 | 左抖腿 | secoue jambe G | `AT+MOVEW,7,1,3` |
| 8 | 右抖腿 | secoue jambe D | `AT+MOVEW,8,1,3` |
| 12 | 右跻脚 | pied D levé | `AT+MOVEW,12,1,3` |
| 20 | 稍息 | repos | `AT+MOVEW,20,1,3` |
| 21 | 左转 | tourne G | `AT+MOVEW,21,1,3` (une fois step=2) |
| 22 | 右转 | tourne D | `AT+MOVEW,22,1,3` |
| 23 | 并脚 | pieds joints | `AT+MOVEW,23,1,3` |
| 64 | 小后退 | petit recul | `AT+MOVEW,64,1,2` |
| 65 | 快抖左脚 | secousse rapide G | `AT+MOVEW,65,1,3` |
| 66 | 快抖右脚 | secousse rapide D | `AT+MOVEW,66,1,3` |
| 98 | 交替向前走 | marche | `AT+MOVEW,98,1,2` |

ACK MCU : `AT+RES,ACK` puis `AT+RES,end`.
Capteurs montants : `AT+INT,tof,<mm>` `light` `cliff` `suspend` `touch`.

## Catalogue officiel RobotSDK (boutique, 1–80)

Source : [store RobotSDK](https://store.letianpai.com/blogs/%E6%96%B0%E9%97%BB/rux-robotsdk) + AAR 2.2.
Détail : [ROBOTSDK.md](ROBOTSDK.md).

Constantes AAR : avant=`63` arrière=`64` crabe `5`/`6` pivot `3`/`4` jambe G `7`.
Exemple page : `ActionMessage.set(63, 2, 3)`.

La boutique numérote **1**=walking forward, **2**=back, alors que le cloud a envoyé **98** pour la marche et **0** pour stand. Ne pas écraser la table UART avec la table marketing.

Extrait utile mock : 1/2 marche, 3/4 pivot, 5/6 crabe, 21/22 spin ~20°, 63/64 « forward/back 2 », 80 dance twist.
