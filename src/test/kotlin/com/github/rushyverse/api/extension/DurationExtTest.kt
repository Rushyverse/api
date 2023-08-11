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
            @ValueSource(ints = [0, 1])
            fun `should return the correct format for singular value second`(time: Int) {
                assertEquals("0${time}seconde", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit`(time: Int) {
                assertEquals("0${time}secondes", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit`(time: Int) {
                assertEquals("${time}secondes", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value minute`(time: Int) {
                assertEquals("0${time}minute 00seconde", time.minutes.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit minute`(time: Int) {
                assertEquals("0${time}minutes 00seconde", time.minutes.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit minute`(time: Int) {
                assertEquals("${time}minutes 00seconde", time.minutes.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value hour`(time: Int) {
                assertEquals("0${time}heure 00minute 00seconde", time.hours.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit hour`(time: Int) {
                assertEquals(
                    "0${time}heures 00minute 00seconde",
                    time.hours.format(FormatTime.long(translator, locale))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit hour`(time: Int) {
                assertEquals("${time}heures 00minute 00seconde", time.hours.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value day`(time: Int) {
                assertEquals(
                    "0${time}jour 00heure 00minute 00seconde",
                    time.days.format(FormatTime.long(translator, locale))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit day`(time: Int) {
                assertEquals(
                    "0${time}jours 00heure 00minute 00seconde",
                    time.days.format(FormatTime.long(translator, locale))
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit day`(time: Int) {
                assertEquals(
                    "${time}jours 00heure 00minute 00seconde",
                    time.days.format(FormatTime.long(translator, locale))
                )
            }

            @Test
            fun `should return the correct format for multiple values`() {
                assertEquals(
                    "04jours 01heure 02minutes 03secondes",
                    (4.days + 1.hours + 2.minutes + 3.seconds).format(FormatTime.long(translator, locale))
                )
            }

        }

        @Nested
        inner class English {

            private val locale = SupportedLanguage.ENGLISH.locale

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value second`(time: Int) {
                assertEquals("0${time}second", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [0, 2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit`(time: Int) {
                assertEquals("0${time}seconds", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit`(time: Int) {
                assertEquals("${time}seconds", time.seconds.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value minute`(time: Int) {
                assertEquals("0${time}minute 00seconds", time.minutes.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit minute`(time: Int) {
                assertEquals("0${time}minutes 00seconds", time.minutes.format(FormatTime.long(translator, locale)))
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit minute`(time: Int) {
                assertEquals(
                    "${time}minutes 00seconds", time.minutes.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value hour`(time: Int) {
                assertEquals(
                    "0${time}hour 00minutes 00seconds", time.hours.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit hour`(time: Int) {
                assertEquals(
                    "0${time}hours 00minutes 00seconds", time.hours.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit hour`(time: Int) {
                assertEquals(
                    "${time}hours 00minutes 00seconds", time.hours.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [1])
            fun `should return the correct format for singular value day`(time: Int) {
                assertEquals(
                    "0${time}day 00hours 00minutes 00seconds", time.days.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [2, 3, 4, 5, 6, 7, 8, 9])
            fun `should return the correct format for plural value with single digit day`(time: Int) {
                assertEquals(
                    "0${time}days 00hours 00minutes 00seconds", time.days.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @ParameterizedTest
            @ValueSource(ints = [10, 11, 12, 13, 14, 15, 16, 17, 18, 19])
            fun `should return the correct format for plural value with double digit day`(time: Int) {
                assertEquals(
                    "${time}days 00hours 00minutes 00seconds", time.days.format(
                        FormatTime.long(translator, locale)
                    )
                )
            }

            @Test
            fun `should return the correct format for multiple values`() {
                assertEquals(
                    "04days 01hour 02minutes 03seconds",
                    (4.days + 1.hours + 2.minutes + 3.seconds).format(FormatTime.long(translator, locale))
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
                "00s", Duration.ZERO.format(
                    FormatTime.short(
                        translator,
                        SupportedLanguage.ENGLISH.locale
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
                        SupportedLanguage.ENGLISH.locale
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
                        SupportedLanguage.ENGLISH.locale
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
                        SupportedLanguage.ENGLISH.locale
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

        @Test
        fun `should return empty string if format does not contain any part`() {
            val nullFormat = FormatTime(
                second = null,
                minute = null,
                hour = null,
                day = null,
            )

            assertEquals(
                "",
                (10.days + 1.hours + 2.minutes + 3.seconds).format(nullFormat)
            )
            assertEquals(
                "",
                Duration.INFINITE.format(nullFormat)
            )
        }

        @Test
        fun `should only display day if other is null`() {
            val nullFormat = FormatTime(
                second = null,
                minute = null,
                hour = null,
                day = { it + "d" },
            )

            assertEquals(
                "10d",
                (10.days).format(nullFormat)
            )
            assertEquals(
                "∞d",
                Duration.INFINITE.format(nullFormat)
            )
        }

        @Test
        fun `should only display hour if other is null`() {
            val nullFormat = FormatTime(
                second = null,
                minute = null,
                hour = { it + "h" },
                day = null,
            )

            assertEquals(
                "10h",
                (10.hours).format(nullFormat)
            )
            assertEquals(
                "∞h",
                Duration.INFINITE.format(nullFormat)
            )
        }

        @Test
        fun `should only display minute if other is null`() {
            val nullFormat = FormatTime(
                second = null,
                minute = { it + "m" },
                hour = null,
                day = null,
            )

            assertEquals(
                "10m",
                (10.minutes).format(nullFormat)
            )
            assertEquals(
                "∞m",
                Duration.INFINITE.format(nullFormat)
            )
        }

        @Test
        fun `should only display second if other is null`() {
            val nullFormat = FormatTime(
                second = { it + "s" },
                minute = null,
                hour = null,
                day = null,
            )

            assertEquals(
                "10s",
                (10.seconds).format(nullFormat)
            )
            assertEquals(
                "∞s",
                Duration.INFINITE.format(nullFormat)
            )
        }

        @Test
        fun `should throw an exception if the duration is negative`() {
            assertThrows<IllegalArgumentException> {
                (-1).seconds.format(
                    format
                )
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "-"])
        fun `should use separator between the different parts`(separator: String) {
            val time = (1.hours + 2.minutes + 3.seconds).format(
                format,
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
                "${infinity}d ${infinity}h ${infinity}m ${infinity}s",
                Duration.INFINITE.format(
                    format,
                    infiniteSymbol = infinity
                )
            )
        }

        @Test
        fun `should return the correct format for 0`() {
            assertEquals(
                "00s", Duration.ZERO.format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 second`() {
            assertEquals(
                "01s", 1.seconds.format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 minute`() {
            assertEquals(
                "01m 00s", 1.minutes.format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 hour`() {
            assertEquals(
                "01h 00m 00s", 1.hours.format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 hour 1 minute`() {
            assertEquals(
                "01h 01m 00s", (1.hours + 1.minutes).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 hour 1 second`() {
            assertEquals(
                "01h 00m 01s", (1.hours + 1.seconds).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 minute 1 second`() {
            assertEquals(
                "01m 01s", (1.minutes + 1.seconds).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day`() {
            assertEquals(
                "01d 00h 00m 00s", 1.days.format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day 1 hour`() {
            assertEquals(
                "01d 01h 00m 00s", (1.days + 1.hours).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day 1 minute`() {
            assertEquals(
                "01d 00h 01m 00s", (1.days + 1.minutes).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day 1 second`() {
            assertEquals(
                "01d 00h 00m 01s", (1.days + 1.seconds).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day 1 hour 1 minute`() {
            assertEquals(
                "01d 01h 01m 00s", (1.days + 1.hours + 1.minutes).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for 1 day 1 hour 1 second`() {
            assertEquals(
                "01d 01h 00m 01s", (1.days + 1.hours + 1.seconds).format(
                    format
                )
            )
        }

        @Test
        fun `should return the correct format for more one month`() {
            assertEquals(
                "69d 00h 00m 00s", 69.days.format(
                    format
                )
            )
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
                "∞d ∞h ∞m ∞s", formatInfiniteTime(
                    format
                )
            )
        }

        @Test
        fun `should display seconds, minute and hour`() {
            assertEquals(
                "∞h ∞m ∞s", formatInfiniteTime(
                    format.copy(day = null)
                )
            )
        }

        @Test
        fun `should display seconds, minute`() {
            assertEquals(
                "∞m ∞s", formatInfiniteTime(
                    format.copy(day = null, hour = null)
                )
            )
        }

        @Test
        fun `should display seconds, hour`() {
            assertEquals(
                "∞h ∞s", formatInfiniteTime(
                    format.copy(day = null, minute = null)
                )
            )
        }

        @Test
        fun `should display seconds, day`() {
            assertEquals(
                "∞d ∞s", formatInfiniteTime(
                    format.copy(hour = null, minute = null)
                )
            )
        }

        @Test
        fun `should display seconds`() {
            assertEquals(
                "∞s", formatInfiniteTime(
                    format.copy(hour = null, minute = null, day = null)
                )
            )
        }

        @ParameterizedTest
        @ValueSource(strings = ["∞", "inf"])
        fun `should use separator`(infinity: String) {
            assertEquals(
                "${infinity}d ${infinity}h ${infinity}m ${infinity}s",
                formatInfiniteTime(
                    format,
                    infiniteSymbol = infinity
                )
            )
        }


    }
}
