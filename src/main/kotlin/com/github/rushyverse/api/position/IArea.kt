package com.github.rushyverse.api.position

import org.bukkit.entity.Entity

/**
 * An area that contains entities.
 * @param E The type of entity.
 * @property entitiesInArea The entities in the area.
 */
public interface IArea<E : Entity> {

    public val entitiesInArea: Set<E>

    /**
     * Compute and update the entities in the area.
     * @return A pair of the entities that were added and the entities that were removed.
     */
    public fun updateEntitiesInArea(): Pair<Collection<E>, Collection<E>>
}