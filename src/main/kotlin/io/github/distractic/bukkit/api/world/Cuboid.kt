package io.github.distractic.bukkit.api.world

import io.github.distractic.bukkit.api.extension.minMax
import io.github.distractic.bukkit.api.world.exception.WorldDifferentException
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block

/**
 * Represents an area in a minecraft world.
 * @property startPosition Position with minimal coordinate's value.
 * @property endPosition Position with maximal coordinate's value.
 */
public class Cuboid(location1: Location, location2: Location) {

    public companion object {

        /**
         * Generate cuboid from a string.
         * Each argument is split by space and correspond to a specific data.
         * The first argument is the world name.
         * The second argument is the x coordinate.
         * The third argument is the y coordinate.
         * The fourth argument is the z coordinate.
         * The fifth argument is the x size.
         * The sixth argument is the y size.
         * The seventh argument is the z size.
         */
        public fun fromString(string: String): Cuboid {
            val args = string.split(' ')
            val world = Bukkit.getWorld(args[0]) ?: throw IllegalArgumentException("World not found")
            val x = args[1].toDouble()
            val y = args[2].toDouble()
            val z = args[3].toDouble()
            val xSize = args[4].toDouble()
            val ySize = args[5].toDouble()
            val zSize = args[6].toDouble()
            return Cuboid(Location(world, x, y, z), Location(world, xSize, ySize, zSize))
        }
    }

    public val startPosition: Location
    public val endPosition: Location

    init {
        val world1 = location1.world
        val world2 = location2.world
        if (world1 != world2) {
            throw WorldDifferentException(world1, world2, "The locations don't have the same world")
        }

        val (minX, maxX) = minMax(location1.x, location2.x)
        val (minY, maxY) = minMax(location1.y, location2.y)
        val (minZ, maxZ) = minMax(location1.z, location2.z)
        startPosition = Location(world1, minX, minY, minZ)
        endPosition = Location(world1, maxX, maxY, maxZ)
    }

    /**
     * Know if a location is in the area.
     *
     * @param location Location.
     * @return `true` if the location is between bounds, `false` otherwise.
     */
    public operator fun contains(location: Location): Boolean = location.world == startPosition.world
            && location.x in startPosition.x..endPosition.x
            && location.y in startPosition.y..endPosition.y
            && location.z in startPosition.z..endPosition.z

    /**
     * Create an iterator to iterate on all locations present in the area.
     * @return Iterator of location.
     */
    public fun locationSequence(): Sequence<Location> = sequence {
        val world = startPosition.world
        for (x in startPosition.blockX..endPosition.blockX) {
            val xDouble = x.toDouble()
            for (y in startPosition.blockY..endPosition.blockY) {
                val yDouble = y.toDouble()
                for (z in startPosition.blockZ..endPosition.blockZ) {
                    yield(Location(world, xDouble, yDouble, z.toDouble()))
                }
            }
        }
    }

    /**
     * Create an iterator to iterate on all blocks present in the area.
     * @return Iterator of location.
     */
    public fun blockSequence(): Sequence<Block> {
        return locationSequence().map { it.world.getBlockAt(it) }
    }
}