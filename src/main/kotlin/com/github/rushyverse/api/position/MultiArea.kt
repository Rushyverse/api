package com.github.rushyverse.api.position

import net.minestom.server.entity.Entity

/**
 * An area corresponding to multiple areas.
 * @param E Type of entity.
 * @property _areas Mutable collection of areas.
 * @property areas Collection of areas.
 */
public class MultiArea<E : Entity>(areas: MutableSet<IArea<E>> = mutableSetOf()) : AbstractArea<E>() {

    private val _areas: MutableSet<IArea<E>> = areas
    public val areas: Set<IArea<E>> get() = _areas

    /**
     * Adds an area.
     * @param area Area to add.
     * @return `true` if the area was added, `false` otherwise.
     */
    public fun addArea(area: IArea<E>): Boolean = _areas.add(area)

    /**
     * Removes an area.
     * @param area Area to remove.
     * @return `true` if the area was removed, `false` otherwise.
     */
    public fun removeArea(area: IArea<E>): Boolean = _areas.remove(area)

    /**
     * Removes all areas.
     */
    public fun removeAllAreas() {
        _areas.clear()
    }

    override fun updateEntitiesInArea(): Pair<Collection<E>, Collection<E>> {
        return update(
            areas.asSequence()
                .onEach { it.updateEntitiesInArea() }
                .flatMap { it.entitiesInArea }
                .toSet()
        )
    }
}