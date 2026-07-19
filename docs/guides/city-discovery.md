# City Discovery

End Cities register themselves. There is no command to run per city, no region to select, and no approval step.

***

## How it works

When a chunk containing an End City loads, BetterEnd asks the **server's own structure data** for that city's exact bounds and pieces, then registers it.

Because it reads the structure data rather than scanning blocks, discovery is:

* **Exact** — real piece bounds, not a guessed bounding box
* **Complete in one pass** — every tower and bridge, plus the ship if the city has one
* **Cheap** — a data lookup, not a block-by-block sweep
* **Immune to looting** — a city stripped bare still registers correctly, because the structure data doesn't change when blocks do

A discovered city is **active immediately**. Loot, elytra frames, protection and snapshots all start working the moment it registers — there's no pending state to approve.

```
[BetterEnd] Discovered End City #1 in world_the_end (1264,0,-368)..(1329,100,-303), 14 pieces
```

Ops with `betterend.discovery.notify` also get a clickable in-game message that teleports them to the city.

***

## The startup sweep

```yaml
discovery:
  startup-sweep: true
```

Discovery normally rides on chunk-load events. But chunks already resident when the plugin enables never fire one — so on a restart, a city sitting in a loaded chunk would be missed.

The startup sweep scans already-loaded chunks once at enable to catch them. On by default; only turn it off if you've measured it slowing startup.

***

## Excluding worlds

```yaml
discovery:
  excluded-worlds:
    - end_vanilla
    - resource_end
```

World names, case-insensitive. Cities in these worlds are never registered and behave exactly like vanilla.

Useful when you run a managed End and a resource End, and only want one of them renewable.

{% hint style="info" %}
**Excluding a world doesn't unregister what's already in it.** Cities registered before the exclusion keep working. Remove them explicitly:

```
/betterend list          # find the ids
/betterend delete <id>   # unregister each
```
{% endhint %}

***

## Turning discovery off

```yaml
discovery:
  enabled: false
```

No new cities register. Already-registered cities keep working normally.

There is **no manual registration command** — BetterEnd is built around structure data, so a city that doesn't exist in the world's structure data can't be registered. That means:

* **Naturally generated cities** — always work
* **Player-built replica cities** — can't be registered
* **Custom worldgen / datapack End Cities** — work if they generate as real structures with structure data; don't if they're pasted schematics

{% hint style="info" %}
Building a custom End City by hand and wanting BetterEnd to manage it is a reasonable ask that isn't currently supported. If you need it, raise it on [Discord](https://discord.gg/qwYcTpHsNC).
{% endhint %}

***

## Inspecting cities

| Command | Shows |
|---|---|
| `/betterend list` | Every registered city, with ids |
| `/betterend info <id>` | Bounds, piece count, ship present, snapshot status, loot cycle |
| `/betterend tp <id>` | Teleport to that city |
| `/betterend delete <id>` | Unregister it |

City ids are simple incrementing numbers, and every command that takes one offers tab completion.

{% hint style="warning" %}
**`delete` unregisters, it doesn't demolish.** The blocks stay exactly where they are — the city just stops being managed. It'll be re-discovered on the next chunk load unless you've also excluded its world or disabled discovery.
{% endhint %}

***

## What's next?

{% content-ref url="../configuration/config.yml.md" %}
[config.yml.md](../configuration/config.yml.md)
{% endcontent-ref %}
