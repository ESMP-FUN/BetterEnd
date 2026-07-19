# Basic Configuration

The defaults are production-ready — this page is about the handful of settings that actually change how your server plays. For the full annotated file, see [config.yml](../configuration/config.yml.md).

Everything here can be set in-game via [the config menu](config-menu.md) instead of editing YAML.

***

## The short version

```yaml
elytra:
  claim-mode: per-ship     # per-ship | per-refresh | global
  cost:
    amount: 0              # 0 = free; pick the item in-game

loot:
  enabled: true
  refresh-hours: 12        # 0 = never refresh

protection:
  enabled: true
  piece-padding: 3

snapshot:
  auto-capture: true
  auto-reset-on-refresh: false   # the one big opt-in

discovery:
  enabled: true
  excluded-worlds: []
```

***

## How rare should elytras be?

This is the decision that most changes your server's feel.

| Mode | Effect | Good for |
|---|---|---|
| `per-ship` | One elytra per player, per ship, forever | **Default.** Exploration stays rewarding — more ships found means more elytras |
| `per-refresh` | Claims reset with the city's loot refresh | Elytras as a renewable resource; good with a short refresh window |
| `global` | One elytra per player, ever, server-wide | Keeping elytras a genuine milestone item |

`per-ship` is the sane middle. It's what makes finding a *new* ship still matter, while never leaving a latecomer with an empty frame.

{% hint style="info" %}
Changing the mode doesn't wipe existing claims. Switching from `global` to `per-ship` lets players who already claimed do so again at other ships. To deliberately clear a ship's history, use `/betterend clearclaims <id>`.
{% endhint %}

## Should claims cost something?

Free by default. A cost turns the elytra into a sink for whatever your economy has too much of.

Set the item in-game — `/betterend` → **Choose Cost Item** — then the amount on the slider. The amount clamps to that item's stack size.

Common choices: a stack of ender pearls, a few diamond blocks, or a custom "Elytra Voucher" item your shop sells. [More →](../guides/elytra-claims.md#claim-cost)

## How often should loot come back?

`loot.refresh-hours`, default `12`.

* **12–24h** — cities are a repeatable activity
* **168 (a week)** — cities are an occasional event
* **0** — never refresh; each player gets exactly one copy of each city, forever

The window is **per city and lazy** — it starts when someone first loots that city, not on a global timer, so cities don't all refresh at once and unvisited ones cost nothing. [More →](../guides/per-player-loot.md)

## Should blocks come back too?

`snapshot.auto-reset-on-refresh`, default `false`.

With it off, a refresh restores *loot* only. Broken blocks stay broken (though protection means there shouldn't be many).

With it on, each refresh also rebuilds the city from its snapshot — griefing and player modifications are reverted.

{% hint style="warning" %}
**Why it's off by default.** A restore rewrites blocks wholesale. Players standing inside during one can be suffocated or displaced, and any building they've done inside the structure bounds is erased. Turn it on if you want pristine cities; leave it off if your players treat cities as bases.
{% endhint %}

## Do you have a second End world?

`discovery.excluded-worlds` takes a list of world names (case-insensitive):

```yaml
discovery:
  excluded-worlds:
    - end_vanilla
    - resource_end
```

Cities in those worlds are never registered, so they behave exactly like vanilla. Useful when you want one End managed and one left alone.

{% hint style="info" %}
Cities registered **before** you excluded a world keep working. Remove them explicitly with `/betterend delete <id>`.
{% endhint %}

***

## Settings you probably shouldn't touch

* **`protection.piece-padding`** (default `3`) — how far each structure piece expands to cover edge decoration. Raising it can start protecting empty space between pieces; lowering it leaves trim breakable.
* **`snapshot.max-cells`** (default `3000000`) — a memory guard, already far above any real city.
* **`discovery.startup-sweep`** (default `true`) — catches cities in chunks already loaded at enable. Only turn it off if startup is measurably slow.
* **`debug.verbose-logging`** — noisy; for diagnosing an issue, not for running.

***

## What's next?

{% content-ref url="../configuration/config.yml.md" %}
[config.yml.md](../configuration/config.yml.md)
{% endcontent-ref %}

{% content-ref url="../guides/elytra-claims.md" %}
[elytra-claims.md](../guides/elytra-claims.md)
{% endcontent-ref %}
