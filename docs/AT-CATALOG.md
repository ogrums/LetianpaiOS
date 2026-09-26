# Catalogue AT MCU (LRAM.1.1.31)

Source : strings `Project.hex`. Fin de trame `\r\n`. ACK `AT+RES,ACK` puis souvent `AT+RES,end`.

## MOVEW — figures

Firmware : `wrong move comd %d, move cmd is[1,100]`.
Android envoie `AT+MOVEW,<number>,<step>,<speed>` (3 args). Le binaire a aussi le format `AT+MOVEW,%d,%d`.
Speed vu : `value is[1,10]`.

Pendant l’exécution le MCU remonte `AT+INT,MOVEW,%d,%d` et `AT+INT,waggle,1`.

Figures **confirmées** logcat (number = 1er arg) :

| n | nom | genre |
|---:|---|---|
| 0 | 立正 stand | pose (accepté malgré [1,100]) |
| 5 / 6 | crabe G / D | loco |
| 7 / 8 | secoue jambe G / D | geste |
| 12 | pied D levé | geste |
| 20 | 稍息 repos | pose |
| 21 / 22 | tourne G / D | rot |
| 23 | pieds joints | pose |
| 64 | petit recul | loco |
| 65 / 66 | secousse rapide G / D | geste |
| 98 | marche alternée | loco |

Les autres `1–4, 9–11, 13–19, 24–63, 67–97, 99–100` existent côté firmware. Les tester une à une via MQTT `number` ou AT brut. Ne pas inventer les noms.

## 61 verbes

### Locomotion / servos
| verbe | params firmware | notes |
|---|---|---|
| MOVEW | cmd[1,100], step, speed[1,10] | figure |
| MOTORW | motor[1,6], kind[0,1], value pulse[500,2500] ou angle | |
| MOTORR | motor | `AT+RES,motor,%d,%d` |
| EARW | ear_cmd[0,3], … | `AT+EARW,%d,%d` |
| Mset / Mreset | calib pose | |
| MCalh/l/m/p | calib high/low/mid/pos | |
| Msaveh/l/m | sauver calib | |
| Munlock / Mread | | |
| MOTA | | |

### LED antennes
| LEDOn | color[1–9] (+ status) | 1 red 2 green 3 blue … + orange/purple/white/black/cyan/yellow |
| LEDOff | aucun | `AT+RES,off` |

### IMU / ToF / cliff / IR / touch
| FiAGW / FiAGR | cmd[0,4] vu | gyro/acc on fil `AT+FiAGW,2,10` |
| AG / AGID / AGCal / AGCalR | type capteur [0,2]/[0,3] | `acc` `gyro` `a+g` |
| MAGR | magneto | |
| TofR TofSet TofCal TofCalR | TofSet vu `1,800` | INT `tof,<mm>` |
| CLIFFR CLIFFW CLIFFD | danger_num[50,300] | INT `cliff` ; RES `safe`/`danger` |
| IRstart IRstop | | INT `IR,%d` |
| AWStart AWStop AWR | | INT `AW,%d,%d,%d` touch AW9310x |
| THR BATR | seuil / batterie | |

### Système
| VerR DateVer SNR | version `LRAM.1.1.31`, SN | |
| Reset | `MCU will reset after 1s` | |
| PowerOff | | |
| FunCtr | fun[1,4] doc hex ; fil : `3,1` pieds `5,1` cliff/ToF | |
| Gsys CfgR CfgW | | |
| FMCW FMCR FMCDUMP | flash | |
| LADDR Did Lid Pid Tid Atestid | ids / addrs EEPROM | |
| RTCr RTCw | RTC | |
| INT | **sortant** MCU | tof light cliff suspend touch MOVEW waggle down AW IR |
| RES | réponses | ACK / end / Err |

Ne pas envoyer les `MCal*` / `FMCW` / `Reset` / `PowerOff` sans intention : calib et flash.
