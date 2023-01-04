package com.github.rushyverse.api.position

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.instance.Instance

/**
 * An area with a position into an instance.
 * @param E Type of entity.
 * @property instance Instance where is located the area.
 * @property position Position of the area.
 */
public interface IAreaLocatable<E: Entity> : IArea<E> {

    public var instance: Instance

    public var position: Pos
}