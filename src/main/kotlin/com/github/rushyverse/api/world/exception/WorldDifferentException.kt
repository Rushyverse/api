package com.github.rushyverse.api.world.exception

import org.bukkit.World

/**
 * Exception when two worlds are different.
 */
public class WorldDifferentException(
    public val world1: World? = null,
    public val world2: World? = null,
    message: String? = null
) :
    RuntimeException(message)