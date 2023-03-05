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
            @ValueSource(ints = [-1, 0, 1])
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

    @Nested
    inner class Down {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.Down
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(0f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(90f, instance.pitch)
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
            @ValueSource(ints = [-1, 0, 1])
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

                assertEquals(-1, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(-1, instance.computeZ(begin, 5, itemFramesPerLine))

                assertEquals(-2, instance.computeZ(begin, 6, itemFramesPerLine))
                assertEquals(-2, instance.computeZ(begin, 7, itemFramesPerLine))
                assertEquals(-2, instance.computeZ(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct z with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeZ(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(4, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(4, instance.computeZ(begin, 5, itemFramesPerLine))

                assertEquals(3, instance.computeZ(begin, 6, itemFramesPerLine))
                assertEquals(3, instance.computeZ(begin, 7, itemFramesPerLine))
                assertEquals(3, instance.computeZ(begin, 8, itemFramesPerLine))
            }

        }

    }

    @Nested
    inner class North {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.North
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(180f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(0f, instance.pitch)
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
                assertEquals(-1, instance.computeX(begin, 1, itemFramesPerLine))
                assertEquals(-2, instance.computeX(begin, 2, itemFramesPerLine))

                assertEquals(0, instance.computeX(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeX(begin, 4, itemFramesPerLine))
                assertEquals(-2, instance.computeX(begin, 5, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct x with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeX(begin, 0, itemFramesPerLine))
                assertEquals(4, instance.computeX(begin, 1, itemFramesPerLine))
                assertEquals(3, instance.computeX(begin, 2, itemFramesPerLine))

                assertEquals(5, instance.computeX(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeX(begin, 4, itemFramesPerLine))
                assertEquals(3, instance.computeX(begin, 5, itemFramesPerLine))
            }

        }

        @Nested
        inner class ComputeY {

            @Test
            fun `should throws exception with no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeY(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct y with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(-1, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(-2, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct y with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(4, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(3, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 8, itemFramesPerLine))
            }
        }

        @Nested
        inner class ComputeZ {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertEquals(0, instance.computeZ(0, it, itemFramesPerLine = 0))
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 0, 1])
            fun `should stay on the same z`(beginZ: Int) {
                repeat(10) {
                    assertEquals(0, instance.computeZ(0, it, 5))
                }
            }

        }

    }

    @Nested
    inner class South {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.South
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(0f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(0f, instance.pitch)
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
            fun `should throws exception with no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeY(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct y with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(-1, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(-2, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct y with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(4, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(3, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 8, itemFramesPerLine))
            }
        }

        @Nested
        inner class ComputeZ {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertEquals(0, instance.computeZ(0, it, itemFramesPerLine = 0))
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 0, 1])
            fun `should stay on the same z`(beginZ: Int) {
                repeat(10) {
                    assertEquals(0, instance.computeZ(0, it, 5))
                }
            }
        }
    }

    @Nested
    inner class West {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.West
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(90f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(0f, instance.pitch)
        }

        @Nested
        inner class ComputeX {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertEquals(0, instance.computeX(0, it, itemFramesPerLine = 0))
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 0, 1])
            fun `should stay on the same x`(beginZ: Int) {
                repeat(10) {
                    assertEquals(0, instance.computeX(0, it, 5))
                }
            }

        }

        @Nested
        inner class ComputeY {

            @Test
            fun `should throws exception with no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeY(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct y with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(-1, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(-2, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct y with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(4, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(3, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 8, itemFramesPerLine))
            }
        }

        @Nested
        inner class ComputeZ {

            @Test
            fun `should always return 0 if no element per line`() {
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
                assertEquals(1, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(2, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(0, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(1, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(2, instance.computeZ(begin, 5, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct z with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeZ(begin, 0, itemFramesPerLine))
                assertEquals(6, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(7, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(5, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(6, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(7, instance.computeZ(begin, 5, itemFramesPerLine))
            }
        }
    }

    @Nested
    inner class East {

        private lateinit var instance: MapImageMath

        @BeforeTest
        fun onBefore() {
            instance = MapImageMath.East
        }

        @Test
        fun `should have the correct yaw`() {
            assertEquals(270f, instance.yaw)
        }

        @Test
        fun `should have the correct pitch`() {
            assertEquals(0f, instance.pitch)
        }

        @Nested
        inner class ComputeX {

            @Test
            fun `should always return 0 if no element per line`() {
                repeat(10) {
                    assertEquals(0, instance.computeX(0, it, itemFramesPerLine = 0))
                }
            }

            @ParameterizedTest
            @ValueSource(ints = [-1, 0, 1])
            fun `should stay on the same x`(beginZ: Int) {
                repeat(10) {
                    assertEquals(0, instance.computeX(0, it, 5))
                }
            }

        }

        @Nested
        inner class ComputeY {

            @Test
            fun `should throws exception with no element per line`() {
                repeat(10) {
                    assertThrows<ArithmeticException> {
                        assertEquals(0, instance.computeY(0, it, itemFramesPerLine = 0))
                    }
                }
            }

            @Test
            fun `should compute the correct y with begin equals to 0`() {
                val begin = 0
                val itemFramesPerLine = 3

                assertEquals(0, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(0, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(-1, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(-1, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(-2, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(-2, instance.computeY(begin, 8, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct y with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeY(begin, 0, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 1, itemFramesPerLine))
                assertEquals(5, instance.computeY(begin, 2, itemFramesPerLine))

                assertEquals(4, instance.computeY(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 4, itemFramesPerLine))
                assertEquals(4, instance.computeY(begin, 5, itemFramesPerLine))

                assertEquals(3, instance.computeY(begin, 6, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 7, itemFramesPerLine))
                assertEquals(3, instance.computeY(begin, 8, itemFramesPerLine))
            }

        }

        @Nested
        inner class ComputeZ {

            @Test
            fun `should always return 0 if no element per line`() {
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
                assertEquals(-1, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(-2, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(0, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(-1, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(-2, instance.computeZ(begin, 5, itemFramesPerLine))
            }

            @Test
            fun `should compute the correct z with begin greater than 0`() {
                val begin = 5
                val itemFramesPerLine = 3

                assertEquals(5, instance.computeZ(begin, 0, itemFramesPerLine))
                assertEquals(4, instance.computeZ(begin, 1, itemFramesPerLine))
                assertEquals(3, instance.computeZ(begin, 2, itemFramesPerLine))

                assertEquals(5, instance.computeZ(begin, 3, itemFramesPerLine))
                assertEquals(4, instance.computeZ(begin, 4, itemFramesPerLine))
                assertEquals(3, instance.computeZ(begin, 5, itemFramesPerLine))
            }
        }
    }
}