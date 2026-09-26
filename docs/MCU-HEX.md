# Firmware MCU `Project.hex` (LRAM.1.1.31)

Intel HEX, flash `0x0800D000`–`0x08035C97` (~163 Ko), entry `0x0800D155`.
Build string : `LRAM.1.1.31` / `Dec 6 2023`.
IMU QMI8658, capotactile AW9310x, 6 servos.

Ghidra non nécessaire pour le protocole : tout l’AT est en ASCII.

## Conversion

```
objcopy -I ihex -O binary Project.hex project.bin
strings -n 6 project.bin | grep AT+
```

Python (si pas d’objcopy) : records type 0 + type 4 (`0800xxxx`).

## AT reconnus (61 verbes)

Mouvement : `MOVEW` `MOTORW` `MOTORR` `EARW` `Mset` `Mreset` `MCal*` `Msave*` `Munlock`
LED : `LEDOn` `LEDOff` — couleurs `1 red, 2 green, 3 blue ~9` + réponses `red/orange/purple/white/blue/black/cyan/green/yellow on`
Capteurs : `FiAGW/R` `AG` `CLIFF*` `Tof*` `MAGR` `BATR` `INT` (sortant MCU)
Système : `VerR` `SNR` `Reset` `PowerOff` `FunCtr` `Gsys` `DateVer` `CfgR/W` `FMC*`

`MOVEW` format firmware aussi `AT+MOVEW,%d,%d` (2 args) ; Android envoie 3 (`cmd,step,speed`). Le MCU accepte les deux.

`EARW` : `ear_cmd is[0,3]`.
Servos `MOTORW` : motor `[1,6]`, kind `[0,1]` (angle/pulse), pulse `[500,2500]`.

## `AT+INT` (MCU → SoC)

`tof` `light` `cliff` `suspend` `touch` `MOVEW` `waggle` `down` `AW` `IR`

## logcat.txt (autre session)

Boot UART : `FunCtr,3,1` `TofSet,1,800` `FunCtr,5,1` (fonctions 3=pieds, 5=cliff/ToF — MCU-VOCAB).
Une marche : `AT+MOVEW,98,1,2`.
MQTT : surtout `controlFace` / `changeShowModule` / `changeMode` / `controlSendWord` — **pas d’AT**. Visage = écran Android, pas le GD32.
Aucun `LEDOn` dans ce log.
