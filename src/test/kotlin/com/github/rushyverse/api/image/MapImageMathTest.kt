package com.github.rushyverse.api.image

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MapImageMathTest {

    @Nested
    inner class Up {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.Up
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(0f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(270f, instance.pitch)
        }

        @Nested
        inner class ComputeX {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeX(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct x with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeX(begin, 0, itemFramesPerLine))
                assertEquals(1, instance.computeX(begin, 1, itemFramesPerLine))
                assertEquals(2, instance.computeX(begin, 2, itemFramesPerLine))

                assertEquals(0, instance.computeX(begin, 3, itemFramesPerLine))
                assertEquals(1, instance.computeX(begin, 4, itemFramesPerLine))
                assertEquals(2, instance.computeX(begin, 5, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct x with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeX(begin, 0, itemFramesPerLine))
                assertEquals(6, instance.computeX(begin, 1, itemFramesPerLine))
                assertEquals(7, instance.computeX(begin, 2, itemFramesPerLine))

                assertEquals(5, instance.computeX(begin, 3, itemFramesPerLine))
                assertEquals(6, instance.computeX(begin, 4, itemFramesPerLine))
                assertEquals(7, instance.computeX(begin, 5, itemFramesPerLine))
            }

        }

        @Nested
        inner class ComputeY {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertEquals(0, instance.computeY(0, it, itemFramesPerLine = 0))
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should stay on the same y`(beginY: Int) {
                repeat(10) {
                    assertEquals(0, instance.computeY(0, it, 5))
                }
            }

        }

        @Nested
        inner class ComputeZ {

            @Test
            fun `should throws exception with no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeZ(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct z with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeZ(begin, 0, itemFramesPerLine))
                assertEquals(0, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(0, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(1, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(1, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(1, instance.computeZ(begin, 5, itemFramesPerLine))

                assertEquals(2, instance.computeZ(begin, 6, itemFramesPerLine))
                assertEquals(2, instance.computeZ(begin, 7, itemFramesPerLine))
                assertEquals(2, instance.computeZ(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct z with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeZ(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(6, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(6, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(6, instance.computeZ(begin, 5, itemFramesPerLine))

                assertEquals(7, instance.computeZ(begin, 6, itemFramesPerLine))
                assertEquals(7, instance.computeZ(begin, 7, itemFramesPerLine))
                assertEquals(7, instance.computeZ(begin, 8, itemFramesPerLine))
            }

        }
    }
}