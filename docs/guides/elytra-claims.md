# Elytra Claims

The headline feature: the End Ship's elytra item frame becomes renewable, without stopping being an item frame.

***

## How it works

In vanilla, an End Ship's elytra sits in an item frame. Punching the frame pops the elytra out, and the frame is empty forever after. First player wins.

BetterEnd intercepts that punch. The claiming player gets a **fresh elytra** in their inventory, and the frame **keeps its own elytra** for the next player. From the player's side nothing looks or feels different — same frame, same punch, same item.

What that buys you over the vault-block approach:

* No datapack, no key item, no custom block
* The ship looks exactly like a vanilla ship
* Players already know how to do it; nothing to explain
* Shaders, resource packs and map mods all behave normally

## What's protected

Because the frame is load-bearing, BetterEnd protects it:

* It can't be broken by players
* It can't be destroyed by non-player damage (explosions, mobs, projectiles)
* Its elytra can't be taken out and kept

{% hint style="info" %}
**Player-placed frames are never touched.** Only the frame that generated as part of the ship structure is managed. A frame a player puts up inside a city behaves entirely vanilla — it can be broken, filled, and emptied as normal.
{% endhint %}

***

## Claim modes

`elytra.claim-mode` decides who can claim, and how often.

### `per-ship` (default)

Each player can claim one elytra from **each ship**, once. Find a new ship, get a new elytra.

Exploration keeps paying off, and a player who arrives at a picked-over ship still gets theirs. This is the mode that matches what most servers want.

### `per-refresh`

Claims are tied to the city's [loot refresh cycle](per-player-loot.md). When a city's window rolls over, everyone can claim from its ship again.

Elytras become a renewable resource on a timer. Pairs naturally with a short `loot.refresh-hours`.

### `global`

Each player can claim **one elytra, ever**, across every ship on the server.

Keeps elytras a milestone. Note that players can still get more the vanilla way — this limits BetterEnd claims, not elytras in general.

{% hint style="info" %}
**Switching modes is safe.** Existing claim records aren't wiped. Going from `global` to `per-ship` means a player who used their one global claim can now claim at ships they haven't visited. To deliberately reopen a specific ship for everyone, use `/betterend clearclaims <id>`.
{% endhint %}

***

## Claim cost

Free by default. To charge for a claim:

1. `/betterend` → **Choose Cost Item**
2. Click any item in your inventory
3. Set the amount on the slider

The item is stored as a full Base64 item stack, so **custom items work properly** — a named, enchanted, NBT-tagged item from another plugin stays exactly that item, and a player's plain diamond won't satisfy a cost set to a custom "Elytra Voucher".

The amount slider **clamps to the item's max stack size**: 64 for most items, 16 for ender pearls, 1 for a bed. Setting `0` makes claims free again.

```yaml
elytra:
  cost:
    item: ""      # Base64 stack — set this in-game, not by hand
    amount: 0     # 0 = free
```

{% hint style="warning" %}
**Don't hand-edit `cost.item`.** It's a serialized item stack, not a material name. Use the in-game picker — that's the only supported way to set it.
{% endhint %}

### Ideas

* **A stack of ender pearls** — thematic, and a sink for a resource the End produces
* **Diamond blocks** — a straightforward wealth check
* **A shop-sold voucher** — sell an "Elytra Voucher" custom item; the cost picker accepts it, and your economy plugin handles pricing
* **Phantom membranes** — flavour-appropriate, since players will need them for repairs anyway

***

## The floating hint

`elytra.text-display`, on by default, floats a small text display above the ship's frame:

* **"Punch to claim"** when claims are free
* The cost when one is set

It's a text display entity, so it needs no resource pack and doesn't interfere with the frame. Turn it off for a completely untouched-looking ship — the claim still works, players just aren't told about it.

***

## BetterAntiDupe integration

If [BetterAntiDupe](https://github.com/ESMP-FUN/BetterAntiDupe) is installed, BetterEnd detects it automatically and **pre-stamps each claimed elytra with the claiming player's ownership tag**.

Without this, renewable elytras look exactly like a duplication exploit to an anti-dupe system — many identical elytras appearing from one source. The stamp means each claim is correctly attributed from the moment it's created.

Nothing to configure. The cost-item picker additionally warns you if you select a material ADP tracks.

***

## Admin commands

| Command | Effect |
|---|---|
| `/betterend clearclaims <id>` | Make that city's ship elytra claimable again by everyone |
| `/betterend info <id>` | Show whether the city has a ship, and its claim state |

`clearclaims` is the tool for a fresh event or a botched cost change — it clears the claim history for one city without touching loot or blocks.

***

## What's next?

{% content-ref url="per-player-loot.md" %}
[per-player-loot.md](per-player-loot.md)
{% endcontent-ref %}
