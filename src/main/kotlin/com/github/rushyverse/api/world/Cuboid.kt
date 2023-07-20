package com.github.rushyverse.api.world

import com.github.rushyverse.api.extension.getStringOrException
import org.bukkit.configuration.ConfigurationSection
import kotlin.jvm.internal.Intrinsics

public class Cuboid(
    public val pos1: Pos,
    public val pos2: Pos
) {

    public operator fun contains(position: Pos): Boolean {
        Intrinsics.checkNotNullParameter(position, "position")
        val minX: Double = pos1.x.coerceAtMost(pos2.x)
        val maxX: Double = pos1.x.coerceAtLeast(pos2.x)
        val minY: Double = pos1.y.coerceAtMost(pos2.y)
        val maxY: Double = pos1.y.coerceAtLeast(pos2.y)
        val minZ: Double = pos1.z.coerceAtMost(pos2.z)
        val maxZ: Double = pos1.z.coerceAtLeast(pos2.z)
        val x: Double = position.x
        if (x in minX..maxX) {
            val y: Double = position.y
            if (y in minY..maxY) {
                val z: Double = position.z
                if (z in minZ..maxZ) {
                    return true
                }
            }
        }
        return false
    }

    public companion object {
        public fun parse(section: ConfigurationSection): Cuboid = Cuboid(
            Pos.parse(section.getStringOrException("pos1")),
            Pos.parse(section.getStringOrException("pos2"))
        )
    }
}
