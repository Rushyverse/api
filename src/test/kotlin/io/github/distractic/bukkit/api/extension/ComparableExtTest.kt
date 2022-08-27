package io.github.distractic.bukkit.api.extension

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class ComparableExtTest {

    @Nested
    @DisplayName("Get min and max")
    inner class MinMax {

        @Test
        fun `a is inferior to b`() {
            val expectedA = 1
            val expectedB = 2
            val (a, b) = minMax(expectedA, expectedB)
            assertEquals(expectedA, a)
            assertEquals(expectedB, b)

            val (a2, b2) = minMax(expectedB, expectedA)
            assertEquals(expectedA, a2)
            assertEquals(expectedB, b2)
        }

        @Test
        fun `a is equals to b`() {
            val expectedA = 2
            val expectedB = 2
            val (a, b) = minMax(expectedA, expectedB)
            assertEquals(expectedA, a)
            assertEquals(expectedB, b)

            val (a2, b2) = minMax(expectedB, expectedA)
            assertEquals(expectedA, a2)
            assertEquals(expectedB, b2)
        }

        @Test
        fun `a is superior to b`() {
            val expectedA = 3
            val expectedB = 2
            val (b, a) = minMax(expectedA, expectedB)
            assertEquals(expectedA, a)
            assertEquals(expectedB, b)

            val (b2, a2) = minMax(expectedB, expectedA)
            assertEquals(expectedA, a2)
            assertEquals(expectedB, b2)
        }
    }
}