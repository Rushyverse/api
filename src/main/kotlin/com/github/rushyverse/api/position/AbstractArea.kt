package com.github.rushyverse.api.position

import net.minestom.server.entity.Entity
import java.util.*

/**
 * An area that contains entities.
 * @param E The type of entity.
 * @property _entitiesInArea The mutable entities in the area.
 * @property entitiesInArea The entities in the area.
 */
public abstract class AbstractArea<E : Entity> : IArea<E> {

    private val _entitiesInArea: MutableSet<E> = Collections.synchronizedSet(mutableSetOf())

    override val entitiesInArea: Set<E>
        get() = _entitiesInArea

    /**
     * Update the entities in the area.
     * @param inArea The entities in the area.
     * @return A pair of the entities that were added and the entities that were removed.
     */
    protected fun update(inArea: Set<E>): Pair<Collection<E>, Collection<E>> {
        val enter = inArea - entitiesInArea
        val quit = entitiesInArea - inArea

        _entitiesInArea.clear()
        _entitiesInArea.addAll(inArea)
        return enter to quit
    }
}