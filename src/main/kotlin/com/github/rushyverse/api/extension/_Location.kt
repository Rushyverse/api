package com.github.rushyverse.api.extension

import org.bukkit.Location
import org.bukkit.World

/**
 * Define the position (x, y, z, pitch, yaw) and the world with the same value as the location in parameter.
 * @receiver Location that will have its values modified
 * @param location Location where values will be retrieved.
 */
public fun Location.copyFrom(location: Location) {
    world = location.world
    x = location.x
    y = location.y
    z = location.z
    pitch = location.pitch
    yaw = location.yaw
}

/**
 * Create a copy of the location.
 * @receiver Location using to create a new location with the same properties.
 * @param world The world in which this location resides
 * @param x The x-coordinate of this new location
 * @param y The y-coordinate of this new location
 * @param z The z-coordinate of this new location
 * @param yaw The absolute rotation on the x-plane, in degrees
 * @param pitch The absolute rotation on the y-plane, in degrees
 * @return Location
 */
public fun Location.copy(
    world: World = this.world,
    x: Double = this.x,
    y: Double = this.y,
    z: Double = this.z,
    yaw: Float = this.yaw,
    pitch: Float = this.pitch,
): Location = Location(world, x, y, z, yaw, pitch)

/**
 * Divides each coordinate of the current location by the given value.
 *
 * @receiver The current location.
 * @param value The value to divide each coordinate by.
 * @return A new location with the divided coordinates.
 */
public fun Location.divide(value: Number): Location {
    val toDouble = value.toDouble()
    return copy(x = x / toDouble, y = y / toDouble, z = z / toDouble)
}

/**
 * Returns the center position between two points.
 * @receiver The first position.
 * @param other The second position.
 * @return The center position between the two points.
 */
public fun Location.centerRelative(other: Location): Location = add(other).divide(2)
