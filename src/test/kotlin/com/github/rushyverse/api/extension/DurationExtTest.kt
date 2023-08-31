package com.github.rushyverse.api.extension

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.time.FormatTime
import com.github.rushyverse.api.translation.ResourceBundleTranslator
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
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
    inner class LongFormat {

        private lateinit var translator: ResourceBundleTranslator

        @BeforeTest
        fun onBefore() {
            translator = ResourceBundleTranslator(APIPlugin.BUNDLE_API)
            translator.registerResourceBundleForSupportedLocales(APIPlugin.BUNDLE_API, ResourceBundle::getBundle)
        }

        @Nested
        inner class French {

            private val locale = SupportedLanguage.FRENCH.locale

            @ParameterizedTest
            @ValueSource(ints = [0])
            fun `should return the correct format for 0 second`(time: Int) {
                assertEquals(
                    "", time.seconds.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value second`(time: Int) {
                assertEquals("0${time} seconde", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit`(time: Int) {
                assertEquals("0${time} secondes", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit`(time: Int) {
                assertEquals("${time} secondes", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value minute`(time: Int) {
                assertEquals(
                    "0${time} minute 00 seconde", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit minute`(time: Int) {
                assertEquals(
                    "0${time} minutes 00 seconde", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit minute`(time: Int) {
                assertEquals(
                    "$time minutes 00 seconde", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value hour`(time: Int) {
                assertEquals(
                    "0${time} heure 00 minute 00 seconde", time.hours.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit hour`(time: Int) {
                assertEquals(
                    "0${time} heures 00 minute 00 seconde",
                    time.hours.format(FormatTime.long(translator, locale, acceptZero = true))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit hour`(time: Int) {
                assertEquals(
                    "$time heures 00 minute 00 seconde", time.hours.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value day`(time: Int) {
                assertEquals(
                    "0${time} jour 00 heure 00 minute 00 seconde",
                    time.days.format(FormatTime.long(translator, locale, acceptZero = true))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit day`(time: Int) {
                assertEquals(
                    "0${time} jours 00 heure 00 minute 00 seconde",
                    time.days.format(FormatTime.long(translator, locale, acceptZero = true))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit day`(time: Int) {
                assertEquals(
                    "$time jours 00 heure 00 minute 00 seconde",
                    time.days.format(FormatTime.long(translator, locale, acceptZero = true))
                )
            }

            @Test
            fun `should return the correct format for multiple values`() {
                assertEquals(
                    "04 jours 01 heure 02 minutes 03 secondes",
                    (4.days + 1.hours + 2.minutes + 3.seconds).format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

        }

        @Nested
        inner class English {

            private val locale = SupportedLanguage.ENGLISH.locale

            @ParameterizedTest
            @ValueSource(ints = [0])
            fun `should return the correct format for 0 second`(time: Int) {
                assertEquals(
                    "", time.seconds.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value second`(time: Int) {
                assertEquals("0${time} second", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit`(time: Int) {
                assertEquals(
                    "0${time} seconds", time.seconds.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit`(time: Int) {
                assertEquals(
                    "${time} seconds", time.seconds.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value minute`(time: Int) {
                assertEquals(
                    "0${time} minute 00 seconds", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit minute`(time: Int) {
                assertEquals(
                    "0${time} minutes 00 seconds", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit minute`(time: Int) {
                assertEquals(
                    "$time minutes 00 seconds", time.minutes.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value hour`(time: Int) {
                assertEquals(
                    "0${time} hour 00 minutes 00 seconds", time.hours.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit hour`(time: Int) {
                assertEquals(
                    "0${time} hours 00 minutes 00 seconds", time.hours.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit hour`(time: Int) {
                assertEquals(
                    "$time hours 00 minutes 00 seconds", time.hours.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value day`(time: Int) {
                assertEquals(
                    "0${time} day 00 hours 00 minutes 00 seconds", time.days.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit day`(time: Int) {
                assertEquals(
                    "0${time} days 00 hours 00 minutes 00 seconds", time.days.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit day`(time: Int) {
                assertEquals(
                    "$time days 00 hours 00 minutes 00 seconds", time.days.format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }

            @Test
            fun `should return the correct format for multiple values`() {
                assertEquals(
                    "04 days 01 hour 02 minutes 03 seconds",
                    (4.days + 1.hours + 2.minutes + 3.seconds).format(
                        FormatTime.long(translator, locale, acceptZero = true)
                    )
                )
            }
        }
    }

    @Nested
    inner class ShortFormat {

        private lateinit var translator: ResourceBundleTranslator

        @BeforeTest
        fun onBefore() {
            translator = ResourceBundleTranslator(APIPlugin.BUNDLE_API)
            translator.registerResourceBundleForSupportedLocales(APIPlugin.BUNDLE_API, ResourceBundle::getBundle)
        }

        @Test
        fun `should throw an exception if the duration is negative`() {
            assertThrows<IllegalArgumentException> {
                (-1).seconds.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.CHINESE.locale
                    )
                )
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "-"])
        fun `should use separator between the different parts`(separator: String) {
            val time = (4.days + 1.hours + 2.minutes + 3.seconds).format(
                FormatTime.short(
                    translator,
                    SupportedLanguage.ENGLISH.locale
                ),
                separator = separator
            )
            assertEquals(
                "04d${separator}01h${separator}02m${separator}03s",
                time
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["∞", "inf"])
        fun `should return infinity if the duration is infinite`(infinity: String) {
            assertEquals(
                "${infinity}d ${infinity}h ${infinity}m ${infinity}s",
                Duration.INFINITE.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale
                    ),
                    infiniteSymbol = infinity
                )
            )
        }

        @Test
        fun `should return the correct format for 0`() {
            assertEquals(
                "", Duration.ZERO.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale,
                        acceptZero = true
                    )
                )
            )
        }

        @Test
        fun `should return the correct format for 1 minute`() {
            assertEquals(
                "01m 00s", 1.minutes.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale,
                        acceptZero = true
                    )
                )
            )
        }

        @Test
        fun `should return the correct format for 1 hour`() {
            assertEquals(
                "01h 00m 00s", 1.hours.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale,
                        acceptZero = true
                    )
                )
            )
        }

        @Test
        fun `should return the correct format for 1 hour 1 minute`() {
            assertEquals(
                "01h 01m 00s", (1.hours + 1.minutes).format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale,
                        acceptZero = true
                    )
                )
            )
        }

        @Test
        fun `should use selected language`() {
            assertEquals(
                "04天 12小时 38分 01秒", (4.days + 12.hours + 38.minutes + 1.seconds).format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.CHINESE.locale
                    )
                )
            )
        }

    }

    @Nested
    inner class TimeFormat {

        private val format: FormatTime = FormatTime(
            second = { it + "s" },
            minute = { it + "m" },
            hour = { it + "h" },
            day = { it + "d" },
        )

        @Nested
        inner class PrefixSingleDigitWithZero {

            private val localFormat = format.copy(
                beginAtZero = true,
                acceptZero = true,
                prefixSingleDigitWithZero = true
            )

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should prefix single digit for seconds`(time: Int) {
                assertEquals(
                    "00d 00h 00m 0${time}s", time.seconds.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should not prefix double digit for seconds`(time: Int) {
                assertEquals(
                    "00d 00h 00m ${time}s", time.seconds.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should prefix single digit for minutes`(time: Int) {
                assertEquals(
                    "00d 00h 0${time}m 00s", time.minutes.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should not prefix double digit for minutes`(time: Int) {
                assertEquals(
                    "00d 00h ${time}m 00s", time.minutes.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should prefix single digit for hours`(time: Int) {
                assertEquals(
                    "00d 0${time}h 00m 00s", time.hours.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should not prefix double digit for hours`(time: Int) {
                assertEquals(
                    "00d ${time}h 00m 00s", time.hours.format(
                        localFormat
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should prefix single digit for days`(time: Int) {
                assertEquals(
                    "0${time}d 00h 00m 00s", time.days.format(
                        localFormat
                    )
                )
            }

            @Test
            fun `should prefix for all time`() {
                assertEquals(
                    "01d 01h 01m 01s", (1.days + 1.hours + 1.minutes + 1.seconds).format(
                        localFormat
                    )
                )
            }

        }

        @Nested
        inner class AcceptZero {

            private val localFormat = format.copy(
                beginAtZero = false,
                acceptZero = true,
                prefixSingleDigitWithZero = false
            )

            @Test
            fun `should display all time`() {
                assertEquals(
                    "", Duration.ZERO.format(
                        localFormat
                    )
                )
            }

            @Test
            fun `should not display day if there is only hour`() {
                assertEquals(
                    "1h 0m 0s", 1.hours.format(
                        localFormat
                    )
                )
            }

            @Test
            fun `should not display 0 value`() {
                assertEquals(
                    "1h 1s", (1.hours + 1.seconds).format(
                        localFormat.copy(
                            beginAtZero = false,
                            acceptZero = false
                        )
                    )
                )
            }

            @Test
            fun `should not display day if day format is null`() {
                assertEquals(
                    "24h 0m 0s", 1.days.format(
                        localFormat.copy(
                            day = null
                        )
                    )
                )
            }

        }

        @Nested
        inner class BeginAtZero {

            private val localFormat = format.copy(
                beginAtZero = true,
                acceptZero = true,
                prefixSingleDigitWithZero = false
            )

            @Test
            fun `should display all time`() {
                assertEquals(
                    "0d 0h 0m 0s", Duration.ZERO.format(
                        localFormat
                    )
                )
            }

            @Test
            fun `should not display 0 at begin if zero not accepted`() {
                assertEquals(
                    "1h", 1.hours.format(
                        localFormat.copy(
                            acceptZero = false
                        )
                    )
                )
            }

            @Test
            fun `should display 0 day if there is only hour`() {
                assertEquals(
                    "0d 1h 0m 0s", 1.hours.format(
                        localFormat
                    )
                )
            }

            @Test
            fun `should not display day if format is null`() {
                assertEquals(
                    "0h 0m 0s", Duration.ZERO.format(
                        localFormat.copy(
                            day = null
                        )
                    )
                )
            }

            @Test
            fun `should not display hour if format is null`() {
                assertEquals(
                    "0d 0m 0s", Duration.ZERO.format(
                        localFormat.copy(
                            hour = null
                        )
                    )
                )
            }

            @Test
            fun `should not display minute if format is null`() {
                assertEquals(
                    "0d 0h 0s", Duration.ZERO.format(
                        localFormat.copy(
                            minute = null
                        )
                    )
                )
            }

            @Test
            fun `should not display second if format is null`() {
                assertEquals(
                    "0d 0h 0m", Duration.ZERO.format(
                        localFormat.copy(
                            second = null
                        )
                    )
                )
            }

        }

    }

    @Nested
    inner class FormatInfiniteTime {

        private val format: FormatTime = FormatTime(
            second = { it + "s" },
            minute = { it + "m" },
            hour = { it + "h" },
            day = { it + "d" },
        )

        @Test
        fun `should display all time`() {
            assertEquals(
                "∞d ∞h ∞m ∞s", Duration.INFINITE.format(
                    format
                )
            )
        }

        @Test
        fun `should display seconds, minute and hour`() {
            assertEquals(
                "∞h ∞m ∞s", Duration.INFINITE.format(
                    format.copy(day = null)
                )
            )
        }

        @Test
        fun `should display seconds, minute`() {
            assertEquals(
                "∞m ∞s", Duration.INFINITE.format(
                    format.copy(day = null, hour = null)
                )
            )
        }

        @Test
        fun `should display seconds, hour`() {
            assertEquals(
                "∞h ∞s", Duration.INFINITE.format(
                    format.copy(day = null, minute = null)
                )
            )
        }

        @Test
        fun `should display seconds, day`() {
            assertEquals(
                "∞d ∞s", Duration.INFINITE.format(
                    format.copy(hour = null, minute = null)
                )
            )
        }

        @Test
        fun `should display seconds`() {
            assertEquals(
                "∞s", Duration.INFINITE.format(
                    format.copy(hour = null, minute = null, day = null)
                )
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["∞", "inf"])
        fun `should use separator`(infinity: String) {
            assertEquals(
                "${infinity}d ${infinity}h ${infinity}m ${infinity}s",
                Duration.INFINITE.format(
                    format,
                    infiniteSymbol = infinity
                )
            )
        }
    }
}
