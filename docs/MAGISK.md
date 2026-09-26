# Magisk sur RUX

But : `su` depuis ADB (ROM user, `adb root` impossible) pour hosts + iptables vers le mock.
L’app Magisk est déjà installée (`com.topjohnwu.magisk` dans TaskService).

## 1. Vérifier que le root est réel

```text
adb shell su -c id
```

Attendu : `uid=0(root)`. Si « su: not found » ou timeout : l’APK est là, le **boot n’est pas patché**. Dans l’app Magisk : Installation → patcher `boot.img` → `fastboot flash boot magisk_patched.img` (bootloader déjà `flash.locked=0`).

Première popup Magisk : autoriser **Shell** / `com.android.shell` en permanence.

## 2. Réglages app (petit écran robot)

- Zygisk : off tant qu’on n’en a pas besoin
- DenyList : vide (ne pas cacher Emqx / LtpNetWork)
- Superuser : Shell = granted, timeout 0 ou « forever »

L’UI Magisk est illisible sur 2" : piloter depuis le PC.

```text
adb shell su -c magisk --sqlite "SELECT * FROM policies;"
adb shell su -c "magisk --denylist disable"
```

## 3. Rediriger le cloud vers le PC (IP_PC = ton LAN)

HTTP :

```text
adb shell su -c "resetprop persist.sys.region.language en"
adb shell su -c 'echo "IP_PC global-robot-api.letianpai.com" >> /system/etc/hosts'
# si /system ro :
adb shell su -c "mount -o rw,remount /"
```

Mieux (systemless) : module Magisk `system/etc/hosts`.

MQTT (Emqx ignore le DNS, IP fixe `43.153.69.45:1883`) :

```text
adb shell su -c "iptables -t nat -A OUTPUT -p tcp -d 43.153.69.45 --dport 1883 -j DNAT --to-destination IP_PC:1883"
```

Persistance : script `/data/adb/service.d/01-rux-mock.sh` + `chmod 755`.

## 4. Contrôle

```text
adb shell su -c "getprop persist.sys.hardcode"
adb shell su -c "cat /data/data/com.letianpai.emqxservice/shared_prefs/*" | findstr host
adb logcat -s EMQX
```

Log attendu après reboot : `Connected to: tcp://IP_PC:1883` au lieu de `43.153.69.45`.
