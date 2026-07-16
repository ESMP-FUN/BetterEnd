package com.esmpfun.betterend.listeners

import com.esmpfun.betterend.BetterEnd
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.generator.structure.Structure

/**
 * Auto-discovery driver. On each chunk load, asks the server whether an End
 * City structure overlaps that chunk via [World.getStructures]; any hit is
 * handed to [com.esmpfun.betterend.managers.CityDiscoveryManager], which
 * dedups and registers. Empty for ~every chunk, so the hot path is cheap.
 *
 * ChunkLoadEvent fires on the chunk's owning region thread (Folia-correct for
 * the getStructures read); the manager extracts bounds here and defers DB
 * work async.
 */
class CityDiscoveryListener(private val plugin: BetterEnd) : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (!plugin.isReady) return
        val world = event.world
        if (world.environment != World.Environment.THE_END) return
        val structures = world.getStructures(event.chunk.x, event.chunk.z, Structure.END_CITY)
        if (structures.isEmpty()) return
        for (gs in structures) plugin.discoveryManager.handle(world, gs)
    }
}
