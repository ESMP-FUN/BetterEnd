# Per-Player Loot

Every player who opens an End City container sees their own private copy of its contents. The first player through no longer empties the city for everyone else.

***

## How it works

The first time anyone opens a city container, BetterEnd captures its rolled contents as a **template**. Every player who opens that container afterwards gets their own copy of the template, stored against their UUID.

Your copy is yours. Take from it, add to it, leave things in it — no other player sees your changes, and you can't take from theirs.

Containers stay containers. A chest is still a chest, with the vanilla model, sound and animation.

***

## The refresh window

This is the part worth understanding, because it's **lazy** — nothing ticks in the background.

Each city runs its own cycle:

1. The first player to loot a city with **no active cycle** starts a fresh window for **that city**
2. During the window, every player who opens a container gets their own copy
3. When the window elapses, **the next player to loot** triggers the refresh — all copies are cleared, and a new window opens

So a refresh is driven by a player arriving, not by a timer firing.

```yaml
loot:
  refresh-hours: 12    # 0 = never refresh
```

### Why lazy matters

* **No thundering herd.** A hundred registered cities don't all refresh at midnight — each is on its own clock, started by its own first visitor.
* **Unvisited cities cost nothing.** A city nobody has been to in a month runs no tasks and does no work.
* **No scheduler pressure.** There's no background job scanning cities for expiry.

The tradeoff: a city's loot doesn't refresh at the exact moment the window elapses — it refreshes on the next visit after that. For loot this is the right behaviour anyway; refreshing a city nobody is in accomplishes nothing.

### Choosing a window

| Value | Feel |
|---|---|
| `6`–`12` | Cities are a repeatable farm. Good for smaller or busier servers |
| `24`–`72` | Cities are a weekly-ish activity |
| `168` | Once a week. Cities stay special |
| `0` | Never refresh. Each player gets exactly one copy of each city, forever |

`0` is worth considering on servers where the End is meant to be finite: everyone gets a fair first run, and that's it.

***

## What counts as city loot

Only containers **inside the generated structure** are treated as city loot. BetterEnd uses the server's structure bounds, so this is exact.

* **City chests, barrels and other containers** → per-player copies
* **A chest a player places inside the city** → fully vanilla, shared, untouched

That distinction matters: players can build a base in an End City and use their own storage normally, without it turning into per-player copies or being wiped on refresh.

{% hint style="info" %}
**Hoppers can't touch city containers.** Item movement in *or* out of a city container is cancelled, so automation can't be used to drain the loot everyone else is about to get a copy of.

Hoppers attached to a **player-placed** container inside a city work exactly as normal — the two are distinguished by a marker written when the block is placed, not by position.
{% endhint %}

***

## Turning it off

```yaml
loot:
  enabled: false
```

Containers revert to vanilla behaviour — shared, first-come-first-served. The elytra frame system is independent and keeps working.

***

## Admin commands

| Command | Effect |
|---|---|
| `/betterend reset <id>` | Clear all per-player copies **and** restore blocks — a full city reset |
| `/betterend resetloot <id> <player>` | Clear one player's copies for that city, so they can loot it fresh |
| `/betterend info <id>` | Show the city's current cycle state |

`resetloot` is the surgical one — good for compensating a player after a bug, or for an event, without resetting the city for everyone.

***

## Interaction with claim modes

If `elytra.claim-mode` is `per-refresh`, elytra claims reset **with the loot cycle**. A refresh then reopens both the chests and the ship's elytra at once.

With `per-ship` or `global`, the two are independent — loot refreshes on its window, elytra claims follow their own rule.

***

## What's next?

{% content-ref url="snapshots-and-resets.md" %}
[snapshots-and-resets.md](snapshots-and-resets.md)
{% endcontent-ref %}
