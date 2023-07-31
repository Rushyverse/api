package com.github.rushyverse.api.world

import org.bukkit.Location

/**
 * Checks if the given Location is within the specified Area.
 *
 * @receiver The Location object.
 * @param area The Area to check against.
 * @return true if the Location is within the Area, false otherwise.
 */
public infix fun Location.isIn(area: Area): Boolean = area.isInArea(this)

/**
 * Represents an area in the world.
 * @property location The location of the area.
 */
public interface Area {

    public var location: Location

    /**
     * Determines if a given location is within the specified area.
     *
     * @param location The location to check.
     * @return `true` if the location is within the area, `false` otherwise.
     */
    public fun isInArea(location: Location): Boolean
}
