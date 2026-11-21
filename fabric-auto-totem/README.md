# Auto Totem (Fabric)

Mod client pour Minecraft **1.21.1** : dès que le totem de ta main secondaire pop, il est instantanément remplacé par un autre depuis ton inventaire.

## Fonctionnalités

- 🛡️ Remplacement automatique du totem en main secondaire (hotbar prioritaire)
- ⏱️ **Délai configurable** (0-100 ticks) : touches **[** et **]** en jeu, ou `config/autototem.json`
- ⌨️ Touche **O** pour activer/désactiver
- 🎯 Indicateur **AutoTotem [Xt]** sur le HUD (vert = actif, gris = inactif)
- 🇫🇷🇬🇧 Traductions FR / EN

## Build

```bash
cd fabric-auto-totem
gradle build
```

Le jar sort dans `build/libs/auto-totem-1.0.0.jar`.

## Installation

1. Installer le [Fabric Loader](https://fabricmc.net/use/installer/) pour 1.21.1
2. Ajouter la [Fabric API](https://modrinth.com/mod/fabric-api) dans `mods/`
3. Mettre le jar du mod dans `mods/`

## Compatibilité

- `environment: client` uniquement, autorisé sur la plupart des serveurs modérés
- Aucun mixin, 100 % événements Fabric API → stable entre les versions
