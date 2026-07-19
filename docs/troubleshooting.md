# Troubleshooting

Most problems have a known cause. Start here.

***

## The plugin won't load

### `Unsupported class file major version` / plugin missing from `/plugins`

You're on **Java 24 or older**. BetterEnd needs **Java 25+**, because Paper 26.1's API classes are compiled to Java 25 bytecode.

Check with `java -version` on the machine running the server — not your local one.

### `Unknown API version` or an immediate disable on a 1.21 server

BetterEnd is **26.x only**. There is no 1.21 build, and it won't load on one. See [Installation](getting-started/installation.md#prerequisites).

### Nothing in the log at all

Confirm the jar is directly in `plugins/`, not a subfolder, and that it's the shaded release jar (`BetterEnd-<version>.jar`) from [Releases](https://github.com/ESMP-FUN/BetterEnd/releases).

***

## Cities aren't being discovered

### Nothing logs when I fly to a city

Discovery rides on **chunk loading**. A few causes, in order of likelihood:

1. **The chunks haven't loaded.** Flying fast on an elytra can outrun chunk loading. Stop, stand still a moment, and watch the console.
2. **The world is excluded.** Check `discovery.excluded-worlds` in `config.yml` — it's case-insensitive but must match the world's actual name.
3. **Discovery is off.** Check `discovery.enabled: true`.
4. **It's not a real structure.** BetterEnd reads the server's structure data. A player-built or schematic-pasted "End City" has no structure data and can't be registered. See [City Discovery](guides/city-discovery.md#turning-discovery-off).

Confirm what's registered with `/betterend list`.

### Cities in already-loaded chunks are missed after a restart

That's what `discovery.startup-sweep: true` (the default) is for. If you turned it off, turn it back on.

***

## Elytra claims aren't working

### Punching the frame does nothing

* Check `elytra.enabled: true`
* Check the city is registered: `/betterend list`
* Check the city actually **has a ship**: `/betterend info <id>` — not every End City generates one
* If a cost is set, the player needs the item. Without it the claim is refused with a red **action-bar** message ("You need 2 × Ender Pearl to claim this elytra") and a low note — easy to miss if they're watching chat

### A player can't claim a second time

Working as configured. Check `elytra.claim-mode`:

* `per-ship` — one per player per ship. They need a **different** ship
* `global` — one per player, ever, server-wide
* `per-refresh` — they must wait for that city's loot refresh

To deliberately reopen a ship: `/betterend clearclaims <id>`.

### The cost item isn't being accepted

The cost is stored as a full item stack, so a **specific** item is required — matching name, enchantments and NBT, not just the material. If you set the cost to a custom named item, a plain vanilla one won't do.

Re-pick it via `/betterend` → **Choose Cost Item** if you're unsure what's stored.

{% hint style="warning" %}
Never hand-edit `elytra.cost.item` in `config.yml`. It's Base64, not a material name.
{% endhint %}

### No floating hint above the frame

Check `elytra.text-display: true`. It's a text display entity — if you run a plugin or client mod that hides or culls display entities, that'll do it too.

***

## Per-player loot isn't working

### Everyone sees the same chest contents

* Check `loot.enabled: true`
* Check the container is **inside the generated structure**. Player-placed chests are deliberately vanilla and shared
* Check the city is registered: `/betterend list`

### Loot never refreshes

The window is **lazy**. It doesn't refresh on a timer — it refreshes when someone next loots the city *after* the window has elapsed. A city nobody visits never refreshes, by design.

Also check `loot.refresh-hours` isn't `0`, which means never.

### A player lost their items

Per-player copies are **storage**, not just loot. If a player leaves items in their copy and the city refreshes, those items are gone.

Warn players not to store things in city containers, or set `loot.refresh-hours: 0`.

***

## Protection isn't working

### I can break blocks in a city

You're almost certainly an op — `betterend.bypass.protection` **defaults to op**. Test as a non-op or negate it:

```
/lp user <you> permission set betterend.bypass.protection false
```

This is the single most common "bug report" for BetterEnd.

### Players can build between the towers

Intended. Protection is [per structure piece](guides/protection.md#bounds-based-per-piece), so the space *between* pieces stays buildable. Only the towers, bridges and ship are protected.

If decoration just outside a tower is breakable, raise `protection.piece-padding` slightly.

### Explosions still damage the city

Check `protection.block-explosions: true`.

***

## Resets and snapshots

### `/betterend reset` says there's no snapshot

The city has none. Create one: `/betterend snapshot <id>`.

Note that this captures the city **as it is now** — if it's already looted and griefed, that's what you'll restore to later.

### A reset erased a player's base

Expected, and why `auto-reset-on-refresh` is off by default. A restore rewrites every block inside the structure bounds, including player builds.

If players base in cities, keep `snapshot.auto-reset-on-refresh: false` and reset manually.

### Snapshots take up too much space

They're gzipped in `plugins/BetterEnd/snapshots/`, one file per city, typically well under a megabyte each. If you've deleted cities, their `city_<id>.dat` files are no longer used and can be removed by hand.

***

## Database

### `Invalid database.type, defaulting to SQLITE`

A typo in `database.type`. Valid values are exactly `sqlite` or `mysql`. Your MySQL settings are being **ignored entirely** while this shows.

### Everything reset after I switched to MySQL

Expected — there's no migration between backends. The new database starts empty: cities re-register on their own, but claim history and loot copies are lost. See [Storage](configuration/storage.md#migrating-between-backends).

***

## Performance

BetterEnd is built to stay off the main thread — discovery, snapshots and database work are async, and the loot refresh cycle is lazy rather than scheduled.

If you're seeing lag:

1. **Check it's actually BetterEnd.** Run a profiler (`/spark profiler`) and look for `com.esmpfun.betterend` frames before changing anything.
2. **Large resets are the one heavy operation.** Restoring a big city writes a lot of blocks. That's inherent — resets are bursty. Avoid `auto-reset-on-refresh` with a very short `refresh-hours`.
3. **Startup sweep on a huge world** adds a one-off cost at enable. Turn `discovery.startup-sweep` off if you've measured it as a problem.
4. **`debug.verbose-logging: true` is expensive.** Make sure it's off in production.

***

## Reporting bugs

[GitHub Issues](https://github.com/ESMP-FUN/BetterEnd/issues), or [Discord](https://discord.gg/qwYcTpHsNC) if you'd rather chat it through.

Please include:

* **BetterEnd version** — `/betterend update status`, or the jar filename
* **Server software and version** — the full `/version` output
* **Java version** — `java -version` on the server host
* **The full stack trace**, if there is one — from the log file, not a screenshot of chat
* **What you expected** and **what happened**
* **`/betterend info <id>`** output for the affected city, if it's city-specific

Turning on `debug.verbose-logging: true`, reproducing the issue, then attaching that section of the log makes almost any report diagnosable in one round trip.

{% hint style="info" %}
**Feature requests are welcome too.** Several things in BetterEnd exist because a server owner said "if it did X, I'd use it". Say it on Discord.
{% endhint %}
