package com.github.rushyverse.api.world.exception

import org.bukkit.World

/**
 * Represents an exception that is thrown when two provided worlds are not the same or are incompatible
 * for a specific operation.
 *
 * This can be used in contexts where operations rely on the assumption that entities, objects, or
 * other items belong to the same world, and any mismatch would result in unexpected behavior.
 *
 * @property world1 The first world involved in the check, which may be the source or origin.
 * @property world2 The second world involved in the check, which may be the destination or target.
 * @param message Optional error message detailing the context or reason for the exception. If not provided, a default message may be utilized.
 */
public class WorldDifferentException(
    public val world1: World? = null,
    public val world2: World? = null,
    message: String? = null
) :
    RuntimeException(message ?: "The worlds ${world1?.name} and ${world2?.name} are different.")
