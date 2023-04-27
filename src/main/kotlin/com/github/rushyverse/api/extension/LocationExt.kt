package com.github.rushyverse.api.extension

import com.github.rushyverse.api.serializer.Pos
import org.bukkit.Location
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Returns the center location between two points.
 * @receiver The first location.
 * @param other The second location.
 * @return The center location between the two points.
 */
public fun Location.centerRelative(other: Location): Location =
    add(other).multiply(0.5)

/**
 * Returns whether the given [Location] is in the cube defined by the two given [Location]s.
 * @receiver The location to check.
 * @param min The minimum location of the cube.
 * @param max The maximum location of the cube.
 * @return `true` if the location is in the cube, `false` otherwise.
 */
public fun Location.isInCube(min: Location, max: Location): Boolean {
    return x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
}

/**
 * Returns whether the given [Location] is in the cylinder defined by the given [locationCylinder], [radius] and height defined by [limitY].
 * @receiver The location to check.
 * @param locationCylinder The location of the cylinder.
 * @param radius The radius of the cylinder.
 * @param limitY The height of the cylinder.
 * @return `true` if the location is in the cylinder, `false` otherwise.
 */
public fun Location.isInCylinder(locationCylinder: Location, radius: Double, limitY: ClosedRange<Double>): Boolean {
    val distance = sqrt((x - locationCylinder.x).pow(2.0) + (z - locationCylinder.z).pow(2.0))
    return distance <= radius && y in limitY
}

/**
 * Converts this [Location] object to a [Pos] object.
 * This function allows serializing coordinates without taking the world into account.
 *
 * @receiver The [Location] to convert.
 * @return A new [Pos] object with the same position and rotation as this [Location] object.
 */
public fun Location.toPos(): Pos = Pos(x, y, z, yaw, pitch)
