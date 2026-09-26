# Codes motion RUX

Deux espaces de noms. Ne pas les mélanger.

```
MQTT d.number  →  TaskService / MCU  →  AT+MOVEW,<cmd>,<steps>,<speed>
CommandLib motion="forward"  →  autre table (AIDL local)
```

## 1. `d.number` vu sur MQTT (app téléphone → robot)

Toujours `motion:"null"`, `step:1`. C’est **ce** tableau qu’il faut mocker.

| number | motion_name | FR | speed vu | Genre |
|---:|---|---|---:|---|
| 0 | 立正 | garde-à-vous / stand | 3 | pose |
| 5 | 螃蟹左 | crabe gauche | 3 | locomotion |
| 6 | 螃蟹右 | crabe droite | 3 | locomotion |
| 8 | 右抖腿 | secoue jambe D | 3 | geste |
| 11 | 左跷脚 | pied G en l’air | 3 | geste |
| 12 | 右跷脚 | pied D en l’air | 3 | geste |
| 21 | 左转 | tourne G | 3 | rotation |
| 22 | 右转 | tourne D | 3 | rotation |
| 23 | 并脚 | pieds joints | 3 | pose |
| 64 | 小后退 | petit recul | 2 | locomotion |
| 65 | 快抖左脚 | secousse rapide pied G | 3 | geste |
| 66 | 快抖右脚 | secousse rapide pied D | 3 | geste |
| 98 | 交替向前走 | marche alternée avant | 2 | locomotion |

Pas vus dans cette capture (existent probablement dans l’app) : 1–4, 7, 9–10, 13–20, 24–63, 67–97. Ne pas inventer.

Mock marche :

```json
{"cmd":"controlMotion","d":{"motion":"null","motion_name":"交替向前走","number":98,"step":1,"speed":2},"et":1999999999999}
```

Stop / stand : `number:0` speed 3.

## 2. AT firmware `AT+MOVEW,cmd,steps,speed`

Documenté dans CommandLib / MCU-VOCAB — **cmd AT ≠ number MQTT** tant qu’on n’a pas le mapping firmware sous les yeux.

| cmd AT (doc code) | Sens |
|---:|---|
| 0 | stand |
| 1 | forward |
| 2 | back |
| 3 | left |
| 4 | right |
| 5–6 | crab |
| 7–24 | figures |
| 29–60 | « random » |

Servos si on bypasse MOVEW : 1 piedG 2 piedD 3 jambeG 4 jambeD 5 oreilleD 6 oreilleG.

## 3. Strings AIDL CommandLib (`d.motion`)

Utilisées en local, **pas** par l’app dans le pcap : `forward`, `backend` (typo back), `left`, `right`, `leftRound`, `rightRound`, `setStraight`, `takeEasy`, `turnRound`, `pettish`, `angry`, `run`, `cheers`, `tried`, `shakeLeg`.

Si le robot réagit à `motion:"forward"` en AIDL mais ignore ce champ en MQTT, c’est normal : deux parseurs.
