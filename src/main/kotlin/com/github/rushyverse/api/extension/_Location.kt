package com.github.rushyverse.api.extension

import com.github.rushyverse.api.world.Pos
import org.bukkit.Location
import org.bukkit.World

/**
 * Get the [Location.world] value from the location.
 * @receiver Location.
 * @return The world where is the location.
 */
public operator fun Location.component1(): World = world

/**
 * Get the [Location.x] value from the location.
 * @receiver Location.
 * @return Value of the coordinate x.
 */
public operator fun Location.component2(): Double = x

/**
 * Get the [Location.y] value from the location.
 * @receiver Location.
 * @return Value of the coordinate y.
 */
public operator fun Location.component3(): Double = y

/**
 * Get the [Location.z] value from the location.
 * @receiver Location.
 * @return Value of the coordinate z.
 */
public operator fun Location.component4(): Double = z

/**
 * Get the [Location.yaw] value from the location.
 * @receiver Location.
 * @return Value of the coordinate yaw.
 */
public operator fun Location.component5(): Float = yaw

/**
 * Get the [Location.pitch] value from the location.
 * @receiver Location.
 * @return Value of the coordinate pitch.
 */
public operator fun Location.component6(): Float = pitch

/**
 * Create a new location with the coordinate center to the current block.
 * Define the x and z properties to the block location + 0.5.
 * @receiver Location.
 * @return New location corresponding to the current location center on the block.
 */
public fun Location.center(): Location = copy(x = blockX + 0.5, z = blockZ + 0.5)

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

public fun Location.toPos(): Pos = Pos(x, y, z, yaw, pitch)