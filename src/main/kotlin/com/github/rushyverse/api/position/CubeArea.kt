package com.github.rushyverse.api.position

import com.github.rushyverse.api.extension.isInCube
import com.github.rushyverse.api.extension.minMaxOf
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

/**
 * A cuboid area defined by two positions.
 * @param E Type of entity.
 * @property entityClass Class of the entity.
 * @property instance Instance where is located the area.
 * @property min Minimum position.
 * @property max Maximum position.
 */
public class CubeArea<E : Entity>(
    public val entityClass: Class<E>,
    public var instance: Instance,
    position1: Pos,
    position2: Pos
) : AbstractArea<E>() {

    public companion object {
        public inline operator fun <reified E : Entity> invoke(
            instance: Instance,
            position1: Pos,
            position2: Pos
        ): CubeArea<E> = CubeArea(E::class.java, instance, position1, position2)
    }

    public val min: Pos
    public val max: Pos

    init {
        val (x1, x2) = minMaxOf(position1.x(), position2.x())
        val (y1, y2) = minMaxOf(position1.y(), position2.y())
        val (z1, z2) = minMaxOf(position1.z(), position2.z())
        this.min = Pos(x1, y1, z1)
        this.max = Pos(x2, y2, z2)
    }

    override fun update(): Pair<Collection<E>, Collection<E>> {
        return update(instance.entities
            .asSequence()
            .filterIsInstance(entityClass)
            .filter { it.position.isInCube(min, max) }
            .toSet()
        )
    }
}


