package com.esmpfun.betterend.utils

import org.bukkit.Location
import org.bukkit.generator.structure.GeneratedStructure
import org.bukkit.generator.structure.Structure
import org.bukkit.util.BoundingBox
import kotlin.math.ceil

/**
 * Structure-zone detection. Since [org.bukkit.generator.structure.StructurePiece]
 * exposes only a bounding box (no piece name), we work purely off the generated
 * structure's box + a configurable "near" margin.
 *
 * `World.getStructures(chunkX, chunkZ, structure)` returns structures with a
 * piece in that chunk, so we sweep the chunks around the location (widened by the
 * near-margin) and test each structure's (expanded) box for containment.
 */
object StructureUtil {

    /**
     * The unexpanded [BoundingBox] of the [structure] instance that contains [loc]
     * within [near] blocks of its bounds, or null if none is nearby.
     */
    fun boxNear(loc: Location, structure: Structure, near: Double): BoundingBox? =
        generatedNear(loc, structure, near)?.boundingBox

    /**
     * The [GeneratedStructure] instance of [structure] that contains [loc] within
     * [near] blocks of its bounds, or null. Gives access to `getPieces()` (used by
     * the End-ship boss to locate the ship's dragon-head anchor).
     */
    fun generatedNear(loc: Location, structure: Structure, near: Double): GeneratedStructure? {
        val world = loc.world ?: return null
        val chunkRadius = ceil(near / 16.0).toInt() + 1
        val baseCx = loc.blockX shr 4
        val baseCz = loc.blockZ shr 4

        for (dx in -chunkRadius..chunkRadius) {
            for (dz in -chunkRadius..chunkRadius) {
                for (generated in world.getStructures(baseCx + dx, baseCz + dz, structure)) {
                    // expand(near) mutates; clone first so we don't grow the live box.
                    if (generated.boundingBox.clone().expand(near).contains(loc.x, loc.y, loc.z)) {
                        return generated
                    }
                }
            }
        }
        return null
    }

    /** True if [loc] is inside, or within [near] blocks of, an instance of [structure]. */
    fun isNear(loc: Location, structure: Structure, near: Double): Boolean =
        generatedNear(loc, structure, near) != null
}
