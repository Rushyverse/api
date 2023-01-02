package com.github.rushyverse.api.extension

import net.minestom.server.coordinate.Pos
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PosExtTest {

    @Nested
    inner class IsInCube {

        @Test
        fun `should return true if the position is in the cube`() {
            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            for (x in 0..10) {
                for (y in 0..10) {
                    for (z in 0..10) {
                        val pos = Pos(x.toDouble(), y.toDouble(), z.toDouble())
                        assertTrue { pos.isInCube(min, max) }
                    }
                }
            }
        }

        @Test
        fun `should return false if the position is not in the cube`() {
            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            assertFalse { Pos(-0.1, 0.0, 0.0).isInCube(min, max) }
            assertFalse { Pos(10.1, 0.0, 0.0).isInCube(min, max) }
            assertFalse { Pos(0.0, -0.1, 0.0).isInCube(min, max) }
            assertFalse { Pos(0.0, 10.1, 0.0).isInCube(min, max) }
            assertFalse { Pos(0.0, 0.0, -0.1).isInCube(min, max) }
            assertFalse { Pos(0.0, 0.0, 10.1).isInCube(min, max) }

            for (x in -10..-1) {
                for (y in -10..-1) {
                    for (z in -10..-1) {
                        val pos = Pos(x.toDouble(), y.toDouble(), z.toDouble())
                        assertFalse { pos.isInCube(min, max) }
                    }
                }
            }

            for (x in 11..20) {
                for (y in 11..20) {
                    for (z in 11..20) {
                        val pos = Pos(x.toDouble(), y.toDouble(), z.toDouble())
                        assertFalse { pos.isInCube(min, max) }
                    }
                }
            }
        }
    }
}