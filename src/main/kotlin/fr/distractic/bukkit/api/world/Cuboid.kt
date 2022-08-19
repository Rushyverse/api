package fr.distractic.bukkit.api.world

import fr.distractic.bukkit.api.delegate.DelegateWorld
import fr.distractic.bukkit.api.extension.minMax
import fr.distractic.bukkit.api.world.exception.WorldDifferentException
import fr.distractic.bukkit.api.world.exception.WorldNotFoundException
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import java.util.*

/**
 * Represents an area in a minecraft world.
 * @property uuidWorld World's UUID.
 * @property world World retrieve from the server.
 * @property minCoordinate Position with minimal coordinate's value.
 * @property maxCoordinate Position with maximal coordinate's value.
 */
public class Cuboid(pluginId: String, public val uuidWorld: UUID, x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {

    public companion object {

        /**
         * Create a cuboid using the position in location instances.
         * Throws an exception if the world of both locations are different.
         * @param location1 Location 1
         * @param location2 Location 2
         * @return New instance of cuboid.
         */
        public fun of(pluginId: String, location1: Location, location2: Location): Cuboid {
            val world1 = location1.world
            val world2 = location2.world
            if (world1 != world2) {
                throw WorldDifferentException(world1, world2, "The locations don't have the same world")
            }
            return Cuboid(
                pluginId,
                world1.uid,
                location1.blockX,
                location1.blockY,
                location1.blockZ,
                location2.blockX,
                location2.blockY,
                location2.blockZ
            )
        }
    }

    public val world: World? by DelegateWorld(pluginId, uuidWorld)

    public val minCoordinate: BlockPosition
    public val maxCoordinate: BlockPosition

    init {
        val (minX, maxX) = minMax(x1, x2)
        val (minY, maxY) = minMax(y1, y2)
        val (minZ, maxZ) = minMax(z1, z2)
        minCoordinate = BlockPosition(minX, minY, minZ)
        maxCoordinate = BlockPosition(maxX, maxY, maxZ)
    }

    /**
     * Retrieve the instance of world.
     * If the world is not found from the server, thrown an exception.
     * @return The instance of world.
     */
    public fun requireWorld(): World = world ?: throw WorldNotFoundException("The world cannot be retrieved from the server")

    /**
     * Know if a location is in the area.
     *
     * @param location Location.
     * @return `true` if the location is between bounds, `false` otherwise.
     */
    public operator fun contains(location: Location): Boolean = location.world == world
            && location.blockX in minCoordinate.x..maxCoordinate.x
            && location.blockY in minCoordinate.y..maxCoordinate.y
            && location.blockZ in minCoordinate.z..maxCoordinate.z

    /**
     * Create an iterator to iterate on all locations present in the area.
     * @return Iterator of location.
     */
    public fun locationSequence(): Sequence<Location> = sequence {
        val world = requireWorld()
        for (x in minCoordinate.x..maxCoordinate.x) {
            val xDouble = x.toDouble()
            for (y in minCoordinate.y..maxCoordinate.y) {
                val yDouble = y.toDouble()
                for (z in minCoordinate.z..maxCoordinate.z) {
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
        return locationSequence()
            .map { it.world.getBlockAt(it) }
    }
}