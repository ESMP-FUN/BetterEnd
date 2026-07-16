package com.esmpfun.betterend.listeners

import com.esmpfun.betterend.BetterEnd
import com.esmpfun.betterend.utils.AntiDupeCompat
import com.esmpfun.betterend.utils.StructureUtil
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Display
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.world.EntitiesLoadEvent
import org.bukkit.generator.structure.Structure
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Renewable elytras, the item-frame way: the ship's elytra **item frame stays
 * an item frame**. Punching it (the vanilla pick-up interaction) puts a fresh
 * elytra straight into the puncher's inventory while the frame — elytra and
 * all — stays put for the next player. No vault block, no datapack, nothing
 * to relearn.
 *
 * - **Identification**: when a chunk's entities load in the End, any item
 *   frame displaying an elytra inside an END_CITY structure is PDC-tagged as
 *   a ship frame. Player-placed frames are tagged at place time and never
 *   converted, so builds inside a city are safe.
 * - **Claiming**: [PlayerItemFrameChangeEvent] (REMOVE) is cancelled — the
 *   frame never loses its elytra — and the claim rules run instead: claim
 *   mode (per-ship / per-refresh / global), then the optional cost item is
 *   consumed, then a fresh elytra is handed over.
 * - **AntiDupe compat**: the handed-out elytra is pre-stamped with the
 *   claimer's AntiDupePro ownership tag (no-op when ADP isn't installed), so
 *   ADP sees a normally-owned item instead of an untracked pickup.
 * - **Protection**: the frame can't be broken or emptied by non-players
 *   while the feature is enabled.
 * - **Hint**: an optional floating text above the frame shows the cost (or
 *   "Punch to claim" when free).
 */
class ElytraFrameListener(private val plugin: BetterEnd) : Listener {

    companion object {
        private fun frameTag(plugin: BetterEnd) = NamespacedKey(plugin, "ship_elytra_frame")
        private fun playerPlacedTag(plugin: BetterEnd) = NamespacedKey(plugin, "player_placed_frame")
        private fun displayTag(plugin: BetterEnd) = NamespacedKey(plugin, "elytra_frame_text")

        /** The floating hint for the current config: cost line or claim line. */
        private fun hintText(plugin: BetterEnd): Component {
            val cost = plugin.elytraClaimManager.costStack()
            return if (cost == null) {
                Component.text("Punch to claim your Elytra", NamedTextColor.GRAY)
            } else {
                Component.text("Elytra — costs ", NamedTextColor.GRAY)
                    .append(Component.text("${cost.amount} × ", NamedTextColor.AQUA))
                    .append(cost.effectiveName().color(NamedTextColor.AQUA))
            }
        }

        /**
         * Push the current config onto every already-loaded hint display (text
         * refresh, or removal when the hint/feature is off) and spawn missing
         * hints above loaded ship frames. Called on save from the dialog and
         * the cost picker; unloaded ships catch up as their entities load.
         */
        fun refreshLoaded(plugin: BetterEnd) {
            val fTag = frameTag(plugin)
            val dTag = displayTag(plugin)
            val wantHint = plugin.config.getBoolean("elytra.enabled", true) &&
                plugin.config.getBoolean("elytra.text-display", true)
            for (world in plugin.server.worlds) {
                if (world.environment != World.Environment.THE_END) continue
                for (display in world.getEntitiesByClass(TextDisplay::class.java)) {
                    if (!display.persistentDataContainer.has(dTag, PersistentDataType.BYTE)) continue
                    if (wantHint) display.text(hintText(plugin)) else display.remove()
                }
                if (!wantHint) continue
                for (frame in world.getEntitiesByClass(ItemFrame::class.java)) {
                    if (!frame.persistentDataContainer.has(fTag, PersistentDataType.BYTE)) continue
                    spawnHintIfMissing(plugin, frame)
                }
            }
        }

        private fun spawnHintIfMissing(plugin: BetterEnd, frame: ItemFrame) {
            val dTag = displayTag(plugin)
            val loc = hintLocation(frame)
            val world = frame.world
            val existing = world.getNearbyEntitiesByType(TextDisplay::class.java, loc, 1.5) {
                it.persistentDataContainer.has(dTag, PersistentDataType.BYTE)
            }
            if (existing.isNotEmpty()) return
            world.spawn(loc, TextDisplay::class.java) { display ->
                display.text(hintText(plugin))
                display.billboard = Display.Billboard.CENTER
                display.isSeeThrough = false
                display.persistentDataContainer.set(dTag, PersistentDataType.BYTE, 1)
            }
        }

        private fun hintLocation(frame: ItemFrame): Location =
            frame.location.clone().add(0.0, 0.9, 0.0)
    }

    private val frameTag = frameTag(plugin)
    private val playerPlacedTag = playerPlacedTag(plugin)

    private fun enabled() = plugin.config.getBoolean("elytra.enabled", true)
    private fun textDisplay() = plugin.config.getBoolean("elytra.text-display", true)

    // ── identification ───────────────────────────────────────────────────────

    /** Tag ship frames (and keep hint displays current) as chunk entities load. */
    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntitiesLoad(event: EntitiesLoadEvent) {
        if (!enabled()) return
        if (event.world.environment != World.Environment.THE_END) return
        for (entity in event.entities) {
            if (entity is TextDisplay &&
                entity.persistentDataContainer.has(displayTag(plugin), PersistentDataType.BYTE)
            ) {
                // Re-render existing hints with the CURRENT config, so a cost
                // change reaches every ship as its entities load back in.
                if (textDisplay()) entity.text(hintText(plugin)) else entity.remove()
                continue
            }
            val frame = entity as? ItemFrame ?: continue
            if (isShipFrame(frame) && textDisplay()) spawnHintIfMissing(plugin, frame)
        }
    }

