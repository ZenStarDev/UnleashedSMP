# UnleashedSMP

One of the most complete Paper plugins for SMP servers. UnleashedSMP delivers a modular,
production-ready event engine, a lucky system, mutant mobs, a modern GUI, extensive
configuration, and maximum compatibility with the Paper plugin ecosystem.

## Features

- **Event Engine** — 31+ isolated, configurable events with weights, cooldowns, durations,
  broadcasting, and an auto-roll scheduler.
- **Lucky System** — weighted good/bad/neutral outcomes with rewards, punishments, economy
  integration, and cooldowns.
- **Mutant Mobs** — randomly spawned, scaled, named, glowing mutants with boss bars and custom loot.
- **GUI** — modern inventory GUI with pagination, categories, and permission filtering.
- **Database** — SQLite / MySQL / MariaDB via HikariCP with a DAO layer and migrations.
- **Integrations** — auto-detected soft depends: Vault, PlayerPoints, LuckPerms, PlaceholderAPI,
  WorldGuard, WorldEdit, Citizens, MythicMobs, and more.
- **Localization** — `en_US` and `th_TH` with MiniMessage and PlaceholderAPI support.
- **API** — register custom events and control the plugin programmatically.

## Requirements

- Java 21
- Paper **1.20+** (1.20, 1.20.4, 1.21, 1.21.4 supported)
- (Optional) any of the supported soft-depend plugins

## Building

Build for all versions (default profile = 1.21.4):

```bash
mvn clean package
```

Build for a specific version:

```bash
mvn clean package -P 1-20-4
mvn clean package -P 1-21
```

Available profiles:
- `1-21-4` (default)
- `1-21`
- `1-20-4`
- `1-20`

The output jar will be named `UnleashedSMP-<version>-<api>.jar`, e.g. `UnleashedSMP-1.0.2-1.21.jar`.

## License

MIT — see [LICENSE](LICENSE).
