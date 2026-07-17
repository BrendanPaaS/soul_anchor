# Soul Anchor

A respawn anchor for Minecraft 1.12.2 (Forge).

Adds a soul anchor block that lets you set your respawn point in the Nether.
Craft it with a nether star surrounded by 8 obsidian, and it drops the obsidian back when broken.
Right click with glowstone to charge it up to 4 times, and right click again to set your respawn — each death uses up one charge, and it glows brighter the more charges it holds.
Use a charged one outside the Nether and it explodes, which is intentional game design.

## Usage

1. Craft: nether star surrounded by 8 obsidian.
2. Place it in the Nether.
3. Right click with glowstone to add charges (up to 4).
4. Right click with an empty hand to set your respawn point.
5. Shift + right click with an empty hand to check charges.
6. Each respawn at the anchor consumes one charge. At zero it goes dark and you respawn normally.

## Config

| Option | Default | Description |
|---|---|---|
| `dimensionWhitelist` | `-1` (Nether) | Dimension IDs where the anchor can be used |
| `activationItem` | `minecraft:glowstone` | Item consumed per charge; empty = free full charge on first click |
| `maxCharges` | `4` | Maximum charges the anchor holds |
| `intentionalGameDesign` | `true` | Explode when used outside whitelisted dimensions |
| `bedsSetSpawn` | `true` | If false, beds no longer set your respawn point |

Editable in-game via the mod config menu, or in `config/soulanchor.cfg`.

## Building

Requires JDK 25 for Gradle (the mod itself targets Java 8 / MC 1.12.2).

```
gradlew build
```

Jar is output to `build/libs/`.

## License

MIT
