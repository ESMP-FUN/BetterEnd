package com.esmpfun.betterend.listeners

import com.esmpfun.betterend.BetterEnd
import com.esmpfun.betterend.managers.ContainerLootManager
import com.esmpfun.betterend.models.EndCity
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.block.DoubleChest
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.BlockInventoryHolder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.loot.Lootable
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Per-player End City container loot (Lootr-style), the BetterAncientCities
 * approach ported to the End.
 *
 * Every player who opens a city chest sees their own private copy of its
 * contents, so the second player into a city doesn't find gutted containers.
 * Cooldown semantics are content *freshness*, not lockout: after a reset,
 * [ContainerLootManager.clearCity] drops everyone's copies and the next open
 * re-clones the template.
 *
 * Eligibility: a container is city loot only if it's inside a registered city
 * AND inside an actual generated structure piece ([EndCity.inStructurePiece]).
 * The piece test rejects player-built chests that happen to sit within the
 * city's envelope but outside the structure.
 *
 * How it works:
 *  - Each container has a shared **template** — the canonical contents every
 *    first-open copy is cloned from, materialized once by rolling the block's
 *    vanilla loot table (a naturally-generated city chest holds an UNROLLED
 *    loot table / empty inventory until first opened). Persists across resets.
 *  - Ops with `betterend.admin` **sneak-open** the template to edit it; a
 *    normal click gets a per-player copy.
 *  - Double chests are keyed by the left half (one 54-slot template/copy).
 *  - Player-placed containers are PDC-tagged at place time and keep vanilla
 *    behaviour.
 *  - Hopper movement in/out of an eligible container is cancelled.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ContainerLootListener(private val plugin: BetterEnd) : Listener {

    private val playerPlacedKey = NamespacedKey("betterend", "player_placed_container")

    /** Anti double-fire: last virtual-open millis per player. */
    private val openDebounce = ConcurrentHashMap<UUID, Long>()

    /** Marks a player's private copy inventory (saved to player_container_loot). */
    class CopyHolder(
        val cityId: Int,
        val pos: ContainerLootManager.ContainerPos
    ) : InventoryHolder {
        lateinit var backing: Inventory
        override fun getInventory(): Inventory = backing
    }

    /** Marks an op editing the shared template (saved to container_template). */
    class TemplateHolder(
        val cityId: Int,
        val pos: ContainerLootManager.ContainerPos
    ) : InventoryHolder {
        lateinit var backing: Inventory
        override fun getInventory(): Inventory = backing
    }

    private companion object {
        val ELIGIBLE = setOf(
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL,
            Material.DISPENSER, Material.DROPPER
        )
        val COPY_TITLE: Component = Component.text("End City Loot")
        val TEMPLATE_TITLE: Component = Component.text("Loot Template (shared)")
    }

    private fun enabled() = plugin.config.getBoolean("loot.enabled", true)

    /** Refresh window in ms (`loot.refresh-hours`); <= 0 disables refresh. */
    private fun refreshMs(): Long = (plugin.config.getInt("loot.refresh-hours", 12) * 3_600_000L)

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onContainerOpen(event: PlayerInteractEvent) {
        if (!enabled() || !plugin.isReady) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.hand != org.bukkit.inventory.EquipmentSlot.HAND) return
        val block = event.clickedBlock ?: return
        if (block.type !in ELIGIBLE) return

        val city = plugin.cityManager.getCachedCityAt(block.location) ?: return
        // Provenance: only actual structure-piece containers are city loot.
        if (!city.inStructurePiece(block.location)) return

        // Player-placed containers keep vanilla behaviour.
        val state = block.state
        if (state is TileState &&
            state.persistentDataContainer.has(playerPlacedKey, PersistentDataType.BYTE)
        ) return

        val container = state as? Container ?: return
        val player = event.player

        // Sneak + admin = edit the shared template; otherwise a per-player copy.
        val isAdminEdit = player.isSneaking && player.hasPermission("betterend.admin")

        event.isCancelled = true

        val now = System.currentTimeMillis()
        val last = openDebounce[player.uniqueId]
        if (last != null && now - last < 700) return
        openDebounce[player.uniqueId] = now
        if (openDebounce.size > 100) openDebounce.entries.removeIf { now - it.value > 10_000 }

        // Resolve the normalized key block (left half of a double chest) and the
        // inventory size SYNCHRONOUSLY — we're on the block's region thread now.
        val inv = container.inventory
        val holder = inv.holder
        val keyBlock = if (holder is DoubleChest) (holder.leftSide as? Chest)?.block ?: block else block
        val size = if (holder is DoubleChest) 54 else inv.size
        val pos = ContainerLootManager.ContainerPos(keyBlock.x, keyBlock.y, keyBlock.z)
        val keyLoc = keyBlock.location
        val keyMaterial = keyBlock.type

        plugin.launchAsync {
            var template = plugin.containerLootManager.loadTemplate(city.id, pos)
            if (template == null) {
                template = materializeOnRegion(keyLoc, size)
                plugin.containerLootManager.saveTemplate(city.id, pos, template, keyMaterial)
            }

            if (isAdminEdit) {
                openVirtual(player, TemplateHolder(city.id, pos), size, TEMPLATE_TITLE, template)
                player.sendMessage(Component.text("§7Editing the shared loot template. Changes apply to every player's first open."))
            } else {
                // First looter past the refresh window starts a new per-city cycle:
                // wipe everyone's copies so the city's loot is fresh again. Editing
                // the template (above) never triggers a cycle. Lazy — no scheduler.
                if (plugin.cityManager.beginCycleIfDue(city.id, refreshMs())) {
                    plugin.containerLootManager.clearCity(city.id)
                    plugin.cityManager.persistCycleStart(city.id)
                    // Optionally restore the structure too (revert griefing) on
                    // the cycle roll. Opt-in — a restore rewrites blocks.
                    if (plugin.config.getBoolean("snapshot.auto-reset-on-refresh", false) &&
                        plugin.snapshotManager.hasSnapshot(city.id)
                    ) {
                        plugin.snapshotManager.restore(city)
                    }
                }
                val existing = plugin.containerLootManager.loadContents(city.id, pos, player.uniqueId)
                openVirtual(player, CopyHolder(city.id, pos), size, COPY_TITLE, existing ?: template)
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onContainerClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val contents = event.inventory.contents.map { it?.clone() }.toTypedArray()
        when (val holder = event.inventory.holder) {
            is CopyHolder -> plugin.launchAsync {
                plugin.containerLootManager.saveContents(holder.cityId, holder.pos, player.uniqueId, contents)
            }
            is TemplateHolder -> plugin.launchAsync {
                plugin.containerLootManager.updateTemplateContents(holder.cityId, holder.pos, contents)
            }
            else -> return
        }
    }

    /** Tag containers players place inside cities so they keep vanilla behaviour. */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onContainerPlace(event: BlockPlaceEvent) {
        if (event.block.type !in ELIGIBLE) return
        if (plugin.cityManager.getCachedCityAt(event.block.location) == null) return
        val state = event.block.state as? TileState ?: return
        state.persistentDataContainer.set(playerPlacedKey, PersistentDataType.BYTE, 1)
        state.update()
    }

    /** Block hopper automation against the pristine template. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHopperMove(event: InventoryMoveItemEvent) {
        if (!enabled()) return
        if (isProtectedTemplate(event.source) || isProtectedTemplate(event.destination)) {
            event.isCancelled = true
        }
    }

    // ==== Helpers ====

    private suspend fun materializeOnRegion(keyLoc: Location, size: Int): Array<ItemStack?> =
        kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            plugin.scheduler.runAtLocation(keyLoc, Runnable {
                val result = try {
                    materializeTemplate(keyLoc.block, size)
                } catch (e: Exception) {
                    plugin.logger.warning("[ContainerLoot] Template materialize failed at ${keyLoc.blockX},${keyLoc.blockY},${keyLoc.blockZ}: ${e.message}")
                    arrayOfNulls<ItemStack?>(size)
                }
                cont.resume(result) {}
            })
        }

    private fun materializeTemplate(block: Block, size: Int): Array<ItemStack?> {
        val container = block.state as? Container ?: return arrayOfNulls(size)
        val holder = container.inventory.holder
        return if (holder is DoubleChest) {
            val out = arrayOfNulls<ItemStack?>(54)
            rollHalf(holder.leftSide as? Chest, out, 0)
            rollHalf(holder.rightSide as? Chest, out, 27)
            out
        } else {
            rollSingle(block.state, container.inventory, size)
        }
    }

    private fun rollSingle(state: BlockState, inv: Inventory, size: Int): Array<ItemStack?> {
        val lootable = state as? Lootable
        val source: Array<ItemStack?> = if (lootable?.lootTable != null && state is Container) {
            val temp = Bukkit.createInventory(null, size)
            lootable.lootTable!!.fillInventory(temp, java.util.Random(), LootContext.Builder(state.block.location).build())
            temp.contents
        } else {
            inv.contents
        }
        return Array(size) { source.getOrNull(it)?.clone() }
    }

    private fun rollHalf(chest: Chest?, out: Array<ItemStack?>, offset: Int) {
        chest ?: return
        val half: Array<ItemStack?> = if (chest.lootTable != null) {
            val temp = Bukkit.createInventory(null, 27)
            chest.lootTable!!.fillInventory(temp, java.util.Random(), LootContext.Builder(chest.block.location).build())
            temp.contents
        } else {
            chest.blockInventory.contents
        }
        for (i in 0 until 27) out[offset + i] = half.getOrNull(i)?.clone()
    }

    private suspend fun openVirtual(
        player: Player,
        holder: InventoryHolder,
        size: Int,
        title: Component,
        contents: Array<ItemStack?>
    ) = kotlinx.coroutines.suspendCancellableCoroutine<Unit> { cont ->
        plugin.scheduler.runAtEntity(player, Runnable {
            try {
                if (player.isOnline) {
                    val virtual = plugin.server.createInventory(holder, size, title)
                    when (holder) {
                        is CopyHolder -> holder.backing = virtual
                        is TemplateHolder -> holder.backing = virtual
                    }
                    for (i in 0 until minOf(size, contents.size)) {
                        virtual.setItem(i, contents[i])
                    }
                    player.openInventory(virtual)
                }
            } catch (e: Exception) {
                plugin.logger.warning("[ContainerLoot] Failed to open container for ${player.name}: ${e.message}")
            }
            cont.resume(Unit) {}
        })
    }

    /**
     * Scans every chunk in [city]'s region for eligible structure-piece
     * containers and materializes a shared template for any without one. Lets
     * admins prep all city loot from a command without opening each container
     * in-world. Returns the number of new templates created. Folia-safe
     * (region-hops per chunk).
     */
    suspend fun materializeCity(city: EndCity): Int {
        val world = city.getWorld() ?: return 0
        var created = 0
        val minCX = city.region.minX shr 4; val maxCX = city.region.maxX shr 4
        val minCZ = city.region.minZ shr 4; val maxCZ = city.region.maxZ shr 4
        for (cx in minCX..maxCX) {
            for (cz in minCZ..maxCZ) {
                val rep = Location(world, (cx shl 4).toDouble(), city.region.minY.toDouble(), (cz shl 4).toDouble())
                val rolled = kotlinx.coroutines.suspendCancellableCoroutine<List<Triple<ContainerLootManager.ContainerPos, Array<ItemStack?>, Material>>> { cont ->
                    plugin.scheduler.runAtLocation(rep, Runnable {
                        val results = mutableListOf<Triple<ContainerLootManager.ContainerPos, Array<ItemStack?>, Material>>()
                        try {
                            if (!world.isChunkLoaded(cx, cz)) world.getChunkAt(cx, cz)
                            for (te in world.getChunkAt(cx, cz).tileEntities) {
                                if (te !is Container) continue
                                val b = te.block
                                if (b.type !in ELIGIBLE) continue
                                if (!city.inStructurePiece(b.location)) continue
                                if ((b.state as? TileState)?.persistentDataContainer
                                        ?.has(playerPlacedKey, PersistentDataType.BYTE) == true) continue
                                val holder = te.inventory.holder
                                val keyBlock = if (holder is DoubleChest) (holder.leftSide as? Chest)?.block ?: b else b
                                if (holder is DoubleChest && keyBlock != b) continue // right half — handled by left
                                val size = if (holder is DoubleChest) 54 else te.inventory.size
                                results.add(
                                    Triple(
                                        ContainerLootManager.ContainerPos(keyBlock.x, keyBlock.y, keyBlock.z),
                                        materializeTemplate(keyBlock, size),
                                        keyBlock.type
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            plugin.logger.warning("[ContainerLoot] City scan failed in chunk $cx,$cz: ${e.message}")
                        }
                        cont.resume(results) {}
                    })
                }
                for ((pos, contents, material) in rolled) {
                    if (!plugin.containerLootManager.hasTemplate(city.id, pos)) {
                        plugin.containerLootManager.saveTemplate(city.id, pos, contents, material)
                        created++
                    }
                }
            }
        }
        return created
    }

    private fun isProtectedTemplate(inv: Inventory): Boolean {
        val block: Block = when (val h = inv.holder) {
            is DoubleChest -> (h.leftSide as? Chest)?.block ?: return false
            is BlockInventoryHolder -> h.block
            else -> return false
        }
        if (block.type !in ELIGIBLE) return false
        val city = plugin.cityManager.getCachedCityAt(block.location) ?: return false
        if (!city.inStructurePiece(block.location)) return false
        val state = block.state as? TileState ?: return false
        return !state.persistentDataContainer.has(playerPlacedKey, PersistentDataType.BYTE)
    }
}
