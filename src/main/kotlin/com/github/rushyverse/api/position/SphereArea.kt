package com.github.rushyverse.api.position

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

/**
 * An area defined by a radius.
 * @param E The type of entity.
 * @property entityClass The class of the entity.
 * @property instance The instance where is located the area.
 * @property center The center of the area.
 * @property range The range.
 */
public class SphereArea<E : Entity>(
    public val entityClass: Class<E>,
    public var instance: Instance,
    public var center: Pos,
    range: Double
) : AbstractArea<E>() {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            instance: Instance,
            position: Pos,
            range: Double
        ): SphereArea<E> = SphereArea(E::class.java, instance, position, range)
    }

    public var range: Double = range
        set(value) {
            require(value >= 0.0) { "Radius must be greater than or equal to 0.0" }
            field = value
        }

    override fun update(): Pair<Collection<E>, Collection<E>> {
        return update(instance.getNearbyEntities(center, range).asSequence().filterIsInstance(entityClass).toSet())
    }
}