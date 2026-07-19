# Installation

Drop a jar in a folder, restart. That's the whole thing — there's no per-city setup to do afterwards.

## Prerequisites

* **Minecraft 26.1 or newer** — Paper, Folia, or Purpur
* **Java 25 or newer**

{% hint style="warning" %}
**26.x only.** BetterEnd uses the native Dialog API and reads the server's structure data directly. Neither exists on 1.21.x, so there is no 1.21 build and the plugin will not load on one. If you need 1.21 support, say so on Discord — it's a rewrite of two subsystems, not a version bump, so it depends on demand.
{% endhint %}

{% hint style="info" %}
**Why Java 25?** Minecraft 26.x runs on it. Paper 26.1's API classes are compiled to Java 25 bytecode, so an older JVM can't even read them.
{% endhint %}

## Download

* [GitHub Releases](https://github.com/ESMP-FUN/BetterEnd/releases) — every version, with changelogs

Look for `BetterEnd-<version>.jar`.

## Installation steps

### 1. Stop your server

Properly, with `/stop`.

### 2. Drop the jar

Move `BetterEnd-<version>.jar` into your server's `plugins/` folder.

```
your-server/
├── plugins/
│   ├── BetterEnd-<version>.jar   ← here
│   └── ... other plugins
└── ...
```

### 3. Start your server

Watch the console:

```
[BetterEnd] BetterEnd starting on Paper...
[BetterEnd] Database pool initialized (SQLITE)
[BetterEnd] Loaded 0 End Cities into cache
[BetterEnd] FastStats Metrics: Enabled
[BetterEnd] BetterEnd ready.
```

`Loaded 0 End Cities` is correct on a first start — cities register themselves as players travel, not up front.

{% hint style="success" %}
**Seeing errors instead?** Check [Troubleshooting](../troubleshooting.md).
{% endhint %}

### 4. Check the data folder

BetterEnd creates `plugins/BetterEnd/`:

```
plugins/BetterEnd/
├── config.yml       # Main configuration (also editable in-game)
├── database.db      # SQLite database (default storage)
└── snapshots/       # Block snapshots, one file per city
```

Two more files appear only when relevant:

* `materials.yml` — written when [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe) is installed, listing the materials it tracks
* `ownership-key` — the AntiDupe ownership key marker

You don't need to open any of them. Everything in `config.yml` can be changed in-game.

## Verify it's working

```
/betterend
```

The config menu opens as a native dialog. If it does, you're installed.

Prefer to check from console? `/betterend list` works there too and prints the discovered-city list (empty on a fresh install).

{% hint style="success" %}
**Next:** [Quick Start](quick-start.md) — what's already running, and how to see it happen in-game.
{% endhint %}

## Optional: BetterAntiDupe

If you run [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe), BetterEnd detects it automatically. Claimed elytras are pre-stamped with the claiming player's ownership tag, so a renewable elytra never looks like a duplicate to ADP.

Nothing to configure — install both and it works. The cost-item picker also warns you if you choose a material ADP tracks.

## Updating

1. Stop the server
2. Replace the old jar with the new one
3. Start the server

Missing database tables are created on start, and your existing `config.yml` is left exactly as it is.

{% hint style="info" %}
**New options don't appear in an existing `config.yml`.** The file is only written when it's missing, so options added in a later version won't show up in yours — they just run on their built-in defaults, which are always the sensible ones.

If you want to see and change a newly added option, either add the key by hand (the [config.yml reference](../configuration/config.yml.md) lists them all) or use `/betterend` — the menu always shows every current setting regardless of what's in the file.
{% endhint %}

{% hint style="warning" %}
**Back up first.** Copy `plugins/BetterEnd/` somewhere safe before updating — it holds your city registrations, per-player loot state, and snapshots.
{% endhint %}

BetterEnd also ships with update checking built in. `/betterend update` checks for a new release, and can download and stage it for your next restart. See [Commands](../reference/commands.md#betterend-update).

## What's next?

{% content-ref url="quick-start.md" %}
[quick-start.md](quick-start.md)
{% endcontent-ref %}

***

## Quick tips

{% hint style="info" %}
**Folia:** supported out of the box. BetterEnd detects Folia at startup and routes scheduler calls to the regional schedulers — the console line tells you which it found (`starting on Folia...`).
{% endhint %}

{% hint style="info" %}
**MySQL:** SQLite is the default and needs no setup. For networks sharing state across servers, switch to MySQL in [config.yml](../configuration/storage.md).
{% endhint %}

{% hint style="info" %}
**Existing worlds are fine.** Cities that were already looted still register and still get protection, snapshots and per-player loot. Players who already took an elytra before you installed BetterEnd can claim again — the claim history starts empty.
{% endhint %}
