package com.github.rushyverse.api.utils

import org.bukkit.Location
import kotlin.test.assertEquals

fun assertEqualsLocation(loc1: Location, loc2: Location) {
    with(loc1) {
        assertEquals(world, loc2.world)
        assertEquals(x, loc2.x)
        assertEquals(y, loc2.y)
        assertEquals(z, loc2.z)
        assertEquals(yaw, loc2.yaw)
        assertEquals(pitch, loc2.pitch)
    }
}