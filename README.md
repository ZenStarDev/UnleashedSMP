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
- Paper 1.21.x
- (Optional) any of the supported soft-depend plugins

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/unleashed` | `unleashed.command.unleashed` | Main command / GUI |
| `/event <name>` | `unleashed.command.event` | Start an event |
| `/eventall <name>` | `unleashed.command.eventall` | Start an event for everyone |
| `/lucky` | `unleashed.command.lucky` | Roll the lucky system |
| `/reload unleashed` | `unleashed.command.reload` | Reload configuration |
| `/debug` | `unleashed.command.debug` | Toggle debug mode |
| `/help` | `unleashed.command.help` | Show help |
| `/admin` | `unleashed.command.admin` | Admin controls |

## Building

```bash
mvn clean package
```

The shaded jar is produced in `target/`.

## License

MIT — see [LICENSE](LICENSE).
