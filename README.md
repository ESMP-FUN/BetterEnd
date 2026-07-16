# BetterEnd

Turn Minecraft's End Cities from one-and-done loot runs into renewable, multiplayer-ready content. Per-player elytras straight from the ship's item frame, per-player chest loot, griefing protection, and full structure snapshots — all discovered and managed automatically, with no per-city setup.

## The problem it solves

The End is the most single-use dimension on a multiplayer server. The first player to reach an End Ship takes *the* elytra; everyone after them finds an empty frame and gutted chests. Existing "elytra vault" solutions replace the iconic item frame with a vault block and force a datapack + key item onto every server.

BetterEnd keeps everything vanilla-looking: the elytra stays in its item frame, players punch it exactly like they always have — and every player gets their own. Chest loot is per-player, cities protect themselves from griefing, and the whole structure can snapshot and restore.

## What you can do

* **Renewable elytras, no vault block** — the ship's elytra item frame stays an item frame. Punching it (vanilla behaviour) puts a fresh elytra in your inventory and the frame stays for the next player.
* **Claim rules you choose** — once per ship (default), re-claimable after each loot refresh, or once per player total. Picked from a dialog, no YAML.
* **Optional claim cost** — free by default, or charge any item (custom items from other plugins included): pick it from your own inventory in-game, set the amount on a slider that respects the item's stack size (16 for ender pearls, 1 for a bed…).
* **Auto-discovery** — End Cities register themselves as their chunks load, using the server's own structure data for exact bounds. Ships included, automatically.
* **Per-player chest loot** — every player who opens a city chest sees their own private copy, refreshed per city on a lazy timer. Chests stay chests.
* **Griefing protection** — the towers, bridges and ship are protected from breaking, placing, and explosions; the void between them stays fully buildable.
* **Snapshots** — capture a city's structure on discovery and restore it on demand (or automatically each loot refresh).
* **Dialog config menu** — `/betterend` opens native MC26 dialogs: sliders, toggles and choice buttons. No resource pack, no config-file spelunking.
* **Guided setup** — `/betterend setup` walks through every setting in plain words, one screen at a time, in about two minutes. The defaults already work; the tour is optional.
* **AntiDupe-friendly** — claimed elytras are pre-stamped with the claimer's [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe) ownership tag when it's installed. No warnings, no false positives.

## Requirements

* **Minecraft 26.1+** (Paper, Folia, or Purpur)
* **Java 25+**

BetterEnd is standalone — no datapack, no dependencies.

## Quick start

1. Drop `BetterEnd-x.y.z.jar` into `plugins/` and restart.
2. That's it — cities register as players find them, elytras are renewable, loot is per-player.
3. Optionally run `/betterend setup` for the guided tour, or `/betterend` for the menu.

## Commands

| Command | What it does |
| --- | --- |
| `/betterend` | Config menu (dialogs) |
| `/betterend setup` | Guided first-time setup tour |
| `/betterend list` | List discovered End Cities |
| `/betterend info <id>` | City details |
| `/betterend tp <id>` | Teleport to a city |
| `/betterend snapshot <id>` | Capture the structure for restoration |
| `/betterend reset <id>` | Restore blocks + fresh loot for everyone |
| `/betterend resetloot <id> <player>` | Let one player loot the city fresh |
| `/betterend clearclaims <id>` | Make the ship's elytra claimable again by everyone |
| `/betterend delete <id>` | Unregister a city |
| `/betterend reload` | Reload config.yml |

All admin commands require `betterend.admin` (default: op).

## Permissions

| Permission | Default | What it grants |
| --- | --- | --- |
| `betterend.admin` | op | Admin commands + config menu |
| `betterend.bypass.protection` | op | Build/break inside protected structures |
| `betterend.discovery.notify` | op | In-game notice when a city is discovered |

## License

Source-available, non-commercial — see [LICENSE](LICENSE). Contributions welcome, see [CONTRIBUTING.md](CONTRIBUTING.md).
