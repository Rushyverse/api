package io.github.rushyverse.api.extension

import net.minestom.server.entity.Entity
import net.minestom.server.thread.Acquirable
import net.minestom.server.thread.AcquirableCollection

/**
 * Get the [Acquirable] of the entity.
 */
public val <E : Entity> E.acquirable: Acquirable<E> get() = getAcquirable()

/**
 * Transform an iterable of entities into an [AcquirableCollection].
 * @receiver Iterable of entities.
 * @return An [AcquirableCollection] of entities.
 */
public fun <E : Entity> Iterable<E>.toAcquirables(): AcquirableCollection<E> {
    return AcquirableCollection(map { it.getAcquirable() })
}

/**
 * Transform an array of entities into an [AcquirableCollection].
 * @receiver Array of entities.
 * @return An [AcquirableCollection] of entities.
 */
public fun <E : Entity> Array<E>.toAcquirables(): AcquirableCollection<E> {
    return AcquirableCollection(map { it.getAcquirable() })
}

/**
 * Transform a sequence of entities into an [AcquirableCollection].
 * @receiver Sequence of entities.
 * @return An [AcquirableCollection] of entities.
 */
public fun <E : Entity> Sequence<E>.toAcquirables(): AcquirableCollection<E> {
    return AcquirableCollection(map { it.acquirable }.toList())
}

