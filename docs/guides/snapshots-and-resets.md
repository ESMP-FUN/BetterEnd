# Snapshots & Resets

A snapshot is a saved copy of a city's blocks. With one, a city can be rebuilt exactly as it generated — undoing griefing, or making a looted city new again.

***

## Automatic capture

```yaml
snapshot:
  auto-capture: true
```

On by default. When a city is discovered, its blocks are captured to a gzip-compressed file, so a reset target always exists without anyone remembering to make one.

Snapshots live in `plugins/BetterEnd/snapshots/`, one file per city:

```
plugins/BetterEnd/snapshots/
├── city_1.dat
├── city_2.dat
└── city_3.dat
```

{% hint style="info" %}
**Capture happens at discovery**, which is normally before players reach the city — so the snapshot is of a pristine, unlooted structure. Discovering a city that players have already wrecked captures it wrecked; use `/betterend snapshot <id>` after repairing it to re-capture.
{% endhint %}

### The cell cap

```yaml
snapshot:
  max-cells: 3000000
```

A hard limit on how many block cells one snapshot may capture, guarding memory against pathological structures. ~3M is far above any real End City — you shouldn't ever need to change it.

***

## Manual snapshots

```
/betterend snapshot <id>
```

Captures (or re-captures) that city now. Use it after you've repaired a city by hand, or if `auto-capture` was off when it was discovered.

***

## Resetting

```
/betterend reset <id>
```

Restores the city's blocks from its snapshot **and** clears everyone's per-player loot copies. The city is new again for every player.

Needs a snapshot to exist. `/betterend info <id>` tells you whether one does.

***

## Auto-restore on refresh

```yaml
snapshot:
  auto-reset-on-refresh: false
```

**Off by default.** With it on, every [loot refresh](per-player-loot.md#the-refresh-window) also restores the city's blocks — so griefing and player modification are reverted on the same cycle as the loot.

{% hint style="warning" %}
**Why it's off by default.** A restore rewrites blocks wholesale:

* Players standing inside can be suffocated or displaced
* Anything a player built **inside the structure bounds** is erased
* On a large city it's a burst of block writes

None of that is a problem on a server where cities are content to be farmed. All of it is a problem where players treat cities as bases. So you choose.
{% endhint %}

### When to turn it on

* Cities are a repeatable activity, not real estate
* You want them pristine on every cycle without staff effort
* You've told players not to build inside city structures

### When to leave it off

* Players base in End Cities
* You'd rather revert griefing manually, when you notice it
* Your refresh window is short (frequent block rewrites add up)

***

## Choosing what comes back

The two settings combine into four sensible configurations:

| `loot.refresh-hours` | `auto-reset-on-refresh` | Result |
|---|---|---|
| `12` | `false` | **Default.** Loot returns; the structure stays as players left it |
| `12` | `true` | Cities fully renew on a cycle — pristine every time |
| `0` | `false` | One-shot cities. Each player loots once, ever |
| `0` | `true` | Loot never returns, but blocks are still restorable manually |

***

## Surgical alternatives

Full resets aren't always what you want:

| Command | Restores blocks | Clears loot | Scope |
|---|---|---|---|
| `/betterend reset <id>` | yes | everyone | Whole city |
| `/betterend resetloot <id> <player>` | no | one player | That player only |
| `/betterend clearclaims <id>` | no | no | Elytra claims only |

Use `resetloot` to compensate one player, and `clearclaims` to reopen a ship's elytra without touching anything else.

***

## Storage

Snapshots are gzip-compressed and held on disk, not in the database, so they don't bloat SQLite or your MySQL instance. A typical city compresses to well under a megabyte.

Deleting a city with `/betterend delete <id>` unregisters it. Its snapshot file is no longer used, and `city_<id>.dat` can be removed by hand if you want the space back.

{% hint style="info" %}
**Back up `plugins/BetterEnd/` before updating.** Snapshots are the one thing that can't be regenerated after a city has been looted or griefed — the structure data they captured is gone once the blocks change.
{% endhint %}

***

## What's next?

{% content-ref url="city-discovery.md" %}
[city-discovery.md](city-discovery.md)
{% endcontent-ref %}
