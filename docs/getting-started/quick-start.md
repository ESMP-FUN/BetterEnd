# Quick Start

Most plugin quick-starts give you a checklist. This one doesn't, because BetterEnd has no setup step — everything is on the moment the server starts.

This page explains what's already running, and how to watch it work.

***

## What's already on

Straight out of the box, with no configuration:

| Feature | Default | What it means |
|---|---|---|
| **Auto-discovery** | on | Cities register themselves as chunks load |
| **Renewable elytras** | on, free | Every player can punch the ship frame for their own elytra, once per ship |
| **Per-player loot** | on, 12h | Every player gets their own copy of city chests |
| **Protection** | on | Structure blocks can't be broken, placed in, or blown up |
| **Snapshots** | on | Captured automatically when a city is discovered |
| **Block auto-restore** | **off** | Resets restore loot only, unless you turn this on |

The one thing that's deliberately off is [auto-restore on refresh](../guides/snapshots-and-resets.md#auto-restore-on-refresh) — rewriting blocks under players who might be standing there is disruptive, so you opt into it.

***

## Watch it happen

### 1. Go to the End

Travel to an End City, or teleport to one. As soon as its chunks load, the console logs:

```
[BetterEnd] Discovered End City #1 in world_the_end (1264,0,-368)..(1329,100,-303), 14 pieces
```

Ops with `betterend.discovery.notify` also get a clickable in-game message that teleports them to the city.

{% hint style="info" %}
**Nothing logged?** The city's chunks have to actually load. Flying near on a fast elytra sometimes outruns chunk loading — stop and wait a moment. See [Troubleshooting](../troubleshooting.md#cities-arent-being-discovered).
{% endhint %}

### 2. Punch the elytra frame

Find the End Ship and punch its elytra item frame, exactly like vanilla.

An elytra goes into your inventory. **The frame keeps its elytra.** Ask a second player to punch it — they get one too.

By default a small floating hint above the frame reads "Punch to claim", or shows the cost if you've set one.

### 3. Open a chest

Open any chest in the city. Loot it. Now have another player open the same chest — they see a full, untouched copy.

### 4. Try to grief it

Break a purpur block in a tower. You can't, unless you're an op (ops have `betterend.bypass.protection` by default). A message appears in your action bar explaining why.

{% hint style="warning" %}
**Testing protection as an op?** It'll look broken — ops bypass it by default. Test as a non-op, or explicitly negate `betterend.bypass.protection`.
{% endhint %}

### 5. Look at the city

```
/betterend list
/betterend info 1
```

`info` shows bounds, piece count, whether a ship was found, snapshot status, and the current loot cycle.

***

## Changing anything

Two ways, neither of which involves a text editor:

```
/betterend          # the config menu — jump straight to any setting
/betterend setup    # the guided tour — five screens, ~2 minutes
```

The tour explains each setting in plain words and saves as you go. Ops get a one-time reminder to run it on join, until it's completed or skipped.

{% content-ref url="config-menu.md" %}
[config-menu.md](config-menu.md)
{% endcontent-ref %}

***

## The three decisions worth making

Everything else can stay default. These three actually change how your server plays:

### How often can a player get an elytra?

`elytra.claim-mode` — `per-ship` (default, one per player per ship), `per-refresh` (re-claimable each loot refresh), or `global` (one per player, ever).

`global` keeps elytras genuinely rare. `per-refresh` makes them a renewable resource. [More →](../guides/elytra-claims.md#claim-modes)

### Should an elytra cost something?

Free by default. You can charge any item — including custom items from other plugins — picked from your own inventory via `/betterend` → **Choose Cost Item**. [More →](../guides/elytra-claims.md#claim-cost)

### How often does loot come back?

`loot.refresh-hours`, default 12. Set `0` to never refresh, so each player gets exactly one copy of each city forever. [More →](../guides/per-player-loot.md)

***

## What's next?

{% content-ref url="basic-configuration.md" %}
[basic-configuration.md](basic-configuration.md)
{% endcontent-ref %}

{% content-ref url="../guides/elytra-claims.md" %}
[elytra-claims.md](../guides/elytra-claims.md)
{% endcontent-ref %}
