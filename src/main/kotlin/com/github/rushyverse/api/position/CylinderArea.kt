package com.github.rushyverse.api.position

import com.github.rushyverse.api.extension.isInCylinder
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/**
 * An area defined by a cylinder shape.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property limitY Limit of the y-axis.
 * @property radius Radius.
 */
public class CylinderArea<E : Entity>(
    public val entityClass: Class<E>,
    public override var world: World,
    public override var location: Location,
    radius: Double,
    public var limitY: ClosedRange<Double>,
) : AbstractArea<E>(), IAreaLocatable<E> {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            world: World,
            location: Location,
            radius: Double,
            limitY: ClosedRange<Double>
        ): CylinderArea<E> = CylinderArea(E::class.java, world, location, radius, limitY)
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
        val cylinderPosition = location
        return update(
            world.entities
                .asSequence()
                .filterIsInstance(entityClass)
                .filter { it.location.isInCylinder(cylinderPosition, radius, limitY) }
                .toSet()
        )
    }
}