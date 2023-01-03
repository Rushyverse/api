package com.github.rushyverse.api.position

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

/**
 * An area defined by a sphere shape.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property instance Instance where is located the area.
 * @property position Position of the sphere.
 * @property radius Radius.
 */
public class SphereArea<E : Entity>(
    public val entityClass: Class<E>,
    public var instance: Instance,
    public var position: Pos,
    radius: Double
) : AbstractArea<E>() {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            instance: Instance,
            position: Pos,
            radius: Double
        ): SphereArea<E> = SphereArea(E::class.java, instance, position, radius)
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
        return update(instance.getNearbyEntities(position, radius).asSequence().filterIsInstance(entityClass).toSet())
    }
}