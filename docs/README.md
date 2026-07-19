# Welcome

Turn Minecraft's End Cities from one-and-done loot runs into renewable, multiplayer-ready content. Per-player elytras straight from the ship's item frame, per-player chest loot, griefing protection, and full structure snapshots — all discovered and managed automatically, with no per-city setup.

***

## The problem it solves

The End is the most single-use dimension on a multiplayer server. The first player to reach an End Ship takes _the_ elytra; everyone after them finds an empty frame and gutted chests.

The usual fix is to replace the ship's item frame with a vault block and push a datapack plus a key item onto every player. BetterEnd doesn't touch the item frame. The elytra stays exactly where Mojang put it, players punch it exactly like they always have — and every player gets their own.

***

## Drop it in and forget it

BetterEnd is built to need no configuration. Install it, restart, and every End City on your server is renewable, protected, and per-player — with no per-city setup, no commands to run, and no file to edit.

The settings that exist are few and deliberate, and every one of them has a default that works. `/betterend` opens a menu if you want to change something; you're not expected to.

***

## What you can do

* **Renewable elytra item frames** — punching the ship's frame gives a fresh elytra while the frame, and its elytra, stays for the next player.
* **Claim rules you choose** — once per ship, re-claimable after each loot refresh, or once per player across the whole server.
* **Optional claim cost** — free by default, or any item (custom/NBT items included), picked from your inventory in-game.
* **Auto-discovery** — End Cities register themselves from the server's own structure data, at exact bounds, ships included.
* **Per-player container loot** — every player gets their own private copy of a city's chests, on a lazy per-city refresh timer.
* **Griefing protection** — bounds-based per structure piece, so the void between towers stays buildable.
* **Snapshots** — captured on discovery, restorable on demand or on each loot refresh.
* **Dialog config menu** — `/betterend` opens native dialogs. No config-file spelunking.
* **Guided setup tour** — `/betterend setup`, five screens, about two minutes.
* **AntiDupe-friendly** — claimed elytras carry the claimer's ownership tag when BetterAntiDupe is installed.

***

## Requirements

* **Minecraft 26.1+**
* **Paper, Folia, or Purpur**
* **Java 25+**
* _Optional:_ [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe), MySQL

{% hint style="warning" %}
**BetterEnd is 26.x only.** It uses the native Dialog API and the server's structure data directly, neither of which exists on 1.21.x. There is no 1.21 build, and the plugin will not load on one.
{% endhint %}

BetterEnd is standalone — no datapack, no library plugin, no resource pack.

***

## Where to go next

[installation.md](getting-started/installation.md) -> Drop the jar in, start the server. Under two minutes.

[quick-start.md](getting-started/quick-start.md) -> **Start here.** There is genuinely no setup — this page explains what's already running and how to check it.

[config-menu.md](getting-started/config-menu.md) -> Change any setting in-game, without touching YAML.

[basic-configuration.md](getting-started/basic-configuration.md) -> The handful of settings most servers actually tweak.

[elytra-claims.md](guides/elytra-claims.md) -> Claim modes, costs, and how the frame stays vanilla.

[per-player-loot.md](guides/per-player-loot.md) -> How the lazy per-city refresh window works.

[troubleshooting.md](troubleshooting.md) -> Something not working? Most issues have a known cause. Check here first.

***

## Support

* [**GitHub Issues**](https://github.com/ESMP-FUN/BetterEnd/issues) — bug reports, feature requests
* [**Discord**](https://discord.gg/qwYcTpHsNC) — community support, announcements
* [**GitHub Releases**](https://github.com/ESMP-FUN/BetterEnd/releases) — downloads and release notes

Source-available, non-commercial (see [LICENSE](https://github.com/ESMP-FUN/BetterEnd/blob/main/LICENSE)). Made with Kotlin by [darkstarworks](https://github.com/darkstarworks).

***

[![Servers](https://img.shields.io/endpoint?url=https%3A%2F%2Ffaststats.dev%2Fapi%2Fshields%2Fbetter-end%3Fmetric%3Dservers%26color%3Dblueviolet%26icon%3D1&style=flat)](https://faststats.dev/project/better-end)
