# Commands

Everything lives under `/betterend`. There are no aliases.

{% hint style="info" %}
**Tab completion** works throughout — including city ids, which are suggested from your actual registered cities.
{% endhint %}

***

## Quick reference

| Command | Description | Permission |
|---|---|---|
| `/betterend` | Open the config menu | `betterend.admin` |
| `/betterend menu` | Same as above, explicitly | `betterend.admin` |
| `/betterend setup` | Guided setup tour | `betterend.admin` |
| `/betterend help` | Command list | `betterend.admin` |
| `/betterend list` | List discovered End Cities | `betterend.admin` |
| `/betterend info <id>` | City details | `betterend.admin` |
| `/betterend tp <id>` | Teleport to a city | `betterend.admin` |
| `/betterend snapshot <id>` | Capture the structure for restoration | `betterend.admin` |
| `/betterend reset <id>` | Restore blocks + fresh loot for everyone | `betterend.admin` |
| `/betterend resetloot <id> <player>` | Let one player loot the city fresh | `betterend.admin` |
| `/betterend clearclaims <id>` | Make the ship's elytra claimable again by everyone | `betterend.admin` |
| `/betterend delete <id>` | Unregister a city | `betterend.admin` |
| `/betterend reload` | Reload `config.yml` | `betterend.admin` |
| `/betterend update <check\|download\|status\|restore\|ignore\|unignore>` | Update checking and staging | `betterend.admin` |

Every command requires `betterend.admin`, which ops have by default. There are no player-facing commands — players just punch frames and open chests.

***

## Command details

### `/betterend`

Opens the [config menu](../getting-started/config-menu.md) as a native dialog. `/betterend menu` is identical.

**Players only** — dialogs need a client. From console, use `list`, `info`, `reset` and the rest.

### `/betterend setup`

Starts the [guided setup tour](../getting-started/config-menu.md#the-setup-tour): five screens, about two minutes, saving as you go.

**Players only.**

### `/betterend list`

Every registered city with its id, world and coordinates. Works from console.

### `/betterend info <id>`

Details for one city:

* Bounds and piece count
* Whether it has a ship (and so an elytra frame)
* Snapshot status
* Current loot cycle state

The first thing to run when something's behaving unexpectedly.

### `/betterend tp <id>`

Teleports you to that city. **Players only.**

### `/betterend snapshot <id>`

Captures that city's blocks now, replacing any existing snapshot.

With `snapshot.auto-capture` on (the default) this happens at discovery, so you'd only run it manually after repairing a city by hand, or if auto-capture was off when it was discovered.

### `/betterend reset <id>`

Full reset: restores blocks from the snapshot **and** clears everyone's per-player loot copies.

Requires a snapshot to exist — check with `info`.

{% hint style="warning" %}
A reset rewrites blocks. Players inside can be displaced or suffocated, and anything built within the structure bounds is erased. Check nobody's in there first.
{% endhint %}

### `/betterend resetloot <id> <player>`

Clears one player's loot copies for one city, so they can loot it fresh. Blocks and everyone else's copies are untouched.

The surgical option — good for compensating a player, or for an event.

### `/betterend clearclaims <id>`

Clears the elytra claim history for one city, so everyone can claim from its ship again. Doesn't touch loot or blocks.

Use it after changing `claim-mode`, or to run an event.

### `/betterend delete <id>`

Unregisters a city. BetterEnd stops managing it — no protection, no per-player loot, no elytra claims.

{% hint style="warning" %}
**This doesn't demolish anything.** Blocks stay exactly as they are. The city will also be **re-discovered on the next chunk load** unless you disable discovery or exclude its world — `delete` alone is not a permanent exclusion.
{% endhint %}

### `/betterend reload`

Re-reads `config.yml`. Use after editing the file by hand; the in-game menu applies changes live and needs no reload.

### `/betterend update`

Update checking, powered by PluginPulse. Releases are checked against [GitHub Releases](https://github.com/ESMP-FUN/BetterEnd/releases).

| Subcommand | Effect |
|---|---|
| `/betterend update` | Same as `check` — the default with no subcommand |
| `/betterend update check` | Check for a newer release |
| `/betterend update download` | Download, checksum-verify, and **stage** it for the next restart |
| `/betterend update install` | Alias for `download` |
| `/betterend update status` | Show the current state |
| `/betterend update restore` | Stage the previous backup, to roll a bad update back |
| `/betterend update ignore <version>` | Stop being told about a specific version |
| `/betterend update unignore <version>` | Undo that |

Nothing is ever swapped under a running server — updates are staged into the update folder and apply on restart. Downloads are checksum-verified and the current jar is backed up first.

{% hint style="warning" %}
**`/betterend update apply` won't do anything here.** It exists (with `reload` as an alias) to hot-apply a staged jar with no restart, but that needs PluginPulse's optional hot-reload module, which BetterEnd doesn't bundle. It reports:

```
BetterEnd: hot reload is not available — restart the server to apply.
```

That's deliberate — BetterEnd ships native SQLite libraries, which is exactly the case hot reload is unsafe for. Restart to apply.

Note also that `/betterend update reload` (hot-apply) and `/betterend reload` (reload `config.yml`) are **different commands**. The bare `/betterend reload` is the one you want.
{% endhint %}

### Update behaviour

By default BetterEnd only **notifies** — it downloads nothing unless you ask. Server owners can change that from an `update:` section in `config.yml`:

```yaml
update:
  mode: notify              # off | check-only | notify | download | auto-stage
  check-interval-hours: 6
```

| Mode | Behaviour |
|---|---|
| `off` | No checking at all |
| `check-only` | Check quietly; result only in `/betterend update status` |
| `notify` | **Default.** Check and tell admins. Downloads nothing |
| `download` | Check, download, verify, and stage for the next restart |
| `auto-stage` | Stage automatically as soon as an update is found |

***

## Console usage

These work from console: `list`, `info`, `snapshot`, `reset`, `resetloot`, `clearclaims`, `delete`, `reload`, `update`, `help`.

These are players-only, because they open a dialog or move you: `menu`, `setup`, `tp`.

***

## While starting up

Commands other than `help` respond with:

```
BetterEnd is still starting up — try again in a moment.
```

Database setup and the city cache load happen off the main thread, so there's a brief window after enable where the plugin isn't ready. It clears itself in a moment.
