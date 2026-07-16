package com.esmpfun.betterend.listeners

import com.esmpfun.betterend.BetterEnd
import com.esmpfun.betterend.models.EndCity
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent

/**
 * Griefing protection for registered End Cities — bounds-based per structure
 * piece, NOT palette-based.
 *
 * End cities are built mostly from plain purpur and end stone bricks, so a
 * material allow-list would leave the structure trivially spoofable. Instead
 * a block is protected iff it falls inside a `city_pieces` bounding box
 * (expanded by `protection.piece-padding` to cover edge decoration + a thin
 * shell), regardless of its type. The void *between* the towers stays fully
 * buildable.
 *
 * Players with `betterend.bypass.protection` (default op) are exempt.
 */
class ProtectionListener(private val plugin: BetterEnd) : Listener {

    private fun pad() = plugin.config.getInt("protection.piece-padding", 3)
    private fun enabled() = plugin.config.getBoolean("protection.enabled", true)

    /** The city protecting [block], or null. Fast region reject, then the
     *  per-piece padded test. */
    private fun protectingCity(block: Block): EndCity? {
        val city = plugin.cityManager.getCachedCityInPaddedRegion(block.location, pad()) ?: return null
        return if (city.inStructurePiece(block.location, pad())) city else null
    }

    private fun isProtected(block: Block): Boolean = protectingCity(block) != null

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (!enabled() || !plugin.isReady) return
        if (event.player.hasPermission("betterend.bypass.protection")) return
        protectingCity(event.block) ?: return
        event.isCancelled = true
        notifyDenied(event.player)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!enabled() || !plugin.isReady) return
        if (event.player.hasPermission("betterend.bypass.protection")) return
        if (!plugin.config.getBoolean("protection.block-place", true)) return
        protectingCity(event.block) ?: return
        event.isCancelled = true
        notifyDenied(event.player)
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!enabled() || !plugin.isReady) return
        if (!plugin.config.getBoolean("protection.block-explosions", true)) return
        event.blockList().removeIf { isProtected(it) }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockExplode(event: BlockExplodeEvent) {
        if (!enabled() || !plugin.isReady) return
        if (!plugin.config.getBoolean("protection.block-explosions", true)) return
        event.blockList().removeIf { isProtected(it) }
    }

    private fun notifyDenied(player: Player) {
        if (!plugin.config.getBoolean("protection.notify-denied", true)) return
        player.sendActionBar(net.kyori.adventure.text.Component.text("§cThis End City is protected."))
    }
}
