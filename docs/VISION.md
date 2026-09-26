# Vision RUX / GeeUI (ogrums)

Date : 2026-09-26
Suivi détaillé + graphes : [ROADMAP.md](ROADMAP.md)

## Objectif produit

Reprendre le robot **sans dépendre du cloud Letianpai** pour l’usage quotidien.

- Mode **offline** : marcher, faces, sons, écran, voix FR/EN.
- Mode **cloud optionnel** : le robot peut encore parler à un serveur (le nôtre ou l’officiel), ce n’est plus obligatoire au boot.
- Couper ou ignorer les spécificités Chine (Weibo/fans CN, Lex AWS CN, news/stock CN, MiJia si hors scope).
- Voix : **STT + TTS français et anglais** (pas le pipeline Lex/Polly/Sphinx-EN seul).

Méthode : vision → analyse → décision → code + PR. Source = forks `ogrums/*` + APK ROM. Hardware = robot + ADB + ROM root. Git = multi-repos.
