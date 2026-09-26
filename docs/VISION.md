# Vision RUX / GeeUI (ogrums)

Date : 2026-09-26
Statut : cadrage — pas encore de changement runtime.
Robot : Letianpai RUX, Android 11 + Debian 10, MCU GD32L233RC, bipède bureau.

## Objectif

Reprendre l’ensemble des forks `ogrums/*`, analyser chaque module, garder une carte globale, puis livrer des améliorations en PR testables sur robot (ADB + ROM root).

## Repos pris en compte

| Repo | Rôle | Couche |
|---|---|---|
| LetianpaiOS | Launcher HOME + AudioService (SFX) | Shell |
| GeeUIDesktop | Entrée apps / bureau | UI |
| GeeUIFace | Visage / modes robot | UI |
| GeeUITime | Horloge | UI |
| GeeUIGuide | QR / premier boot | UI |
| GeeUISetting | Réglages | UI |
| GeeUIWiFiConnector | Wi-Fi | UI |
| GeeUIInstaller | Installation APK | Système |
| GeeUIBase | Lib de base | Shared |
| GeeUIComponents | Widgets / canal comm (nom officiel : GeeUIComponets) | Shared |
| LetianpaiService | Bus AIDL inter-apps | Bus |
| GeeUITaskService | Orchestration tâches | Bus |
| GeeUIMcuService | UART / AT+FunCtr / marche / oreilles | Motion |
| MCU | Firmware GD32 + PDF protocole SOC↔MCU | Motion |
| GeeUIAIAudioService | Assistant vocal | IA |
| LtpNetWork | Réseau | IA / cloud |
| third_party_demo | Démo LLM tiers (Go) | IA |
| DemoForRobotSDK | SDK Kotlin | SDK |
| GeeUI_ROM | Image / docs ROM | OS |

LetianpaiOS compile le launcher et les SFX. Il ne compile pas le MCU ni le bus AIDL. `RobotService` démarre des packages externes (MCU, task, EMQX, speech).

## Architecture runtime

```
[App téléphone LeTianPai]     [Cloud / GPT / EMQX]
            |                            |
            v                            v
     GeeUI (Android 11)  ↔  Debian 10 (dual boot)
            |
   LeTianPaiLauncher  (HOME)
            |
   Audio SFX | Speech (rhj / lex) | Face | Settings | Desktop
            |
   LetianpaiService  (AIDL)
            |
   GeeUITaskService     GeeUIMcuService
            |                    |
            |             Serial JNI → AT+FunCtr
            |                    |
            |             MCU GD32L233RC
            |              servos jambes + oreilles
            |              cliff / ToF / hang
            v
        GeeUI_ROM / system uid
```

Contrats déjà identifiés :
- `PowerMotion` JSON `{"function":3,"status":0}` — puissance servos pieds.
- `function` 5 — cliff / hang / ToF.
- GeeUIMcuService traduit ça en `AT+FunCtr` via JNI série.
- Speech : `com.rhj.speech` (ZH) / `com.geeui.lex` (EN), pas dans LetianpaiOS.

## Choix git : pas un monorepo unique

Décision : **rester multi-repos**, avec ce fichier comme carte. Pas de fusion ROM + firmware C + apps Android + démo Go.

### Pourquoi un monorepo unique n’est pas le bon défaut ici

Avantages réels d’un monorepo :
- un seul clone, un seul changelog, versions alignées ;
- refactor AIDL / packages cross-app dans une PR ;
- CI unique, catalog Gradle unique.

Inconvénients plus lourds sur *ce* projet :
- **contexte agent** : ROM + MCU Keil + 15 apps Android = arbre énorme. Chaque analyse tire du bruit.
- **parallèle** : une branche monorepo se marche dessus ; 19 forks permettent N PR indépendantes.
- **natures différentes** : AGP/Java 17 ≠ firmware Keil ≠ Debian/ROM ≠ Go LLM.
- **upstream** : l’org Letianpai-Robot est déjà éclatée ; un monorepo casse le rebase fork ← officiel.
- **build** : GeeUI_ROM n’a rien à faire dans le même `./gradlew` que Face.

### Ce qu’on fait à la place (fédération)

1. **Garder 19 repos.** Un chantier = un repo (ou deux si contrat AIDL + client).
2. **Carte unique** = ce fichier (+ mises à jour ici, pas 19 README divergents).
3. **Plus tard seulement**, si la dette Gradle le justifie : *composite build* des libs partagées (`GeeUIBase` + `GeeUIComponents`) consommées par les apps. Pas la ROM, pas le MCU C.
4. **Jamais** mêler `GeeUI_ROM` et `MCU/` dans le même workspace d’analyse agent.

Règle de contexte pour les agents : ouvrir seulement le repo du chantier + le contrat voisin (AIDL ou `CommandLib`). Ne pas indexer les 19 arbres.

## Backlog d’amélioration (priorisé, pas commencé)

1. Cartographie contrats AIDL + commandes AT (LetianpaiService, GeeUIMcuService, MCU PDF).
2. Boot launcher : délais magiques 200 ms / 1 s, liste d’apps en dur, Mi IoT hors région `en`.
3. Locomotion : WalkManager / Servo / cliff — fiabilité bureau + API plus claire.
4. IA : séparer SFX, assistant, LLM tiers ; réduire les APK speech éclatés.
5. Toolchain apps : AGP 8 / Java 17 déjà dans LetianpaiOS et GeeUIFace ; aligner les autres forks.
6. Tests : aujourd’hui surtout `CommandLib`. Ajouter parseurs AT, ordre de boot, régression JSON PowerMotion.
7. Privileges : `sharedUserId=android.uid.system` release vs debug émulateur.

## Méthode de travail

1. Vision (ce doc).
2. Analyse module par module, un repo à la fois.
3. Décision + PR sur le fork concerné.
4. Flash / `adb install` sur robot root, pas seulement émulateur.

Prochain module proposé : **LetianpaiService + GeeUIMcuService** (contrats), puis launcher boot.
