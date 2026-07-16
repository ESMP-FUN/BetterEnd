package com.esmpfun.betterend.integrations

import com.esmpfun.betterend.BetterEnd
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie

/**
 * bStats integration. Anonymous usage metrics that drive feature
 * prioritization: which database backend servers actually run, elytra
 * claim-mode choice, per-player loot adoption, and fleet city counts.
 *
 * Respect knobs (either disables collection entirely):
 *  - BetterEnd's own `metrics.enabled` in config.yml
 *  - bStats' global opt-out (`plugins/bStats/config.yml`)
 *
 * All chart callables are evaluated by bStats on its own submission
 * schedule (~30 min) on the main thread; every supplier below reads
 * cheap in-memory state only.
 */
object MetricsService {

    /**
     * bStats service id for BetterEnd, registered at
     * https://bstats.org/plugin/bukkit/Better%20End/32676.
     * A value <= 0 disables metrics init entirely.
     */
    private const val BSTATS_SERVICE_ID: Int = 32676

    fun init(plugin: BetterEnd): String {
        if (BSTATS_SERVICE_ID <= 0) return "Disabled (no service id)"
        if (!plugin.config.getBoolean("metrics.enabled", true)) return "Disabled (config)"

        return try {
            val metrics = Metrics(plugin, BSTATS_SERVICE_ID)

            metrics.addCustomChart(SimplePie("database_type") {
                plugin.databaseManager.databaseType.toString().lowercase()
            })

            metrics.addCustomChart(SimplePie("elytra_claim_mode") {
                plugin.config.getString("elytra.claim-mode", "per-ship")
            })

            metrics.addCustomChart(SimplePie("per_player_loot") {
                plugin.config.getBoolean("loot.enabled", true).toString()
            })

            metrics.addCustomChart(SimplePie("city_count") {
                when (val n = plugin.cityManager.all().size) {
                    0 -> "0"
                    in 1..5 -> "1-5"
                    in 6..20 -> "6-20"
                    in 21..50 -> "21-50"
                    else -> if (n <= 100) "51-100" else "100+"
                }
            })

            "Enabled"
        } catch (e: Exception) {
            plugin.logger.warning("bStats init failed: ${e.message}")
            "Failed"
        }
    }
}
