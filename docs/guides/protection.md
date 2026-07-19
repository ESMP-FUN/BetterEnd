# Protection

End City structures protect themselves from griefing — without fencing players out of the island they're on.

***

## Bounds-based, per piece

This is the design decision that matters.

BetterEnd doesn't draw one big box around a city. It protects **each generated structure piece individually** — every tower, bridge, and the ship — using the server's own structure data for exact bounds.

The result: the towers can't be stripped, but **the empty space between them stays fully buildable**. Players can build on the island, bridge between towers, and set up a base in the gaps, all without fighting the plugin.

A one-big-region approach would protect a huge cube of mostly-empty air and make the whole island read-only. This doesn't.

### Piece padding

```yaml
protection:
  piece-padding: 3
```

Each piece expands by this many blocks, covering edge decoration and a thin shell around each tower — the trim and detail blocks that sit just outside the structure's strict bounds.

`3` is a good default. Raising it starts protecting genuinely empty space between pieces; lowering it leaves decoration breakable.

***

## What's protected

Everything inside a protected piece, **regardless of block type**:

| Setting | Default | Protects against |
|---|---|---|
| `protection.enabled` | `true` | Breaking structure blocks (the master toggle) |
| `protection.block-place` | `true` | Placing blocks inside a structure piece |
| `protection.block-explosions` | `true` | Creepers, TNT, and other explosions |

The elytra item frame is protected separately and always, as part of the [claim system](elytra-claims.md#whats-protected).

{% hint style="info" %}
**"Regardless of block type" is deliberate.** No allowlist to maintain — if it's inside the structure, it's protected. That covers purpur, end stone bricks, chests, shulkers, banners, and whatever a future update adds.
{% endhint %}

***

## Telling players why

```yaml
protection:
  notify-denied: true
```

A denied break or place shows an **action-bar message** rather than failing silently. Silent failures generate support tickets; a one-line explanation doesn't.

It's the action bar rather than chat deliberately — a player mining along a wall would otherwise spam their own chat.

***

## Bypassing

`betterend.bypass.protection` lets a player build and break freely inside protected structures. **Ops have it by default.**

{% hint style="warning" %}
**Testing protection as an op will look broken.** You'll break blocks freely and conclude protection isn't working. Test as a non-op, or explicitly negate the permission:

```
/lp user <you> permission set betterend.bypass.protection false
```
{% endhint %}

For builders who need to work inside cities, grant the permission to a staff rank rather than turning protection off.

***

## Turning it off

```yaml
protection:
  enabled: false
```

Cities become fully breakable. Discovery, per-player loot, elytra claims and snapshots all keep working — protection is independent.

Worth considering on anarchy-style servers, where you might still want renewable elytras and per-player loot without the "you can't break that" layer.

{% hint style="info" %}
**Protection off + auto-restore on** is a coherent combination: players can destroy a city freely, and it rebuilds itself on the next loot refresh. See [Snapshots & Resets](snapshots-and-resets.md).
{% endhint %}

***

## Scope

Protection is deliberately self-contained — three toggles and a padding value, with no integration surface:

* **No claim-plugin hooks.** BetterEnd doesn't inspect Residence / Lands / GriefPrevention claims.
* **No WorldGuard integration.** It neither reads nor writes WorldGuard regions.
* **No container-access control.** Protection covers blocks; container *contents* are handled by [per-player loot](per-player-loot.md) instead.

That's the point rather than a shortfall — nothing to configure, nothing to keep in sync with another plugin, and no behaviour that changes depending on what else you have installed. Protection works the same on every server running it.

If your setup genuinely needs one of these, say so on [Discord](https://discord.gg/qwYcTpHsNC).

***

## What's next?

{% content-ref url="snapshots-and-resets.md" %}
[snapshots-and-resets.md](snapshots-and-resets.md)
{% endcontent-ref %}
