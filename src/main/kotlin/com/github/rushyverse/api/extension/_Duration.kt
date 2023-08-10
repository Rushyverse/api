package com.github.rushyverse.api.extension

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.translation.Translator
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Type of function used to format a [Duration] part to a string.
 */
public typealias FormatPartTime = (String) -> String

/**
 * Regex used to get the number in a string.
 * Will create groups named `text1`, `number` and `text2` to get the text before and after the number.
 * The number found has to be a single digit.
 */
private val numberRegex = Regex("^(?<text1>\\D*)(?<number>\\d)(?<text2>\\D*)$")

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
 * Will use the translation key `time.hour.short`, `time.minute.short` and `time.second.short` to format the duration.
 *
 * Example:
 * ```kotlin
 * (1.hours + 2.minutes + 3.seconds).longFormat(..) // 01hour 02minutes 03seconds
 * (2.minutes + 3.seconds).longFormat(..) // 02minutes 03seconds
 * (3.seconds).longFormat(..) // 03seconds
 * ```
 *
 * @receiver Duration The duration to format.
 * @param translator Translator to use to get the translation.
 * @param locale Locale to use to get the translation.
 * @param bundle Bundle to use to get the translation.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
public fun Duration.longFormat(
    translator: Translator,
    locale: Locale,
    bundle: String = APIPlugin.BUNDLE_API,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    return format(
        formatSecond = { translator.get("time.second.long", locale, arrayOf(it.toIntOrString()), bundle) },
        formatMinute = { translator.get("time.minute.long", locale, arrayOf(it.toIntOrString()), bundle) },
        formatHour = { translator.get("time.hour.long", locale, arrayOf(it.toIntOrString()), bundle) },
        formatDay = { translator.get("time.day.long", locale, arrayOf(it.toIntOrString()), bundle) },
        separator = separator,
        infiniteSymbol = infiniteSymbol
    )
}

/**
 * Format a [Duration] to a string.
 *
 * If the duration is infinite, the [infiniteSymbol] will be used, for example `∞h ∞m ∞s`.
 *
 * Will use the translation key `time.hour.short`, `time.minute.short` and `time.second.short` to format the duration.
 *
 * Example:
 * ```kotlin
 * (1.hours + 2.minutes + 3.seconds).shortFormat(..) // 01h 02m 03s
 * (2.minutes + 3.seconds).shortFormat(..) // 02m 03s
 * (3.seconds).shortFormat(..) // 03s
 * ```
 *
 * @receiver Duration The duration to format.
 * @param translator Translator to use to get the translation.
 * @param locale Locale to use to get the translation.
 * @param bundle Bundle to use to get the translation.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
public fun Duration.shortFormat(
    translator: Translator,
    locale: Locale,
    bundle: String = APIPlugin.BUNDLE_API,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    return format(
        formatSecond = { translator.get("time.second.short", locale, arrayOf(it.toIntOrString()), bundle) },
        formatMinute = { translator.get("time.minute.short", locale, arrayOf(it.toIntOrString()), bundle) },
        formatHour = { translator.get("time.hour.short", locale, arrayOf(it.toIntOrString()), bundle) },
        formatDay = { translator.get("time.day.short", locale, arrayOf(it.toIntOrString()), bundle) },
        separator = separator,
        infiniteSymbol = infiniteSymbol
    )
}

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
 * @param formatDay Function that received [infiniteSymbol] or time string with format `00` and return day string.
 * @param formatHour Function that received [infiniteSymbol] or time string with format `00` and return hour string.
 * @param formatMinute Function that received [infiniteSymbol] or time string with format `00` and return minute string.
 * @param formatSecond Function that received [infiniteSymbol] or time string with format `00` and return second string.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
public fun Duration.format(
    formatSecond: FormatPartTime,
    formatMinute: FormatPartTime,
    formatHour: FormatPartTime,
    formatDay: FormatPartTime,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    require(!this.isNegative()) { "Number must be positive" }
    if (isInfinite()) {
        return formatInfiniteTime(formatSecond, formatMinute, formatHour, formatDay, separator, infiniteSymbol)
    }

    return buildString {
        var hasValue = false

        val days = inWholeDays
        if (days > 0) {
            append(prefixSingleDigitWithZero(formatDay(days.toString())))
            append(separator)
            hasValue = true
        }

        val hours = inWholeHours % 24
        if (hasValue || hours > 0) {
            append(prefixSingleDigitWithZero(formatHour(hours.toString())))
            append(separator)
            hasValue = true
        }

        val minutes = inWholeMinutes % 60
        if (hasValue || minutes > 0) {
            append(prefixSingleDigitWithZero(formatMinute(minutes.toString())))
            append(separator)
        }

        val seconds = inWholeSeconds % 60
        append(prefixSingleDigitWithZero(formatSecond(seconds.toString())))
    }
}

/**
 * Adds a prefix zero for single-digit numbers in a given string.
 *
 * @param string The input string.
 * @return The modified string with prefix zero for single-digit numbers.
 */
private fun prefixSingleDigitWithZero(string: String): String {
    return string.replace(numberRegex) { matchResult ->
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
    formatSecond: FormatPartTime,
    formatMinute: FormatPartTime? = null,
    formatHour: FormatPartTime? = null,
    formatDay: FormatPartTime? = null,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    return buildString {
        formatDay?.let { append(it(infiniteSymbol)).append(separator) }
        formatHour?.let { append(it(infiniteSymbol)).append(separator) }
        formatMinute?.let { append(it(infiniteSymbol)).append(separator) }
        append(formatSecond(infiniteSymbol))
    }
}
