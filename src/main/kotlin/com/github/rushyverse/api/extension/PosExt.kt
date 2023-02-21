package com.github.rushyverse.api.extension

import net.minestom.server.coordinate.Pos
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Returns whether the given [Pos] is in the cube defined by the two given [Pos]s.
 * @receiver The position to check.
 * @param min The minimum position of the cube.
 * @param max The maximum position of the cube.
 * @return `true` if the position is in the cube, `false` otherwise.
 */
public fun Pos.isInCube(min: Pos, max: Pos): Boolean {
    return x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
}

/**
 * Returns whether the given [Pos] is in the cylinder defined by the given [positionCylinder], [radius] and height defined by [limitY].
 * @receiver The position to check.
 * @param positionCylinder The position of the cylinder.
 * @param radius The radius of the cylinder.
 * @param limitY The height of the cylinder.
 * @return `true` if the position is in the cylinder, `false` otherwise.
 */
public fun Pos.isInCylinder(positionCylinder: Pos, radius: Double, limitY: ClosedRange<Double>): Boolean {
    val distance = sqrt((x - positionCylinder.x).pow(2.0) + (z - positionCylinder.z).pow(2.0))
    return distance <= radius && y in limitY
}