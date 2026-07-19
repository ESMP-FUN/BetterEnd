package com.esmpfun.betterend.integrations

import com.esmpfun.betterend.BetterEnd
import dev.faststats.Metrics
import dev.faststats.bukkit.BukkitContext
import dev.faststats.data.Metric

/**
 * FastStats integration. Anonymous usage metrics that drive feature
 * prioritization: which database backend servers actually run, elytra
 * claim-mode choice, per-player loot adoption, and fleet city counts.
 *
 * Respect knobs (either disables collection entirely):
 *  - BetterEnd's own `metrics.enabled` in config.yml
 *  - FastStats' global opt-out (`plugins/faststats/config.properties`)
 *
 * The first server start is deliberately silent: FastStats writes its
 * config and submits nothing until the next restart, giving admins a
 * window to set `enabled=false` before any data leaves the box.
 *
 * All metric callables are evaluated by FastStats on its own submission
 * schedule; every supplier below reads cheap in-memory state only.
 */
object MetricsService {

    /**
     * FastStats project token for BetterEnd. A blank value disables
     * metrics init entirely.
     */
    private const val PROJECT_TOKEN: String = "0f6a0acd2f476c81fd3241d567a8c546"

    private var context: BukkitContext? = null

    fun init(plugin: BetterEnd): String {
        if (PROJECT_TOKEN.isBlank()) return "Disabled (no project token)"
        if (!plugin.config.getBoolean("metrics.enabled", true)) return "Disabled (config)"

        return try {
            context = BukkitContext.Factory(plugin, PROJECT_TOKEN)
                .metrics { factory ->
                    factory
                        .addMetric(Metric.string("database_type") {
                            plugin.databaseManager.databaseType.toString().lowercase()
                        })
                        .addMetric(Metric.string("elytra_claim_mode") {
                            plugin.config.getString("elytra.claim-mode", "per-ship")
                        })
                        .addMetric(Metric.bool("per_player_loot") {
                            plugin.config.getBoolean("loot.enabled", true)
                        })
                        .addMetric(Metric.string("city_count") {
                            when (val n = plugin.cityManager.all().size) {
                                0 -> "0"
                                in 1..5 -> "1-5"
                                in 6..20 -> "6-20"
                                in 21..50 -> "21-50"
                                else -> if (n <= 100) "51-100" else "100+"
                            }
                        })
                        .create()
                }
                .create()
                .also { it.ready() }

            "Enabled"
        } catch (e: Exception) {
            plugin.logger.warning("FastStats init failed: ${e.message}")
            "Failed"
        }
    }

    /** Flushes and stops the submission scheduler. Safe to call when init never ran. */
    fun shutdown() {
        context?.let { ctx ->
            context = null
            runCatching { ctx.shutdown() }
        }
    }
}
