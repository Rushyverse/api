package com.github.rushyverse.api.position

import com.github.rushyverse.api.extension.isInCylinder
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

/**
 * An area defined by a cylinder shape.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property instance Instance where is located the area.
 * @property position Position of the cylinder.
 * @property limitY Limit of the y-axis.
 * @property radius Radius.
 */
public class CylinderArea<E : Entity>(
    public val entityClass: Class<E>,
    public var instance: Instance,
    public var position: Pos,
    radius: Double,
    public var limitY: ClosedRange<Double>,
) : AbstractArea<E>() {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            instance: Instance,
            position: Pos,
            radius: Double,
            limitY: ClosedRange<Double>
        ): CylinderArea<E> = CylinderArea(E::class.java, instance, position, radius, limitY)
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
        val cylinderPosition = position
        return update(
            instance.entities
                .asSequence()
                .filterIsInstance(entityClass)
                .filter { it.position.isInCylinder(cylinderPosition, radius, limitY) }
                .toSet()
        )
    }
}