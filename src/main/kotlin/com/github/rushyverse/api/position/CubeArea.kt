package com.github.rushyverse.api.position

import com.github.rushyverse.api.extension.centerRelative
import com.github.rushyverse.api.extension.isInCube
import com.github.rushyverse.api.extension.minMaxOf
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/**
 * A cuboid area defined by two positions.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property min Minimum position.
 * @property max Maximum position.
 * @property position Center position of the cube
 */
public class CubeArea<E : Entity>(
    public val entityClass: Class<E>,
    public override var world: World,
    location1: Location,
    location2: Location
) : AbstractArea<E>(), IAreaLocatable<E> {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            world: World,
            location1: Location,
            location2: Location
        ): CubeArea<E> = CubeArea(E::class.java, world, location1, location2)
    }

    override var location: Location
        get() = max.centerRelative(min)
        set(value) {
            // The new position becomes the center of the cube.
            val halfSize = max.toVector().subtract(min.toVector()).multiply(0.5)
            min = value.toVector().subtract(halfSize).toLocation(value.world)
            max = value.toVector().add(halfSize).toLocation(value.world)
        }

    public var min: Location
        private set

    public var max: Location
        private set

    init {
        val (x1, x2) = minMaxOf(location1.x(), location2.x())
        val (y1, y2) = minMaxOf(location1.y(), location2.y())
        val (z1, z2) = minMaxOf(location1.z(), location2.z())
        this.min = Location(world, x1, y1, z1)
        this.max = Location(world, x2, y2, z2)
    }

    override fun updateEntitiesInArea(): Pair<Collection<E>, Collection<E>> {
        return update(world.entities
            .asSequence()
            .filterIsInstance(entityClass)
            .filter { it.location.isInCube(min, max) }
            .toSet()
        )
    }
}


