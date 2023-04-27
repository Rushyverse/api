package com.github.rushyverse.api.position

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/**
 * An area defined by a sphere shape.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property radius Radius.
 */
public class SphereArea<E : Entity>(
    public val entityClass: Class<E>,
    public override var world: World,
    public override var location: Location,
    radius: Double
) : AbstractArea<E>(), IAreaLocatable<E> {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            world: World,
            location: Location,
            radius: Double
        ): SphereArea<E> = SphereArea(E::class.java, world, location, radius)
    }

    public var radius: Double = radius
        set(value) {
            verifyNewRadiusValue(value)
            field = value
        }

    init {
        verifyNewRadiusValue(radius)
    }

    /**
     * Verifies that the new radius value is greater than or equal to 0.0.
     * @param value New radius value.
     */
    private fun verifyNewRadiusValue(value: Double) {
        require(value >= 0.0) { "Radius must be greater than or equal to 0.0" }
    }

    override fun updateEntitiesInArea(): Pair<Collection<E>, Collection<E>> {
        return update(world.getNearbyEntities(location, radius, radius, radius).asSequence().filterIsInstance(entityClass).toSet())
    }
}