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

    @Nested
    inner class IsInCylinder {

        @Test
        fun `should return true if the position is in the cylinder`() {
            val positionCylinder = Pos(0.0, 0.0, 0.0)
            val radius = 10.0
            val limitY = 0.0..10.0
            for (x in -10..10) {
                for (y in 0..10) {
                    val posX = Pos(x.toDouble(), y.toDouble(), 0.0)
                    assertTrue { posX.isInCylinder(positionCylinder, radius, limitY) }

                    val posZ = Pos(0.0, y.toDouble(), x.toDouble())
                    assertTrue { posZ.isInCylinder(positionCylinder, radius, limitY) }
                }
            }
        }

        @Test
        fun `should return false if the position is not in the cylinder`() {
            val positionCylinder = Pos(0.0, 0.0, 0.0)
            val radius = 10.0
            val limitY = 0.0..10.0
            assertFalse { Pos(-10.1, 0.0, 0.0).isInCylinder(positionCylinder, radius, limitY) }
            assertFalse { Pos(10.1, 0.0, 0.0).isInCylinder(positionCylinder, radius, limitY) }
            assertFalse { Pos(0.0, -0.1, 0.0).isInCylinder(positionCylinder, radius, limitY) }
            assertFalse { Pos(0.0, 10.1, 0.0).isInCylinder(positionCylinder, radius, limitY) }
            assertFalse { Pos(0.0, 0.0, -10.1).isInCylinder(positionCylinder, radius, limitY) }
            assertFalse { Pos(0.0, 0.0, 10.1).isInCylinder(positionCylinder, radius, limitY) }

            for (x in -20..-11) {
                for (y in -10..-1) {
                    val posX = Pos(x.toDouble(), y.toDouble(), 0.0)
                    assertFalse { posX.isInCylinder(positionCylinder, radius, limitY) }

                    val posZ = Pos(0.0, y.toDouble(), x.toDouble())
                    assertFalse { posZ.isInCylinder(positionCylinder, radius, limitY) }
                }
            }

            for (x in 11..20) {
                for (y in 11..20) {
                    val posX = Pos(x.toDouble(), y.toDouble(), 0.0)
                    assertFalse { posX.isInCylinder(positionCylinder, radius, limitY) }

                    val posZ = Pos(0.0, y.toDouble(), x.toDouble())
                    assertFalse { posZ.isInCylinder(positionCylinder, radius, limitY) }
                }
            }
        }
    }
}