    /** Player-placed frames are tagged so they always keep vanilla behaviour. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onHangingPlace(event: HangingPlaceEvent) {
        val frame = event.entity as? ItemFrame ?: return
        if (frame.world.environment != World.Environment.THE_END) return
        frame.persistentDataContainer.set(playerPlacedTag, PersistentDataType.BYTE, 1)
    }

    /**
     * Whether [frame] is a ship's elytra frame — cheap tag check first, then a
     * one-time structure test that stamps the tag for next time.
     */
    private fun isShipFrame(frame: ItemFrame): Boolean {
        if (frame.persistentDataContainer.has(frameTag, PersistentDataType.BYTE)) return true
        if (frame.persistentDataContainer.has(playerPlacedTag, PersistentDataType.BYTE)) return false
        if (frame.item.type != Material.ELYTRA) return false
        if (frame.world.environment != World.Environment.THE_END) return false
        // Only ship frames — never player-built elytra displays elsewhere.
        // (Frames placed by players inside the city are caught by the
        // player-placed tag above.)
        if (!StructureUtil.isNear(frame.location, Structure.END_CITY, 8.0)) return false
        frame.persistentDataContainer.set(frameTag, PersistentDataType.BYTE, 1)
        return true
    }

    // ── claiming ─────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onFrameChange(event: PlayerItemFrameChangeEvent) {
        if (!enabled() || !plugin.isReady) return
        if (event.action != PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE) return
        val frame = event.itemFrame
        if (!isShipFrame(frame)) return

        // The frame's elytra NEVER leaves — everything below hands out copies.
        event.isCancelled = true
        val player = event.player

        val city = plugin.cityManager.getCachedCityAt(frame.location)
        if (city == null) {
            // Discovery registers the city asynchronously moments after its
            // chunks first load; a punch can only lose that race right after
            // generation.
            player.sendActionBar(Component.text("This ship is still being registered — try again in a moment.", NamedTextColor.YELLOW))
            return
        }

        if (plugin.elytraClaimManager.hasClaimed(city.id, player.uniqueId)) {
            val msg = when (plugin.elytraClaimManager.mode()) {
                com.esmpfun.betterend.managers.ElytraClaimManager.ClaimMode.PER_SHIP ->
                    "You've already claimed this ship's elytra."
                com.esmpfun.betterend.managers.ElytraClaimManager.ClaimMode.PER_REFRESH ->
                    "Already claimed — you can claim here again after this city's loot refreshes."
                com.esmpfun.betterend.managers.ElytraClaimManager.ClaimMode.GLOBAL ->
                    "You've already claimed your elytra."
            }
            player.sendActionBar(Component.text(msg, NamedTextColor.RED))
            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 0.7f)
            return
        }

        // Optional cost, consumed from the claimer's inventory.
        val cost = plugin.elytraClaimManager.costStack()
        if (cost != null) {
            if (!player.inventory.containsAtLeast(cost, cost.amount)) {
                player.sendActionBar(
                    Component.text("You need ${cost.amount} × ", NamedTextColor.RED)
                        .append(cost.effectiveName().color(NamedTextColor.RED))
                        .append(Component.text(" to claim this elytra.", NamedTextColor.RED))
                )
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.7f, 0.7f)
                return
            }
            player.inventory.removeItem(cost.clone())
        }

        val elytra = ItemStack(Material.ELYTRA)
        // AntiDupePro tracks ELYTRA: pre-stamp the claimer as owner so ADP sees
        // a normally-owned pickup. No-op when ADP isn't installed.
        AntiDupeCompat.tagOwner(elytra, player.uniqueId)
        giveOrDrop(player, elytra)

        plugin.elytraClaimManager.record(city.id, player.uniqueId)
        player.sendActionBar(Component.text("Elytra claimed — happy flying!", NamedTextColor.GREEN))
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f)
        if (plugin.config.getBoolean("debug.verbose-logging", false)) {
            plugin.logger.info("[Elytra] ${player.name} claimed at city #${city.id} (${frame.location.blockX},${frame.location.blockY},${frame.location.blockZ})")
        }
    }

    private fun giveOrDrop(player: Player, item: ItemStack) {
        val leftover = player.inventory.addItem(item)
        for (rest in leftover.values) {
            player.world.dropItemNaturally(player.location, rest)
        }
    }

    // ── frame protection ─────────────────────────────────────────────────────

    /** Ship frames can't be broken (explosions, obstruction, non-player causes). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingBreak(event: HangingBreakEvent) {
        if (!enabled()) return
        val frame = event.entity as? ItemFrame ?: return
        if (isShipFrame(frame)) event.isCancelled = true
    }

    /**
     * Non-player damage (skeleton arrows, dispenser projectiles, …) would pop
     * the elytra with no claim flow — block it. Player punches pass through so
     * [onFrameChange] can run the claim.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onFrameDamage(event: EntityDamageByEntityEvent) {
        if (!enabled()) return
        val frame = event.entity as? ItemFrame ?: return
        if (event.damager is Player) return
        if (isShipFrame(frame)) event.isCancelled = true
    }
}
