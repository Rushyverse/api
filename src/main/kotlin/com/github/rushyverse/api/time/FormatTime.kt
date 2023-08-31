package com.github.rushyverse.api.time

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.extension.HOUR_IN_DAY
import com.github.rushyverse.api.extension.MINUTE_IN_HOUR
import com.github.rushyverse.api.extension.SECOND_IN_MINUTE
import com.github.rushyverse.api.extension.toIntOrString
import com.github.rushyverse.api.translation.Translator
import java.util.*
import kotlin.time.Duration

/**
 * String used to represent an infinite duration.
 */
public const val INFINITE_SYMBOL: String = "∞"

/**
 * Data class that represents a time format.
 * It provides properties and companion functions to create time formats.
 *
 * @property second A function that formats the seconds.
 * @property minute A function that formats the minutes.
 * @property hour A function that formats the hours.
 * @property day A function that formats the days.
 * @property prefixSingleDigitWithZero Whether to prefix single digit time units with a zero.
 * @property acceptZero Whether to accept zero as a value for the time units.
 * If false, the time unit will not be displayed if the value is zero.
 * @property beginAtZero Whether to begin the time format at the first non-zero time unit.
 * @constructor Creates a new instance of FormatTime.
 */
public data class FormatTime(
    val second: FormatPartTime? = null,
    val minute: FormatPartTime? = null,
    val hour: FormatPartTime? = null,
    val day: FormatPartTime? = null,
    val prefixSingleDigitWithZero: Boolean = DEFAULT_PREFIX_SINGLE_DIGIT_WITH_ZERO,
    val acceptZero: Boolean = DEFAULT_ACCEPT_ZERO,
    val beginAtZero: Boolean = DEFAULT_BEGIN_AT_ZERO
) {

    public companion object {

        private const val DEFAULT_PREFIX_SINGLE_DIGIT_WITH_ZERO = true

        private const val DEFAULT_ACCEPT_ZERO = true

        private const val DEFAULT_BEGIN_AT_ZERO = false

        private const val ZERO = "0"

        /**
         * Regex used to get the number in a string.
         * Will create groups named `text1`, `number` and `text2` to get the text before and after the number.
         * The number found has to be a single digit.
         */
        private val patternTranslationWithSingleDigitTime = Regex("^(?<text1>\\D*)(?<number>\\d)(?<text2>\\D*)$")

        /**
         * Constructs a FormatTime object for displaying time in long format.
         * The long format is used for displaying time in a more readable way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @property prefixSingleDigitWithZero Whether to prefix single digit time units with a zero.
         * @property acceptZero Whether to accept zero as a value for the time units.
         * If false, the time unit will not be displayed if the value is zero.
         * @property beginAtZero Whether to begin the time format at the first non-zero time unit.
         * @return A FormatTime object configured for long format.
         */
        public fun long(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
            prefixSingleDigitWithZero: Boolean = DEFAULT_PREFIX_SINGLE_DIGIT_WITH_ZERO,
            acceptZero: Boolean = DEFAULT_ACCEPT_ZERO,
            beginAtZero: Boolean = DEFAULT_BEGIN_AT_ZERO
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.long", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.long", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.long", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.long", locale, arrayOf(it.toIntOrString()), bundle) },
            prefixSingleDigitWithZero = prefixSingleDigitWithZero,
            acceptZero = acceptZero,
            beginAtZero = beginAtZero
        )

        /**
         * Constructs a FormatTime object for displaying time in short format.
         * The short format is used for displaying time in a more compact way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @property prefixSingleDigitWithZero Whether to prefix single digit time units with a zero.
         * @property acceptZero Whether to accept zero as a value for the time units.
         * If false, the time unit will not be displayed if the value is zero.
         * @property beginAtZero Whether to begin the time format at the first non-zero time unit.
         * @return A FormatTime object configured for short format.
         */
        public fun short(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
            prefixSingleDigitWithZero: Boolean = DEFAULT_PREFIX_SINGLE_DIGIT_WITH_ZERO,
            acceptZero: Boolean = DEFAULT_ACCEPT_ZERO,
            beginAtZero: Boolean = DEFAULT_BEGIN_AT_ZERO
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.short", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.short", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.short", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.short", locale, arrayOf(it.toIntOrString()), bundle) },
            prefixSingleDigitWithZero = prefixSingleDigitWithZero,
            acceptZero = acceptZero,
            beginAtZero = beginAtZero
        )
    }

    /**
     * Get the day formatted time for the given duration.
     * @param duration The duration to format.
     * @return The formatted time if [day] is not null, null otherwise.
     */
    public fun getDay(duration: Duration): String? {
        day?.let {
            val value = duration.inWholeDays
            return getTimeOrZeroFormatted(it, value, true)
        }
        return null
    }

    /**
     * Get the hour formatted time for the given duration.
     * @param duration The duration to format.
     * @param isFirstUnit Whether the hour is the first unit to be displayed.
     * @return The formatted time if [hour] is not null, null otherwise.
     */
    public fun getHour(duration: Duration, isFirstUnit: Boolean): String? {
        hour?.let {
            val value = if (isFirstUnit) duration.inWholeHours else duration.inWholeHours % HOUR_IN_DAY
            return getTimeOrZeroFormatted(it, value, isFirstUnit)
        }
        return null
    }

    /**
     * Get the minute formatted time for the given duration.
     * @param duration The duration to format.
     * @param isFirstUnit Whether the minute is the first unit to be displayed.
     * @return The formatted time if [minute] is not null, null otherwise.
     */
    public fun getMinute(duration: Duration, isFirstUnit: Boolean): String? {
        minute?.let {
            val value = if (isFirstUnit) duration.inWholeMinutes else duration.inWholeMinutes % MINUTE_IN_HOUR
            return getTimeOrZeroFormatted(it, value, isFirstUnit)
        }
        return null
    }

    /**
     * Get the second formatted time for the given duration.
     * @param duration The duration to format.
     * @param isFirstUnit Whether the second is the first unit to be displayed.
     * @return The formatted time if [second] is not null, null otherwise.
     */
    public fun getSecond(duration: Duration, isFirstUnit: Boolean): String? {
        second?.let {
            val value = if (isFirstUnit) duration.inWholeSeconds else duration.inWholeSeconds % SECOND_IN_MINUTE
            return getTimeOrZeroFormatted(it, value, isFirstUnit)
        }
        return null
    }

    /**
     * Get the formatted time for the given duration.
     * If [time] is greater than 0, the formatted time will be the formatted [time].
     * If [time] is 0, and it is the first unit to be displayed, the formatted time will be 0.
     * If [time] is 0, and [acceptZero] is true, the formatted time will be 0.
     * @param format Format to use.
     * @param time Time to format.
     * @param isFirstUnit Whether the time is the first unit to be displayed.
     * @return The formatted time if [format] is not null, null otherwise.
     */
    private fun getTimeOrZeroFormatted(format: FormatPartTime, time: Long, isFirstUnit: Boolean): String? {
        return when {
            time > 0 -> time.toString()
            isFirstUnit -> if (beginAtZero && acceptZero) ZERO else null
            acceptZero -> ZERO
            else -> null
        }?.let { adapt(format(it)) }
    }

    /**
     * Adapts a string to the format according to the object configuration.
     * @param string Value to adapt.
     * @return The adapted string, can be the same as the input string.
     */
    private fun adapt(string: String): String {
        return if (prefixSingleDigitWithZero) {
            prefixSingleDigitWithZero(string)
        } else {
            string
        }
    }

    /**
     * Adds a prefix zero for single-digit numbers in a given string.
     *
     * @param string The input string.
     * @return The modified string with prefix zero for single-digit numbers.
     */
    private fun prefixSingleDigitWithZero(string: String): String {
        return string.replace(patternTranslationWithSingleDigitTime) { matchResult ->
            val (text1, number, text2) = matchResult.destructured
            "${text1}0${number}${text2}"
        }
    }
}

/**
 * Type of function used to format a [Duration] part to a string.
 */
public typealias FormatPartTime = (String) -> String
