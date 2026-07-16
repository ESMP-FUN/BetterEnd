package com.esmpfun.betterend

import com.esmpfun.betterend.commands.BeCommand
import com.esmpfun.betterend.database.DatabaseManager
import com.esmpfun.betterend.gui.framework.VcGuiListener
import com.esmpfun.betterend.integrations.MetricsService
import com.esmpfun.betterend.listeners.CityDiscoveryListener
import com.esmpfun.betterend.listeners.ContainerLootListener
import com.esmpfun.betterend.listeners.ElytraFrameListener
import com.esmpfun.betterend.listeners.ProtectionListener
import com.esmpfun.betterend.managers.CityDiscoveryManager
import com.esmpfun.betterend.managers.CityManager
import com.esmpfun.betterend.managers.ContainerLootManager
import com.esmpfun.betterend.managers.ElytraClaimManager
import com.esmpfun.betterend.managers.SnapshotManager
import com.esmpfun.betterend.scheduler.SchedulerAdapter
import com.esmpfun.betterend.setup.SetupReminderListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * BetterEnd — turns the End into renewable, multiplayer-friendly content:
 * per-player elytras straight from the ship's item frame (no vault block, no
 * datapack), per-player End City loot, grief protection, and snapshot-based
 * structure resets.
 *
 * Standalone plugin. Reuses the proven BetterAncientCities architecture
 * (async-first init, [SchedulerAdapter] Paper/Folia abstraction, per-player
 * container loot, gzip snapshots) by port, not by dependency, plus Mantle's
 * MC26 dialog config approach.
 */
class BetterEnd : JavaPlugin() {

    /** Flips true once async init completes; gates command/listener execution. */
    @Volatile
    var isReady: Boolean = false
        private set

    /** Paper/Folia scheduler abstraction. */
    lateinit var scheduler: SchedulerAdapter
        private set

    lateinit var databaseManager: DatabaseManager
        private set

    lateinit var cityManager: CityManager
        private set

    lateinit var discoveryManager: CityDiscoveryManager
        private set

    lateinit var containerLootManager: ContainerLootManager
        private set

    lateinit var elytraClaimManager: ElytraClaimManager
        private set

    lateinit var snapshotManager: SnapshotManager
        private set

    /** Directory holding per-city snapshot files. */
    val snapshotsDir: File by lazy { File(dataFolder, "snapshots").apply { mkdirs() } }

    // Plugin-wide coroutine scope (SupervisorJob so one failed job doesn't tear
    // down the rest). Cancelled in onDisable.
    private val pluginJob = SupervisorJob()
    val pluginScope = CoroutineScope(Dispatchers.Default + pluginJob)

    /** Launch an async coroutine on the plugin scope. */
    fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job =
        pluginScope.launch(block = block)

    override fun onEnable() {
        saveDefaultConfig()
        scheduler = SchedulerAdapter.create(this)

        logger.info("BetterEnd starting on ${if (scheduler.isFolia) "Folia" else "Paper"}...")

        // Async-first init: heavy setup (DB, caches, discovery sweep) runs off
        // the main thread; listeners/commands register on the main thread once
        // ready.
        databaseManager = DatabaseManager(this)
        cityManager = CityManager(this)
        discoveryManager = CityDiscoveryManager(this)
        containerLootManager = ContainerLootManager(this)
        elytraClaimManager = ElytraClaimManager(this)
        snapshotManager = SnapshotManager(this)

        launchAsync {
            try {
                databaseManager.initialize()
                cityManager.preload()
                elytraClaimManager.preload()
                scheduler.runTask(Runnable {
                    server.pluginManager.registerEvents(CityDiscoveryListener(this@BetterEnd), this@BetterEnd)
                    server.pluginManager.registerEvents(ContainerLootListener(this@BetterEnd), this@BetterEnd)
                    server.pluginManager.registerEvents(ProtectionListener(this@BetterEnd), this@BetterEnd)
                    server.pluginManager.registerEvents(ElytraFrameListener(this@BetterEnd), this@BetterEnd)
                    server.pluginManager.registerEvents(SetupReminderListener(this@BetterEnd), this@BetterEnd)
                    // Central GUI dispatcher — routes only BaseHolder inventories.
                    server.pluginManager.registerEvents(VcGuiListener(), this@BetterEnd)

                    // Paper plugins register commands in code, not via paper-plugin.yml.
                    @Suppress("UnstableApiUsage")
                    registerCommand("betterend", "BetterEnd admin command & config menu", BeCommand(this@BetterEnd))

                    // Update checking (PluginPulse). Config in pluginpulse.yml;
                    // server owners can override mode/interval via an `update:`
                    // block in config.yml.
                    io.github.darkstarworks.pluginpulse.PluginPulse.bootstrap(this@BetterEnd)

                    // Anonymous usage metrics (bStats). Opt-out via metrics.enabled
                    // in config.yml or the global plugins/bStats/config.yml.
                    logger.info("bStats Metrics: ${MetricsService.init(this@BetterEnd)}")

                    isReady = true
                    logger.info("BetterEnd ready.")
                    // Catch cities in chunks already resident at enable (the live
                    // ChunkLoadEvent covers everything loaded afterward).
                    discoveryManager.startupSweep()
                })
            } catch (e: Exception) {
                logger.severe("BetterEnd failed to initialize: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun onDisable() {
        isReady = false
        io.github.darkstarworks.pluginpulse.PluginPulse.shutdown(this)
        scheduler.cancelAllTasks()
        pluginScope.cancel()
        if (::databaseManager.isInitialized) databaseManager.close()
        logger.info("BetterEnd disabled.")
    }
}
