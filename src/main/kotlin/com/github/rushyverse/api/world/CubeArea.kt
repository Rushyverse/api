package com.github.rushyverse.api.world

import com.github.rushyverse.api.extension.centerRelative
import com.github.rushyverse.api.extension.minMaxOf
import org.bukkit.Location

/**
 * A cuboid area defined by two positions.
 * @property min Minimum position.
 * @property max Maximum position.
 * @property location Center position of the cube.
 */
public class CubeArea(loc1: Location, loc2: Location) {

    public var location: Location
        get() = max.centerRelative(min)
        set(value) {
            // The new position becomes the center of the cube.
            val halfSize = max.centerRelative(min)
            min = value.subtract(halfSize)
            max = value.add(halfSize)
        }

    public var min: Location
        private set

    public var max: Location
        private set

    init {
        val world1 = loc1.world
        val world2 = loc2.world
        require(world1 === world2) { "Locations must be in the same world" }

        val (x1, x2) = minMaxOf(loc1.x, loc2.x)
        val (y1, y2) = minMaxOf(loc1.y, loc2.y)
        val (z1, z2) = minMaxOf(loc1.z, loc2.z)
        this.min = Location(world1, x1, y1, z1)
        this.max = Location(world2, x2, y2, z2)
    }
}
