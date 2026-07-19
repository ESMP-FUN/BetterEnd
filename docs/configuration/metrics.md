# Metrics & Privacy

BetterEnd reports anonymous usage metrics through [FastStats](https://faststats.dev). This page says exactly what's collected and how to turn it off.

***

## What's collected

Four aggregate values, and nothing else:

| Metric | Example values | Why |
|---|---|---|
| `database_type` | `sqlite`, `mysql` | Whether MySQL support is worth maintaining |
| `elytra_claim_mode` | `per-ship`, `per-refresh`, `global` | Which claim modes are actually used |
| `per_player_loot` | `true`, `false` | Whether servers keep per-player loot on |
| `city_count` | `0`, `1-5`, `6-20`, `21-50`, `51-100`, `100+` | Realistic scale, for performance work |

Plus what FastStats collects for any plugin: server software, version counts, and an anonymous server id.

## What isn't

* **No player data.** No names, no UUIDs, no IPs, no chat, no locations.
* **No world data.** City coordinates are never sent — `city_count` is a bucketed range, not a list.
* **No config values** beyond the four above. Your MySQL credentials, cost items and world names stay local.

City count is deliberately **bucketed**. A server with 37 cities reports `21-50`, which is enough to know whether performance work should target tens or hundreds of cities, and not enough to identify anyone.

***

## Why it exists

Small plugins get built on guesses. These four numbers answer questions that change what gets worked on:

* If almost nobody runs MySQL, that code doesn't need more effort.
* If `global` claim mode is popular, it deserves more features.
* If servers routinely have 100+ cities, discovery and snapshot performance matter more than they seem to at 5.

***

## Opting out

Two independent switches — **either one** stops collection entirely.

### Per-plugin

```yaml
metrics:
  enabled: false
```

In `plugins/BetterEnd/config.yml`. Turns off BetterEnd's metrics only. Confirmed at startup:

```
[BetterEnd] FastStats Metrics: Disabled (config)
```

### Server-wide

```properties
enabled=false
```

In `plugins/faststats/config.properties`. Turns off FastStats for **every** plugin on the server that uses it.

{% hint style="success" %}
**The first server start is silent.** FastStats writes its config file and submits nothing until the next restart. Opting out before any data leaves your server is always possible — you don't have to opt out in advance to stay private.
{% endhint %}

***

## Startup messages

The console tells you the state on every start:

| Message | Meaning |
|---|---|
| `FastStats Metrics: Enabled` | Collecting |
| `FastStats Metrics: Disabled (config)` | Off via `metrics.enabled: false` |
| `FastStats Metrics: Disabled (no project token)` | No token compiled in — shouldn't occur in a release build |
| `FastStats Metrics: Failed` | Init threw; a warning line follows with the reason |

`Failed` is harmless — metrics are wrapped so a failure can never affect the plugin. Report it if you see it, though.

***

## Public data

The aggregate numbers are public: [faststats.dev/project/better-end](https://faststats.dev/project/better-end).

[![Servers](https://img.shields.io/endpoint?url=https%3A%2F%2Ffaststats.dev%2Fapi%2Fshields%2Fbetter-end%3Fmetric%3Dservers%26color%3Dblueviolet%26icon%3D1&style=flat)](https://faststats.dev/project/better-end)

***

## A note on version 0.2.0

BetterEnd used bStats before 0.2.0. If you'd previously opted out through `plugins/bStats/config.yml`, **that opt-out no longer applies** — it was specific to bStats.

To stay opted out, use either switch above. The per-plugin `metrics.enabled: false` in `config.yml` carried over unchanged and still works.

{% hint style="info" %}
**Numbers will lag adoption.** Because the first start after an update is silent, a server that updates to 0.2.0 appears only after its next restart. A quiet dashboard immediately after release is expected, not a fault.
{% endhint %}
