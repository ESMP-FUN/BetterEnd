# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog, and this project adheres to Semantic Versioning.

## [0.2.3] - 2026-07-20
### Fixed
- **Every "close" button in the dialogs did nothing.** These screens stay open while you navigate between them, which means a button carries no implicit close — it has to close the dialog itself, and five of them had no action at all: **Close** on the main menu and on each settings screen, and **Close**, **Finish later** and **Done** in the setup tour. All five now close as labelled. "Finish later" still resumes on the step you left.

### Changed
- Dialogs now declare escape-to-close explicitly instead of inheriting the server default, so a screen can always be dismissed with Escape even if one of its buttons fails.

## [0.2.2] - 2026-07-20
### Fixed
- **`/betterend` and `/betterend setup` threw instead of opening.** Both build dialogs that stay open while you navigate between screens, but left the dialog's `pause` flag at its default of `true`. The server rejects a pausing dialog whose after-action leaves it paused, so every screen failed with `Dialogs that pause the game must use after_action values that unpause it after user action`. All five dialogs now declare `pause(false)` — a dedicated server never pauses regardless.

## [0.2.1] - 2026-07-20
### Fixed
- **BetterEnd did nothing on 0.1.0 and 0.2.0.** `/betterend` was registered from a scheduled task rather than during plugin enable. Paper only accepts command registration while a plugin is enabling, so it always threw `Cannot register lifecycle event handlers` — which aborted the rest of startup, leaving the ready flag unset. Every listener checks that flag, so elytra claims, per-player loot, protection and city discovery were all silently inactive, and update checking and metrics never started. The command is now registered inside `onEnable` where Paper expects it. **Updating from 0.1.0 or 0.2.0 requires no config changes — the plugin simply starts working.**
- Startup no longer depends on optional integrations: the ready flag is set before update checking and metrics initialise, and a failure in either is logged instead of disabling the plugin.

## [0.2.0] - 2026-07-19
### Changed
- **Usage metrics moved from bStats to FastStats.** No player data is collected, and the opt-out in `config.yml` (`metrics.enabled`) is unchanged. The server-wide opt-out file is now `plugins/faststats/config.properties` (`enabled=false`) instead of `plugins/bStats/config.yml`; nothing is submitted until the restart after that file is first written, so admins can always opt out before any data leaves the server.

## [0.1.0] - 2026-07-17
### Added
- **Renewable elytra item frames.** The elytra frame in an End Ship stays an item frame; punching it (vanilla pick-up) gives the player a fresh elytra while the frame stays for the next player. Frames are protected from breaking and non-player damage; player-placed frames are never touched.
- **Claim modes** — once per ship (default), re-claimable after each loot refresh, or once per player total.
- **Optional claim cost** — any item (custom/NBT items included), picked from your inventory in-game; the amount slider clamps to the item's max stack size. Free by default.
- **Floating hint** above the ship frame showing the cost (or "Punch to claim").
- **End City auto-discovery** via the server's structure data — exact piece bounds, ships included, active immediately (no approval step).
- **Per-player container loot** (Lootr-style) with a lazy per-city refresh window, shared op-editable loot templates, hopper protection, and player-placed-container detection.
- **Griefing protection**, bounds-based per structure piece — the void between towers stays buildable.
- **Structure snapshots** — gzip block snapshots captured automatically on discovery, restorable on demand (`/betterend reset`) or automatically on each loot refresh (opt-in).
- **Dialog config menu** (`/betterend`) — native MC26 dialogs with sliders, toggles and choice buttons; every change applies live.
- **Guided setup tour** (`/betterend setup`) — every setting explained in plain words, one screen at a time; ops get a one-time reminder on join until it's completed or skipped.
- **BetterAntiDupe compatibility** — claimed elytras are pre-stamped with the claimer's ownership tag when ADP is installed, and the cost picker warns when a tracked material is chosen.
- SQLite (default) or MySQL storage, Folia support, PluginPulse update checking.
