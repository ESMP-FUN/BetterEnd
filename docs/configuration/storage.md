# Storage

BetterEnd stores city registrations, elytra claim history, and per-player loot copies in a database. SQLite by default, MySQL optionally.

***

## SQLite (default)

```yaml
database:
  type: sqlite
```

Zero setup. A `database.db` file appears in `plugins/BetterEnd/` on first start and that's the end of it.

This is the right choice for the overwhelming majority of servers, including large single-server survival setups. SQLite is not a toy — it handles this workload comfortably.

***

## MySQL

```yaml
database:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: betterend
    username: root
    password: ""
```

Connections are pooled (HikariCP). The console confirms which backend came up:

```
[BetterEnd] Database pool initialized (MYSQL)
```

{% hint style="warning" %}
**An invalid `type` silently falls back to SQLite**, with a warning:

```
[BetterEnd] Invalid database.type, defaulting to SQLITE
```

If you configured MySQL and see `SQLITE` in the console, check that line — a typo in `type` is the usual cause, and your MySQL settings will be ignored entirely.
{% endhint %}

### Setting it up

1. Create the database and a user:

   ```sql
   CREATE DATABASE betterend CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'betterend'@'%' IDENTIFIED BY 'a-real-password';
   GRANT ALL PRIVILEGES ON betterend.* TO 'betterend'@'%';
   FLUSH PRIVILEGES;
   ```

2. Fill in `config.yml`
3. Restart — tables are created automatically

***

## Which should you use?

**Stay on SQLite unless you have a specific reason not to.** It's faster for a single server (no network round-trip), needs no maintenance, and backs up by copying one file.

Switch to MySQL when:

* **Multiple servers share one End** — the only genuinely compelling reason. Per-player loot and claim history stay consistent across a proxy network
* **Your host requires it** — some managed hosts don't give you a persistent filesystem
* **You already run centralised backups** through your database

Don't switch just because MySQL sounds more serious. For one server it's strictly more moving parts for no gain.

***

## Migrating between backends

There is no built-in migration command. Changing `database.type` starts from an **empty** database — cities re-register automatically as chunks load, but **elytra claim history and per-player loot copies are lost**.

In practice that means:

* Players who had claimed an elytra can claim again
* Everyone's per-player loot copies reset to fresh

{% hint style="warning" %}
**Plan the switch.** Do it during a quiet period, and tell players their End City loot will reset. On a `global` claim mode server this is more disruptive — everyone effectively gets a second elytra.
{% endhint %}

If you need a real migration, raise it on [Discord](https://discord.gg/qwYcTpHsNC).

***

## What's stored

| Data | Notes |
|---|---|
| **City registrations** | Bounds, pieces, world, ship presence. Regenerates automatically if lost |
| **Elytra claims** | Which player claimed at which ship. Cannot be regenerated |
| **Per-player loot** | Each player's private container copies, plus the shared templates |
| **Loot cycles** | Each city's current refresh window |

**Snapshots are not in the database.** They're gzip files in `plugins/BetterEnd/snapshots/`, so a database of any size stays small.

***

## Backups

Back up the **whole `plugins/BetterEnd/` folder**, not just the database — snapshots live alongside it and can't be regenerated once a city's blocks have changed.

For MySQL setups, that's your usual database backup **plus** the `snapshots/` directory.

{% hint style="info" %}
**City registrations are the cheap part.** If you lose them, cities re-register on their own. Claim history and snapshots are the parts worth protecting.
{% endhint %}
