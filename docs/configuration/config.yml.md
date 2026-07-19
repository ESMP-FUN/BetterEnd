# config.yml

The complete reference for `plugins/BetterEnd/config.yml`.

{% hint style="info" %}
**You may never need this page.** Everything here is editable in-game through [the config menu](../getting-started/config-menu.md), and in-game changes are written back to this file. This is the reference for people who prefer YAML.
{% endhint %}

After editing the file by hand, run `/betterend reload`.

***

## database

```yaml
database:
  type: sqlite
  mysql:
    host: localhost
    port: 3306
    database: betterend
    username: root
    password: ""
```

| Key | Default | Description |
|---|---|---|
| `type` | `sqlite` | `sqlite` (zero-setup) or `mysql`. Invalid values fall back to SQLite with a console warning |
| `mysql.*` | — | Ignored entirely when `type: sqlite` |

Full details, including when MySQL is worth it: [Storage](storage.md).

***

## discovery

```yaml
discovery:
  enabled: true
  startup-sweep: true
  excluded-worlds: []
```

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | Auto-register End Cities as their chunks load. Discovered cities are active immediately |
| `startup-sweep` | `true` | Sweep already-loaded chunks at enable, so cities resident at startup aren't missed |
| `excluded-worlds` | `[]` | World names (case-insensitive) where cities are never registered |

Cities registered before a world was excluded keep working — remove them with `/betterend delete <id>`.

[More → City Discovery](../guides/city-discovery.md)

***

## elytra

```yaml
elytra:
  enabled: true
  claim-mode: per-ship
  cost:
    item: ""
    amount: 0
  text-display: true
```

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | The renewable elytra frame system |
| `claim-mode` | `per-ship` | `per-ship`, `per-refresh`, or `global` — see below |
| `cost.item` | `""` | The cost item as a Base64 stack. **Set in-game**, not by hand |
| `cost.amount` | `0` | How many the claim consumes. `0` = free |
| `text-display` | `true` | Float a hint above the frame (cost, or "Punch to claim") |

**Claim modes:**

* `per-ship` — each player can claim one elytra from each ship, ever
* `per-refresh` — claims reset when that city's loot refresh window rolls over
* `global` — each player can claim one elytra total, across all ships

{% hint style="warning" %}
`cost.item` is a serialized item stack, not a material name. Use `/betterend` → **Choose Cost Item**; hand-editing it will not work.
{% endhint %}

[More → Elytra Claims](../guides/elytra-claims.md)

***

## loot

```yaml
loot:
  enabled: true
  refresh-hours: 12
```

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | Per-player container loot (Lootr-style) |
| `refresh-hours` | `12` | Per-city refresh window in hours. `0` = never refresh |

The window is **per city and lazy**: the first player to loot a city with no active (or an expired) cycle starts a fresh window for that city. When it elapses, the next looter triggers the refresh. Cities never all refresh at once, and unvisited cities do no work.

Only containers inside the generated structure are treated as city loot — player-placed chests keep vanilla behaviour.

The in-game slider ranges from 0 to 168 hours (one week). Larger values are accepted in the file.

[More → Per-Player Loot](../guides/per-player-loot.md)

***

## protection

```yaml
protection:
  enabled: true
  piece-padding: 3
  block-place: true
  block-explosions: true
  notify-denied: true
```

| Key | Default | Description |
|---|---|---|
| `enabled` | `true` | Grief protection master toggle |
| `piece-padding` | `3` | Blocks to expand each structure piece by, covering edge decoration and a thin shell |
| `block-place` | `true` | Deny placing blocks inside a protected piece |
| `block-explosions` | `true` | Protect structure blocks from creeper/TNT/other explosions |
| `notify-denied` | `true` | Show an action-bar message when a break or place is denied |

Protection is **bounds-based per structure piece**, not one region around the city — the void between towers stays fully buildable.

{% hint style="warning" %}
`betterend.bypass.protection` defaults to **op**. Testing protection as an op will make it look broken.
{% endhint %}

[More → Protection](../guides/protection.md)

***

## snapshot

```yaml
snapshot:
  max-cells: 3000000
  auto-capture: true
  auto-reset-on-refresh: false
```

| Key | Default | Description |
|---|---|---|
| `max-cells` | `3000000` | Hard cap on block cells per snapshot, to protect memory. Far above any real city |
| `auto-capture` | `true` | Snapshot a city automatically on discovery, so a reset target always exists |
| `auto-reset-on-refresh` | `false` | Also restore blocks when a city's loot cycle rolls over |

{% hint style="warning" %}
**`auto-reset-on-refresh` is off for a reason.** A restore rewrites blocks and can suffocate or displace players standing inside, and erases anything built within the structure bounds. Turn it on if cities are farmable content; leave it off if players base in them.
{% endhint %}

[More → Snapshots & Resets](../guides/snapshots-and-resets.md)

***

## metrics

```yaml
metrics:
  enabled: true
```

Anonymous usage metrics via FastStats. No player data — aggregate config and feature usage only.

[More → Metrics & Privacy](metrics.md)

***

## update _(optional, not in the default file)_

Not written to `config.yml` by default — add it only if you want to change how update checking behaves. It overrides the values BetterEnd ships in `pluginpulse.yml`.

```yaml
update:
  mode: notify
  check-interval-hours: 6
```

| Key | Default | Description |
|---|---|---|
| `mode` | `notify` | `off`, `check-only`, `notify`, `download`, or `auto-stage` |
| `check-interval-hours` | `6` | How often to check |

`notify` tells admins and downloads nothing. `download` and `auto-stage` fetch and stage updates for the next restart — nothing is ever swapped under a running server.

[More → Commands](../reference/commands.md#update-behaviour)

***

## setup

```yaml
setup:
  completed: false
```

Set to `true` automatically once the `/betterend setup` tour has been completed **or** skipped. While it's `false`, ops with `betterend.admin` get a one-time reminder on join.

Set it back to `false` to make the reminder appear again.

***

## debug

```yaml
debug:
  verbose-logging: false
```

Verbose diagnostic logging. Noisy — turn it on to investigate an issue, then turn it back off. Useful output to attach to a bug report.

***

## Full default file

```yaml
database:
  type: sqlite
  mysql:
    host: localhost
    port: 3306
    database: betterend
    username: root
    password: ""

discovery:
  enabled: true
  startup-sweep: true
  excluded-worlds: []

elytra:
  enabled: true
  claim-mode: per-ship
  cost:
    item: ""
    amount: 0
  text-display: true

loot:
  enabled: true
  refresh-hours: 12

protection:
  enabled: true
  piece-padding: 3
  block-place: true
  block-explosions: true
  notify-denied: true

snapshot:
  max-cells: 3000000
  auto-capture: true
  auto-reset-on-refresh: false

metrics:
  enabled: true

setup:
  completed: false

debug:
  verbose-logging: false
```
