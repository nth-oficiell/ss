# Auto Totem (Fabric)

Mod client Auto Totem pour Minecraft : dès que le totem de ta main secondaire pop, il est instantanément remplacé par un autre depuis ton inventaire.

## 📦 18 versions compilées

| Version MC | Jar |
|---|---|
| 1.20.6 | `dist/auto-totem-1.20.6-1.1.0.jar` |
| 1.21 → 1.21.8 | `dist/auto-totem-1.21.x-1.1.0.jar` |
| 1.21.9 → 1.21.11 | `dist/auto-totem-1.21.9+-1.1.0.jar` |
| 26.1 / 26.1.1 / 26.1.2 | `dist/auto-totem-26.1.x-1.1.0.jar` |
| 26.2 / 26.3 | `dist/auto-totem-26.2+-1.1.0.jar` |

## Fonctionnalités

- 🛡️ Remplacement automatique du totem en main secondaire (hotbar prioritaire)
- 🖥️ **Menu in-game** : touche **O** (par défaut) ouvre le menu Auto Totem
- ✅ Dans le menu : bouton ON/OFF, slider **Délai (0-100 ticks)**, rebind de la touche d'ouverture
- ⏱️ Délai aussi configurable via `config/autototem.json`
- 🎯 Indicateur **AutoTotem [Xt]** sur le HUD (vert = actif, gris = inactif)
- 🇫🇷🇬🇧 Traductions FR / EN

## Architecture

- `code-yarn/` + `code-screen-y1/` + `hud-legacy|hud-tick/` : noyau 1.20.6 → 1.21.8 (Yarn)
- `code-screen-y2/` + `hud-tick2/` : écran/clavier 1.21.9 → 1.21.11 (`KeyInput`, `KeyBinding.Category`)
- `code-moj|code-moj2/` + `hud-moj/` : jeu non-obfusqué 26.x (noms officiels, Java 25)
- `versions/<mc>/` : un projet Gradle par version (Loom adapté : 1.7.4 / 1.11.1 / 1.17.21 / 1.19.4)

## Build

```bash
# Yarn era (<= 1.21.8) : Gradle 8.9
gradle -p versions/1.21.1 build
# 1.21.9 → 1.21.11 : Gradle 9.9.2
gradle -p versions/1.21.11 build
# 26.x : Gradle 9.9.2 + JDK 25
gradle -p versions/26.3 build
```

## Installation

1. Installer le [Fabric Loader](https://fabricmc.net/use/installer/) pour ta version
2. Ajouter la [Fabric API](https://modrinth.com/mod/fabric-api) dans `mods/`
3. Mettre le jar de ta version (dossier `dist/`) dans `mods/`

## Compatibilité

- `environment: client`, aucun mixin, 100 % événements Fabric API
- Config persistée dans `config/autototem.json` (enabled, delayTicks)
