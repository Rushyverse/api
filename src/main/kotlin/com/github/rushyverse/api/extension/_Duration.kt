package com.github.rushyverse.api.extension

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.translation.Translator
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Data class that represents a time format.
 * It provides properties and companion functions to create time formats.
 * Each function is nullable, if it is null, the time part will not be formatted.
 *
 * @property second A function that formats the seconds.
 * @property minute A function that formats the minutes.
 * @property hour A function that formats the hours.
 * @property day A function that formats the days.
 * @constructor Creates a new instance of FormatTime.
 */
public data class FormatTime(
    val second: FormatPartTime? = null,
    val minute: FormatPartTime? = null,
    val hour: FormatPartTime? = null,
    val day: FormatPartTime? = null,
) {

    public companion object {

        /**
         * Constructs a FormatTime object for displaying time in long format.
         * The long format is used for displaying time in a more readable way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @return A FormatTime object configured for long format.
         */
        public fun long(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.long", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.long", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.long", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.long", locale, arrayOf(it.toIntOrString()), bundle) },
        )

        /**
         * Constructs a FormatTime object for displaying time in short format.
         * The short format is used for displaying time in a more compact way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @return A FormatTime object configured for short format.
         */
        public fun short(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.short", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.short", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.short", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.short", locale, arrayOf(it.toIntOrString()), bundle) },
        )
    }
}

/**
 * Type of function used to format a [Duration] part to a string.
 */
public typealias FormatPartTime = (String) -> String

/**
 * Regex used to get the number in a string.
 * Will create groups named `text1`, `number` and `text2` to get the text before and after the number.
 * The number found has to be a single digit.
 */
private val patternTranslationWithSingleDigitTime = Regex("^(?<text1>\\D*)(?<number>\\d)(?<text2>\\D*)$")

/**
 * String used to represent an infinite duration.
 */
private const val INFINITE_SYMBOL = "∞"

/**
 * Number of milliseconds corresponding to one tick.
 */
public const val MILLISECOND_PER_TICK: Int = 50

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val UInt.ticks: Duration get() = toInt().ticks

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val Int.ticks: Duration get() = (this * MILLISECOND_PER_TICK).milliseconds

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val ULong.ticks: Duration get() = toLong().ticks

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val Long.ticks: Duration get() = (this * MILLISECOND_PER_TICK).milliseconds

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val UShort.ticks: Duration get() = toShort().ticks

/**
 * Get an instance of [Duration] corresponding to the time of ticks.
 * 1 tick corresponding to 50 milliseconds, 20 ticks to 1 second.
 */
public val Short.ticks: Duration get() = (this * MILLISECOND_PER_TICK).milliseconds

/**
 * Format a [Duration] to a string.
 *
 * If the duration is infinite, the [infiniteSymbol] will be used, for example `∞h ∞m ∞s`.
 *
 * If the duration is not infinite, the [formatHour], [formatMinute]
 * and [formatSecond] functions will be used to format.
 *
 * If the duration contains only seconds, the [formatSecond] function will be used to format.
 *
 * If the duration contains at least minute, the [formatMinute] and [formatSecond] functions will be used to format.
 *
 * If the duration contains at least one hour, the [formatHour], [formatMinute]
 * and [formatSecond] functions will be used to format.
 *
 * Example:
 * ```kotlin
 * (1.hours + 2.minutes + 3.seconds).format(..) // 01h 02m 03s
 * (2.minutes + 3.seconds).format(..) // 02m 03s
 * (3.seconds).format(..) // 03s
 * ```
 *
 * @receiver Duration The duration to format.
 * @param format The format to use.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
public fun Duration.format(
    format: FormatTime,
    separator: String = " ",
    infiniteSymbol: String = INFINITE_SYMBOL
): String {
    require(!this.isNegative()) { "Number must be positive" }
    if (isInfinite()) {
        return formatInfiniteTime(format, separator, infiniteSymbol)
    }

    return buildString {
        var hasValue = false

        format.day?.let { formatter ->
            val days = inWholeDays
            if (days > 0) {
                append(prefixSingleDigitWithZero(formatter(days.toString())))
                append(separator)
                hasValue = true
            }
        }

        format.hour?.let { formatter ->
            val hours = if(hasValue) inWholeHours % 24 else inWholeHours
            if (hasValue || hours > 0) {
                append(prefixSingleDigitWithZero(formatter(hours.toString())))
                append(separator)
                hasValue = true
            }
        }

        format.minute?.let { formatter ->
            val minutes = if(hasValue) inWholeMinutes % 60 else inWholeMinutes
            if (hasValue || minutes > 0) {
                append(prefixSingleDigitWithZero(formatter(minutes.toString())))
                append(separator)
                hasValue = true
            }
        }

        format.second?.let { formatter ->
            val seconds = if(hasValue) inWholeSeconds % 60 else inWholeSeconds
            append(prefixSingleDigitWithZero(formatter(seconds.toString())))
        }
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

/**
 * Format an infinite time to a string.
 *
 * Example:
 * ```kotlin
 * formatInfinite(..) // ∞d ∞h ∞m ∞s
 * ```
 * @param formatSecond Function that received [infiniteSymbol] and return second string.
 * @param formatMinute Function that received [infiniteSymbol] and return minute string, if null, minute will not be displayed.
 * @param formatHour Function that received [infiniteSymbol] and return hour string, if null, hour will not be displayed.
 * @param formatDay Function that received [infiniteSymbol] and return day string, if null, day will not be displayed.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
public fun formatInfiniteTime(
    format: FormatTime,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    return buildString {
        format.day?.let { append(it(infiniteSymbol)).append(separator) }
        format.hour?.let { append(it(infiniteSymbol)).append(separator) }
        format.minute?.let { append(it(infiniteSymbol)).append(separator) }
        format.second?.let { append(it(infiniteSymbol)) }
    }
}
