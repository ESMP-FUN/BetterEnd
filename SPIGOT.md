[CENTER][SIZE=7][B]BetterEnd[/B][/SIZE]

[SIZE=4][COLOR=#7f8c8d]Renewable End Cities — every player gets the elytra, and the item frame stays an item frame.[/COLOR][/SIZE]

[SIZE=3]Paper · Folia · Purpur — Minecraft 26.1+[/SIZE][/CENTER]

[CENTER][SIZE=4][I]The End is the most single-use dimension on a multiplayer server.[/I]
[I]The first player to reach an End Ship takes [B]the[/B] elytra;[/I]
[I]everyone after them finds an empty frame and gutted chests.[/I][/SIZE]
[SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]No vault block. No datapack. No key item.[/B][/COLOR][/SIZE]
The usual fix is to swap the ship's item frame for a vault block, then push a datapack and a key item onto every player.

[B]BetterEnd doesn't touch the item frame.[/B] The elytra stays exactly where Mojang put it, players punch it exactly like they always have — and every player gets their own. Chest loot is per-player, cities protect themselves from griefing, and the whole structure can snapshot and restore.
[LIST]
[*][B]First player takes the only elytra[/B] → per-player claims from the vanilla item frame
[*][B]Elytra vaults need a datapack + key[/B] → nothing extra; no dependencies at all
[*][B]First player also empties every chest[/B] → per-player container loot, Lootr-style
[*][B]Griefers strip the towers and ship[/B] → bounds-based protection per structure piece
[*][B]Cities never come back[/B] → snapshots, restorable on demand or each refresh
[*][B]Setup overhead per city[/B] → auto-discovery from the server's own structure data
[/LIST]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Setup[/B][/COLOR][/SIZE]
There isn't any.
[LIST=1]
[*]Drop the jar into [ICODE]plugins/[/ICODE]
[*]Restart
[*]There is no step 3
[/LIST]
Auto-discovery, per-player elytras, per-player loot and protection are all on by default. Cities register themselves as their chunks load, at their exact structure bounds, ships included.

Want to change something? [ICODE]/betterend[/ICODE] opens a menu of native MC26 dialogs — sliders, toggles, choice buttons — and [ICODE]/betterend setup[/ICODE] walks you through every setting in plain words in about two minutes. [B]You never have to open a config file.[/B]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Features[/B][/COLOR][/SIZE]
[LIST]
[*][B]Renewable elytra item frames[/B] — punching the ship's frame (vanilla pick-up) gives you a fresh elytra while the frame, and its elytra, stays for the next player. Frames are protected from breaking and non-player damage; player-placed frames are never touched.
[*][B]Claim modes[/B] — once per ship (default), re-claimable after each loot refresh, or once per player across the whole server.
[*][B]Optional claim cost[/B] — free by default, or charge any item (custom/NBT items from other plugins included). Pick it from your own inventory in-game; the amount slider clamps to that item's max stack size.
[*][B]Floating hint[/B] above the ship frame showing the cost, or "Punch to claim" when it's free.
[*][B]Auto-discovery[/B] — cities register themselves on chunk load, active immediately, no approval step. A startup sweep catches cities already loaded when the plugin enables.
[*][B]Per-player container loot[/B] — every player gets their own private copy of a city container's contents, on a lazy per-city refresh timer. Player-placed chests keep vanilla behaviour.
[*][B]Griefing protection[/B] — towers, bridges and the ship are protected from breaking, placing and explosions; the void [I]between[/I] pieces stays fully buildable, so builders aren't fenced off the island.
[*][B]Structure snapshots[/B] — captured automatically on discovery, restorable on demand or automatically on each loot refresh (opt-in).
[*][B]Dialog config menu[/B] — every change applies live and is written back to [ICODE]config.yml[/ICODE].
[*][B]Guided setup tour[/B] — one setting per screen; ops get a one-time reminder on join until it's completed or skipped.
[/LIST]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]How per-player loot works[/B][/COLOR][/SIZE]
Each city runs its own refresh cycle, and it's [B]lazy[/B] — nothing ticks in the background:
[LIST]
[*]The first player to loot a city with no active cycle starts a fresh window for [I]that[/I] city.
[*]Every player who opens a container during the window sees their own private copy.
[*]When the window elapses, the next player to loot triggers the refresh — all copies cleared, new window opens.
[/LIST]
Default window is 12 hours ([ICODE]loot.refresh-hours[/ICODE]); [ICODE]0[/ICODE] means never refresh. Because the cycle is per-city and starts on first use, a hundred discovered cities don't all refresh at the same moment — and cities nobody visits cost nothing.
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Protection that doesn't fence off the island[/B][/COLOR][/SIZE]
Protection is [B]bounds-based per structure piece[/B], not one big region around the city. Any block inside a generated piece — tower, bridge, ship — is protected regardless of type, with a small padding (default 3 blocks) covering edge decoration and a thin shell.

