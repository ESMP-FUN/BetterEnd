package com.esmpfun.betterend.models

import org.bukkit.Location
import org.bukkit.util.BoundingBox

/**
 * An inclusive, integer block-coordinate axis-aligned box. World-agnostic
 * (the owning [EndCity] carries the world). Used both for a city's region
 * envelope and for each structure piece's bounds.
 */
data class IntBox(
    val minX: Int, val minY: Int, val minZ: Int,
    val maxX: Int, val maxY: Int, val maxZ: Int,
) {
    fun contains(x: Int, y: Int, z: Int): Boolean =
        x in minX..maxX && y in minY..maxY && z in minZ..maxZ

    fun contains(loc: Location): Boolean = contains(loc.blockX, loc.blockY, loc.blockZ)

    /** This box expanded by [pad] blocks on every face. */
    fun expanded(pad: Int): IntBox =
        IntBox(minX - pad, minY - pad, minZ - pad, maxX + pad, maxY + pad, maxZ + pad)

    companion object {
        /**
         * Converts a Bukkit [BoundingBox] (as returned by
         * `GeneratedStructure`/`StructurePiece`) to inclusive block coords.
         *
         * Bukkit's `BoundingBox` upper corner is exclusive (a single block at
         * (0,0,0) is min(0,0,0)..max(1,1,1)), so we subtract 1 for an inclusive
         * block max.
         */
        fun fromBukkit(b: BoundingBox): IntBox = IntBox(
            b.minX.toInt(), b.minY.toInt(), b.minZ.toInt(),
            (b.maxX - 1).toInt(), (b.maxY - 1).toInt(), (b.maxZ - 1).toInt(),
        )

        /** The smallest box enclosing all of [boxes]. Throws on an empty list. */
        fun union(boxes: List<IntBox>): IntBox {
            require(boxes.isNotEmpty()) { "cannot union zero boxes" }
            return IntBox(
                boxes.minOf { it.minX }, boxes.minOf { it.minY }, boxes.minOf { it.minZ },
                boxes.maxOf { it.maxX }, boxes.maxOf { it.maxY }, boxes.maxOf { it.maxZ },
            )
        }
    }
}
