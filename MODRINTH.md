# BetterEnd

### Renewable End Cities — every player gets the elytra, and the item frame stays an item frame.

**Please share your "If it would do [THING], I would use it" feedback!** [Join the Discord →](https://discord.gg/qwYcTpHsNC)

<br>

## Why BetterEnd?

The End is the most single-use dimension on a multiplayer server. The first player to reach an End Ship takes *the* elytra; everyone after them finds an empty frame and gutted chests.

The usual fix is to swap the ship's item frame for a vault block, then push a datapack and a key item onto every player. **BetterEnd doesn't touch the item frame.** The elytra stays exactly where Mojang put it, players punch it exactly like they always have — and every player gets their own.

| Problem | Solution |
|---------|----------|
| First player takes the only elytra | Per-player claims straight from the **vanilla item frame** |
| Elytra vaults need a datapack + key item | **Nothing extra** — no datapack, no dependencies, no resource pack |
| First player also empties every chest | Per-player **container loot**, Lootr-style |
| Griefers strip the towers and ship | Bounds-based **protection** per structure piece |
| Cities never come back | **Snapshots** — restore blocks on demand or each loot refresh |
| Setup overhead per city | **Auto-discovery** — cities register themselves from the server's structure data |

---

## Plug-and-Play Setup

There isn't any. Drop the jar in `plugins/`, restart, done — auto-discovery, per-player elytras, per-player loot and protection are all on by default.

```
1. Drop BetterEnd-x.y.z.jar into plugins/
2. Restart
3. There is no step 3
```

Cities register themselves as their chunks load, using the server's own structure data for exact bounds — ships included. Want to change something? `/betterend` opens a menu, or `/betterend setup` walks you through every setting in plain words in about two minutes. **You never have to open a config file.**

---

## Features

### Core Systems

- **Renewable elytra item frames** — punching the ship's frame (vanilla pick-up) puts a fresh elytra in your inventory while the frame, and its elytra, stays for the next player. Frames are protected from breaking and non-player damage; player-placed frames are never touched.
- **Claim modes** — once per ship (default), re-claimable after each loot refresh, or once per player across the whole server.
- **Optional claim cost** — free by default, or charge any item, custom/NBT items from other plugins included. Pick it from your own inventory in-game; the amount slider clamps to that item's max stack size.
- **Floating hint** above the ship frame showing the cost, or "Punch to claim" when it's free.
- **Auto-discovery** — End Cities register themselves as chunks load, at their exact structure bounds. Active immediately, no approval step. A startup sweep catches cities already loaded when the plugin enables.
- **Per-player container loot** — every player who opens a city container gets their own private copy, on a lazy per-city refresh timer so cities never all refresh at once. Player-placed chests keep vanilla behaviour.
- **Griefing protection** — towers, bridges and the ship are protected from breaking, placing and explosions. The void *between* pieces stays fully buildable, so builders aren't fenced out of the island.
- **Structure snapshots** — captured automatically on discovery, restorable on demand (`/betterend reset`) or automatically on each loot refresh (opt-in).
- **Dialog config menu** — `/betterend` opens native MC26 dialogs with sliders, toggles and choice buttons. Every change applies live and is written back to `config.yml`.
- **Guided setup tour** — `/betterend setup`, one setting per screen. Ops get a one-time reminder on join until it's completed or skipped.

<details>

<summary><strong>How per-player loot actually works</strong> — the refresh window, explained</summary>

Each city runs its own refresh cycle, and it's **lazy** — nothing ticks in the background:

- The first player to loot a city with no active cycle starts a fresh window for **that city**.
- Every player who opens a container during the window sees their own private copy of its contents.
- When the window elapses, the next player to loot triggers the refresh — all copies are cleared and a new window opens.

The default window is 12 hours (`loot.refresh-hours`). Set it to `0` to never refresh, and per-player copies persist indefinitely.

Because the cycle is per-city and starts on first use, a hundred discovered cities don't all refresh at the same moment, and cities nobody visits cost nothing.

</details>

<details>

<summary><strong>Protection details</strong> — why the void between towers stays buildable</summary>

Protection is **bounds-based per structure piece**, not one big region around the city. Any block inside a generated piece — tower, bridge, ship — is protected regardless of block type, and each piece is expanded by a small padding (default 3 blocks) to cover edge decoration and a thin shell.

The result: griefers can't strip the towers, but the empty space between pieces is still yours to build in. Ops with `betterend.bypass.protection` build freely, and a denied break or place shows an action-bar message rather than failing silently.

Explosion protection (creepers, TNT, others) is on by default and can be toggled independently of block-break protection.

</details>

<details>

<summary><strong>Snapshots & resets</strong> — bringing a looted city back</summary>

A gzip block snapshot is captured automatically when a city is discovered, so a reset target always exists without a manual step. A hard cell cap (default ~3M, far above any real city) protects memory on pathological structures.

- `/betterend reset <id>` restores the blocks **and** gives everyone fresh loot.
- `snapshot.auto-reset-on-refresh` does the same automatically on each loot refresh. It's **off** by default — a restore rewrites blocks and can be disruptive if players are standing inside.
- `/betterend resetloot <id> <player>` re-rolls loot for one player without touching blocks.
- `/betterend clearclaims <id>` makes the ship's elytra claimable again by everyone.

</details>

<details>

<summary><strong>Technical</strong> — Folia, dual database, AntiDupe integration</summary>

- **Folia support** — scheduler calls are routed to the regional schedulers; also runs on Paper and Purpur.
- **Dual database** — SQLite (default, zero setup) or MySQL with connection pooling.
- **Written in Kotlin** with coroutines; discovery, snapshots and database work stay off the main thread.
- **BetterAntiDupe integration** — claimed elytras are pre-stamped with the claimer's ownership tag when [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe) is installed, so renewable elytras don't trip duplicate detection. The cost picker also warns when you choose a tracked material.
- **Excluded worlds** — `discovery.excluded-worlds` leaves a second End-type world completely vanilla.

</details>

---

## Requirements

| Requirement | Version |
|-------------|---------|
| **Minecraft** | 26.1+ |
| **Server** | Paper, Folia, or Purpur |
| **Java** | 25+ |

> **Note:** BetterEnd is **26.x only**. It uses the native Dialog API and the server's structure data directly, neither of which exists on 1.21.x — there is no 1.21 build. If you're on 1.21, this plugin won't load.

**Dependencies: none.** No datapack, no library plugin, no resource pack.

---

## Reference

<details>

<summary><strong>Commands</strong></summary>

| Command | Description |
|---------|-------------|
| `/betterend` | Config menu (native dialogs) |
| `/betterend setup` | Guided first-time setup tour |
| `/betterend list` | List discovered End Cities |
| `/betterend info <id>` | City details |
| `/betterend tp <id>` | Teleport to a city |
| `/betterend snapshot <id>` | Capture the structure for restoration |
| `/betterend reset <id>` | Restore blocks + fresh loot for everyone |
| `/betterend resetloot <id> <player>` | Let one player loot the city fresh |
| `/betterend clearclaims <id>` | Make the ship's elytra claimable again by everyone |
| `/betterend delete <id>` | Unregister a city |
| `/betterend reload` | Reload `config.yml` |

</details>

<details>

<summary><strong>Permissions</strong></summary>

| Permission | Description | Default |
|------------|-------------|---------|
| `betterend.admin` | Admin commands + config menu | OP |
| `betterend.bypass.protection` | Build/break inside protected structures | OP |
| `betterend.discovery.notify` | Notice when a city is discovered | OP |

</details>

<details>

<summary><strong>Essential Configuration</strong></summary>

Defaults work out of the box — and everything here is editable in-game via `/betterend`. The settings servers actually tweak:

```yaml
elytra:
  claim-mode: per-ship     # per-ship | per-refresh | global
  cost:
    amount: 0              # 0 = free. Pick the item in-game.

loot:
  enabled: true            # per-player container loot
  refresh-hours: 12        # 0 = never refresh

protection:
  enabled: true
  piece-padding: 3         # blocks to expand each structure piece by

snapshot:
  auto-capture: true       # snapshot on discovery
  auto-reset-on-refresh: false   # also restore blocks each refresh

discovery:
  enabled: true
  excluded-worlds: []      # leave a second End world fully vanilla
```

</details>

---

## Support

- **[Discord](https://discord.gg/qwYcTpHsNC)** — support, announcements, feature requests.
- **[GitHub Issues](https://github.com/ESMP-FUN/BetterEnd/issues)** — bug reports.
- **[Source Code](https://github.com/ESMP-FUN/BetterEnd)** — source-available, non-commercial.

---

## Target Audience

- **Survival & SMP servers** — the End stops being a race won once, on day three.
- **Long-running worlds** — cities that reset are cities worth revisiting.
- **Anarchy-adjacent / open-build servers** — protection that covers the structure without fencing off the island.
- **Anyone tired of empty item frames.**

---

<div align="center">

**Paper 26.1+** · **Folia** · **Java 25+** · **No dependencies**

Made with Kotlin by [darkstarworks](https://github.com/darkstarworks)

---

This plugin is free and actively maintained.

If you have questions or would like to just say Hi, come [join the Discord](https://discord.gg/qwYcTpHsNC).

Rather stay silent? (Anonymous) donations are also **VERY** welcome: https://ko-fi.com/darkstarworks

[![Servers](https://img.shields.io/endpoint?url=https%3A%2F%2Ffaststats.dev%2Fapi%2Fshields%2Fbetter-end%3Fmetric%3Dservers%26color%3Dblueviolet%26icon%3D1&style=flat)](https://faststats.dev/project/better-end)

</div>
