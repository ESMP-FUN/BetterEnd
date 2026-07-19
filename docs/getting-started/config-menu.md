# The Config Menu

BetterEnd is menu-first. Every setting in `config.yml` can be changed in-game through native Minecraft dialogs — sliders, toggles and choice buttons — and changes apply live and are written back to the file.

No resource pack. No chat-based fake GUI. These are the real MC26 dialogs.

***

## Opening it

```
/betterend
```

Or `/betterend menu` — same thing. Requires `betterend.admin` (ops have it by default).

{% hint style="info" %}
**Players-only.** Dialogs render on a client, so the menu can't open from console. From console, use `/betterend list`, `info`, `reset` and friends instead.
{% endhint %}

***

## The main menu

| Button | What it opens |
|---|---|
| **Elytra Frames** | Claim rules, cost amount, and the floating frame hint |
| **End Cities** | Discovery, loot refresh, protection and snapshot settings |
| **Choose Cost Item** | Pick the claim cost item straight from your inventory |
| **Setup Tour** | Re-run the guided walkthrough |
| **Close** | Close without saving |

The menu also shows a live summary line — either "Claims are currently free." or "A claim currently costs 2 × ender pearl."

***

## Elytra Frames

* **Feature enabled** — the whole renewable-frame system
* **Who can claim, how often** — `per-ship`, `per-refresh`, or `global`
* **Floating hint above the frame** — the text display showing cost or "Punch to claim"
* **Choose Cost Item** — jumps to the item picker

{% hint style="warning" %}
Opening the cost-item picker from this screen **discards unsaved edits** on it. Save first, then pick the item.
{% endhint %}

## End Cities

Everything about the cities themselves, on one screen:

* **Register new End Cities automatically** — `discovery.enabled`
* **Loot refresh window (hours, 0 = never)** — a slider from 0 to 168 (one week)
* **Grief protection** — the master protection toggle
* **Also deny placing blocks** — `protection.block-place`
* **Protect from explosions** — `protection.block-explosions`
* **Tell players when a break is denied** — the action-bar notice
* **Snapshot each city on discovery** — `snapshot.auto-capture`
* **Auto-restore blocks on loot refresh** — off by default

***

## Choosing a cost item

**Choose Cost Item** opens an inventory-based picker rather than a dialog, because it has to show your actual items.

Click any item in your inventory and it becomes the claim cost. This includes:

* Vanilla items
* Items with custom NBT, names, lore or enchantments
* Custom items from other plugins

The item is stored as a Base64-encoded stack, so it survives exactly — a specifically-named, specifically-enchanted item stays that item.

Then set the amount with the slider. **The slider clamps to that item's max stack size** — 64 for most things, 16 for ender pearls, 1 for a bed. Setting the amount to `0` makes claims free again.

{% hint style="info" %}
**Running BetterAntiDupe?** The picker warns you when you choose a material ADP tracks, since charging a tracked item interacts with its ownership system.
{% endhint %}

***

## The setup tour

```
/betterend setup
```

Five screens, roughly two minutes, each with a plain-English explanation and one or two inputs. Navigation is **[← Back] [Next →] [Finish later]**, and **each step saves as you pass through it** — quitting halfway keeps what you've already answered.

The screens, in order:

1. **Elytra frames** — enable, and pick the claim mode
2. **Elytra cost** — the cost amount and the floating hint
3. **Per-player loot** — enable, and the refresh window
4. **Protection** — grief protection and the denial notice
5. **Snapshots & resets** — auto-capture and auto-restore

The welcome screen offers **Start the tour**, **Skip — defaults are fine**, or **Close** (which asks again next time).

{% hint style="info" %}
**The reminder.** Ops with `betterend.admin` get a one-time nudge on join until the tour is completed or skipped. Once `setup.completed` is `true` in config.yml, it stops. Skipping sets it too — the tour is genuinely optional and the defaults are production-ready.
{% endhint %}

***

## Menu edits and config.yml

Changes made in the menu are written straight back to `config.yml`, so the two never drift apart. You can edit the file directly instead if you prefer — run `/betterend reload` afterwards to pick the changes up.

{% hint style="warning" %}
**Don't edit the file and the menu at the same time.** If you have `config.yml` open in an editor while saving from the in-game menu, your editor will hold a stale copy and overwrite the menu's changes when you save it. Reload after editing, and close the file before using the menu.
{% endhint %}

***

## What's next?

{% content-ref url="basic-configuration.md" %}
[basic-configuration.md](basic-configuration.md)
{% endcontent-ref %}
