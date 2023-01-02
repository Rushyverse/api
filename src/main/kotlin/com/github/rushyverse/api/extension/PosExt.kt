package com.github.rushyverse.api.extension

import net.minestom.server.coordinate.Pos

/**
 * Returns whether the given [Pos] is in the cube defined by the two given [Pos]s.
 * @receiver The position to check.
 * @param min The minimum position of the cube.
 * @param max The maximum position of the cube.
 * @return `true` if the position is in the cube, `false` otherwise.
 */
public fun Pos.isInCube(min: Pos, max: Pos): Boolean {
    return x in min.x..max.x && y in min.y..max.y && z in min.z..max.z
}