package com.github.rushyverse.api.world

import org.bukkit.Location
import org.bukkit.World

public data class Pos(
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0F,
    val pitch: Float = 0F
) {
    public companion object {
        public fun parse(strPosition: String): Pos {
            val split = strPosition.split(" ")

            val yaw = if (split.size > 3) {
                split[3].toFloat()
            } else 0F

            val pitch = if (split.size > 4) {
                split[4].toFloat()
            } else 0F

            return Pos(
                split[0].toDouble(),
                split[1].toDouble(),
                split[2].toDouble(),
                yaw,
                pitch,
            )
        }
    }

    public fun toLocation(world: World): Location = Location(world, x, y, z, yaw, pitch)
}