Griefers can't strip the towers, but the empty space between pieces is still yours to build in. Ops with [ICODE]betterend.bypass.protection[/ICODE] build freely, and a denied break shows an action-bar message rather than failing silently. Explosion protection (creepers, TNT) toggles independently.
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Compatibility[/B][/COLOR][/SIZE]
[LIST]
[*][B]Server software:[/B] Paper, Folia, Purpur and Paper-compatible forks
[*][B]Minecraft:[/B] 26.1+
[*][B]Java:[/B] 25+
[*][B]Dependencies:[/B] none — no datapack, no library plugin, no resource pack
[*][B]Storage:[/B] SQLite (default, zero setup) or MySQL with connection pooling
[/LIST]
[B]Heads up:[/B] BetterEnd is [B]26.x only[/B]. It uses the native Dialog API and the server's structure data directly, neither of which exists on 1.21.x — there is no 1.21 build, and it won't load on one.

Optional: [B]BetterAntiDupe[/B] — when installed, claimed elytras are pre-stamped with the claimer's ownership tag so renewable elytras don't trip duplicate detection.
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Commands[/B][/COLOR][/SIZE]
All admin commands require [ICODE]betterend.admin[/ICODE] (ops have it by default).
[LIST]
[*][ICODE]/betterend[/ICODE] — config menu (native dialogs)
[*][ICODE]/betterend setup[/ICODE] — guided first-time setup tour
[*][ICODE]/betterend list[/ICODE] — list discovered End Cities
[*][ICODE]/betterend info <id>[/ICODE] — city details
[*][ICODE]/betterend tp <id>[/ICODE] — teleport to a city
[*][ICODE]/betterend snapshot <id>[/ICODE] — capture the structure for restoration
[*][ICODE]/betterend reset <id>[/ICODE] — restore blocks + fresh loot for everyone
[*][ICODE]/betterend resetloot <id> <player>[/ICODE] — let one player loot the city fresh
[*][ICODE]/betterend clearclaims <id>[/ICODE] — make the ship's elytra claimable again by everyone
[*][ICODE]/betterend delete <id>[/ICODE] — unregister a city
[*][ICODE]/betterend reload[/ICODE] — reload [ICODE]config.yml[/ICODE]
[/LIST]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Permissions[/B][/COLOR][/SIZE]
[LIST]
[*][ICODE]betterend.admin[/ICODE] — admin commands + config menu [I](default: op)[/I]
[*][ICODE]betterend.bypass.protection[/ICODE] — build/break inside protected structures [I](default: op)[/I]
[*][ICODE]betterend.discovery.notify[/ICODE] — notice when a city is discovered [I](default: op)[/I]
[/LIST]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Free & Source Available[/B][/COLOR][/SIZE]
[LIST]
[*]No licence key
[*]No "premium" feature gating
[*]Anonymous usage metrics only (opt out in [ICODE]config.yml[/ICODE], or server-wide) — no player data
[*]Source-available, non-commercial — full source on GitHub, issues welcome
[/LIST]
[CENTER][SIZE=3][COLOR=#808080]
——————————————————————————————
[/COLOR][/SIZE][/CENTER]
[SIZE=6][COLOR=#0000ff][B]Links[/B][/COLOR][/SIZE]
[LIST]
[*][URL='https://github.com/ESMP-FUN/BetterEnd']Source code and issue tracker[/URL]
[*][URL='https://discord.gg/qwYcTpHsNC']Discord — support, announcements, feature requests[/URL]
[*][URL='https://ko-fi.com/darkstarworks']Ko-fi — (anonymous) donations are VERY welcome[/URL]
[/LIST]
[CENTER][SIZE=3][I]Please share your "If it would do [THING], I would use it" feedback —[/I]
[I]there's a good chance it ships. And if BetterEnd made your End worth[/I]
[I]revisiting, a positive review on this page is the best way to support development.[/I][/SIZE]

[URL='https://faststats.dev/project/better-end'][IMG]https://img.shields.io/endpoint?url=https%3A%2F%2Ffaststats.dev%2Fapi%2Fshields%2Fbetter-end%3Fmetric%3Dservers%26color%3Dblueviolet%26icon%3D1&style=flat[/IMG][/URL][/CENTER]
