# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog, and this project adheres to Semantic Versioning.

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
