package com.github.rushyverse.api.position

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity

/**
 * An area with a location into an instance.
 * @param E Type of entity.
 * @property world World where is located the area.
 * @property location Location of the area.
 */
public interface IAreaLocatable<E : Entity> : IArea<E> {

    public var world: World

    public var location: Location
}