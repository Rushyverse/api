package com.github.rushyverse.api.extension

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationExtTest {

    @Nested
    @DisplayName("Int conversion")
    inner class IntConversion {

        @Test
        fun `positive returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0.ticks)
            assertEquals(50.milliseconds, 1.ticks)
            assertEquals(1.seconds, 20.ticks)
            assertEquals(2.seconds, 40.ticks)
            assertEquals(5.seconds, 100.ticks)
            assertEquals(250.milliseconds, 5.ticks)
        }

        @Test
        fun `negative returns the corresponding ticks`() {
            assertEquals((-50).milliseconds, (-1).ticks)
            assertEquals((-1).seconds, (-20).ticks)
            assertEquals((-2).seconds, (-40).ticks)
            assertEquals((-5).seconds, (-100).ticks)
            assertEquals((-250).milliseconds, (-5).ticks)
        }

        @Test
        fun `uInt returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0u.ticks)
            assertEquals(50.milliseconds, 1u.ticks)
            assertEquals(1.seconds, 20u.ticks)
            assertEquals(2.seconds, 40u.ticks)
            assertEquals(5.seconds, 100u.ticks)
            assertEquals(250.milliseconds, 5u.ticks)
        }
    }

    @Nested
    @DisplayName("Short conversion")
    inner class ShortConversion {

        @Test
        fun `positive returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0.toShort().ticks)
            assertEquals(50.milliseconds, 1.toShort().ticks)
            assertEquals(1.seconds, 20.toShort().ticks)
            assertEquals(2.seconds, 40.toShort().ticks)
            assertEquals(5.seconds, 100.toShort().ticks)
            assertEquals(250.milliseconds, 5.toShort().ticks)
        }

        @Test
        fun `negative returns the corresponding ticks`() {
            assertEquals((-50).milliseconds, (-1).toShort().ticks)
            assertEquals((-1).seconds, (-20).toShort().ticks)
            assertEquals((-2).seconds, (-40).toShort().ticks)
            assertEquals((-5).seconds, (-100).toShort().ticks)
            assertEquals((-250).milliseconds, (-5).toShort().ticks)
        }

        @Test
        fun `uShort returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0.toUShort().ticks)
            assertEquals(50.milliseconds, 1.toUShort().ticks)
            assertEquals(1.seconds, 20.toUShort().ticks)
            assertEquals(2.seconds, 40.toUShort().ticks)
            assertEquals(5.seconds, 100.toUShort().ticks)
            assertEquals(250.milliseconds, 5.toUShort().ticks)
        }
    }

    @Nested
    @DisplayName("Long conversion")
    inner class LongConversion {

        @Test
        fun `positive returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0.toLong().ticks)
            assertEquals(50.milliseconds, 1.toLong().ticks)
            assertEquals(1.seconds, 20.toLong().ticks)
            assertEquals(2.seconds, 40.toLong().ticks)
            assertEquals(5.seconds, 100.toLong().ticks)
            assertEquals(250.milliseconds, 5.toLong().ticks)
        }

        @Test
        fun `negative returns the corresponding ticks`() {
            assertEquals((-50).milliseconds, (-1).toLong().ticks)
            assertEquals((-1).seconds, (-20).toLong().ticks)
            assertEquals((-2).seconds, (-40).toLong().ticks)
            assertEquals((-5).seconds, (-100).toLong().ticks)
            assertEquals((-250).milliseconds, (-5).toLong().ticks)
        }

        @Test
        fun `uLong returns the corresponding ticks`() {
            assertEquals(Duration.ZERO, 0.toULong().ticks)
            assertEquals(50.milliseconds, 1.toULong().ticks)
            assertEquals(1.seconds, 20.toULong().ticks)
            assertEquals(2.seconds, 40.toULong().ticks)
            assertEquals(5.seconds, 100.toULong().ticks)
            assertEquals(250.milliseconds, 5.toULong().ticks)
        }
    }

    @Nested
    inner class TimeFormat {

        private val formatHour: (String) -> String = { it + "h" }
        private val formatMinute: (String) -> String = { it + "m" }
        private val formatSecond: (String) -> String = { it + "s" }

        @Test
        fun `should throw an exception if the duration is negative`() {
            assertThrows<IllegalArgumentException> {
                (-1).seconds.format(
                    formatHour,
                    formatMinute,
                    formatSecond
                )
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "-"])
        fun `should use separator between the different parts`(separator: String) {
            val time = (1.hours + 2.minutes + 3.seconds).format(
                formatHour,
                formatMinute,
                formatSecond,
                separator = separator
            )
            assertEquals(
                "01h${separator}02m${separator}03s",
                time
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["∞", "inf"])
        fun `should return infinity if the duration is infinite`(infinity: String) {
            assertEquals(
                "${infinity}h ${infinity}m ${infinity}s",
                Duration.INFINITE.format(formatHour, formatMinute, formatSecond, infiniteSymbol = infinity))
        }

        @Test
        fun `should return the correct format for 0`() {
            assertEquals("00s", Duration.ZERO.format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 second`() {
            assertEquals("01s", 1.seconds.format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 minute`() {
            assertEquals("01m 00s", 1.minutes.format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 hour`() {
            assertEquals("01h 00m 00s", 1.hours.format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 hour 1 minute`() {
            assertEquals("01h 01m 00s", (1.hours + 1.minutes).format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 hour 1 second`() {
            assertEquals("01h 00m 01s", (1.hours + 1.seconds).format(formatHour, formatMinute, formatSecond))
        }

        @Test
        fun `should return the correct format for 1 minute 1 second`() {
            assertEquals("01m 01s", (1.minutes + 1.seconds).format(formatHour, formatMinute, formatSecond))
        }

    }
}
