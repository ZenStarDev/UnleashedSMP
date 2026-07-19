# Changelog

All notable changes to UnleashedSMP are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.3] - 2026-07-19

### Added
- Multi-version support via Maven profiles (Paper 1.20, 1.20.4, 1.21, 1.21.4).
- Dynamic `api-version` in plugin.yml per build profile.

## [1.0.2] - 2026-07-19

### Added
- Persist curse/luck levels to database. Luck now survives reloads and server restarts.

## [1.0.1] - 2026-07-19

### Fixed
- Mutant bossbar now correctly parses MiniMessage tags like `<dark>name</dark>`.

## [1.0.0] - 2026-07-19

### Added
- Core bootstrap, lifecycle, and dependency loader.
- Configuration manager with hot reload for 12 config files.
- Event engine with 31 default events and auto-roll scheduler.
- Lucky system with weighted outcomes.
- Mutant mob system with scaling and boss bars.
- Modern inventory GUI (main menu, events, lucky, mutants).
- Database layer (SQLite/MySQL/MariaDB) with HikariCP and DAO pattern.
- Integration hooks for popular plugins (auto-detected).
- Localization (en_US, th_TH).
- PlaceholderAPI expansion.
- Developer API.
- JUnit/Mockito test scaffolding.
- Curse/luck system: per-player luck levels affecting event targeting and auto-roll chance.
- Event countdown: configurable pre-start countdown with broadcast warnings and admin controls.
- Admin commands: `/admin cancelcountdown`, `/admin pending`, `/admin luck`.

### Fixed
- 19 event task leaks: repeating tasks now cancel properly on event stop.
- GUI main menu and event list click handling were broken.
- `DoubleDropsEvent` now respects configured multiplier.
- `LuckyDropEvent` now respects configured multiplier.
- `BossSpawnEvent` invalid entity type no longer crashes and leaks.
- `PvpFrenzyEvent` now toggles PvP on/off.
- `BloodMoonEvent` now applies mob multiplier and stops buff task on end.
- Event reload now restarts auto-roll scheduler.
- `EventManager.isEnabled` permission logic now works when no players are online.
- Database DAO calls are now null-safe when DB is unavailable.
- Command classes now parse MiniMessage before sending to players.
