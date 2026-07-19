# Permissions

BetterEnd has three permissions. All default to **op**.

***

## The full list

| Permission | Default | Grants |
|---|---|---|
| `betterend.admin` | op | All `/betterend` commands and the config menu |
| `betterend.bypass.protection` | op | Build and break inside protected structures |
| `betterend.discovery.notify` | op | In-game notice when a city is discovered |

***

## `betterend.admin`

Gates the entire command surface. Without it, `/betterend` does nothing at all.

There is no split between "view" and "modify" — one permission covers `list` and `info` as well as `reset` and `delete`. Grant it to staff you'd trust with a city reset.

## `betterend.bypass.protection`

Lets a player break and place blocks inside protected structure pieces, ignoring [protection](../guides/protection.md).

{% hint style="warning" %}
**This is the one that trips people up.** Ops have it by default, so testing protection while opped makes it look completely broken — you'll break a purpur block, see no denial, and conclude the plugin isn't working.

Test as a non-op, or negate it on yourself:

```
/lp user <you> permission set betterend.bypass.protection false
```
{% endhint %}

Grant it to a builder rank if staff need to work inside cities. That's much better than turning protection off server-wide.

## `betterend.discovery.notify`

Sends a clickable in-game message whenever a city is discovered:

```
[BetterEnd] Discovered End City #3 world_the_end [1264 55 -368] • 14 pieces
```

Clicking the coordinates teleports you there. The console logs discoveries regardless of who holds this.

Useful while setting up, or on a new world where you want to see the End filling in. On an established server it gets noisy — consider negating it for staff who don't need it.

***

## Rank examples

### Regular players

Nothing. Players need no permission to claim elytras or get per-player loot — those work for everyone by design.

```yaml
# No BetterEnd permissions needed
```

### Builders / staff who work inside cities

```
/lp group builder permission set betterend.bypass.protection true
```

Protection bypass only. They can build inside structures, but can't reset or delete cities.

### Moderators

```
/lp group mod permission set betterend.admin true
/lp group mod permission set betterend.bypass.protection false
```

Full commands, but protection still applies to them — so they can't accidentally grief a city while moderating.

### Admins

Default op is fine. All three permissions.

### Testing protection as an admin

```
/lp user <you> permission set betterend.bypass.protection false
```

Set it back to `true` (or unset it) when you're done.

***

## Notes

* **Three permissions, on purpose.** `betterend.admin` is all-or-nothing rather than a node per subcommand — three permissions you can hold in your head beats twenty you have to look up. If you genuinely need finer control, raise it on [Discord](https://discord.gg/qwYcTpHsNC).
* **No bypass for per-player loot.** Loot copies are per-UUID with no override — admins get their own copy like everyone else. Use `/betterend resetloot <id> <player>` to give someone a fresh one.
* **No bypass for elytra claims.** Claim modes apply to everyone. Use `/betterend clearclaims <id>` to reopen a ship.
