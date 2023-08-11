package com.github.rushyverse.api.extension

import com.github.rushyverse.api.time.FormatTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Regex used to get the number in a string.
 * Will create groups named `text1`, `number` and `text2` to get the text before and after the number.
 * The number found has to be a single digit.
 */
private val patternTranslationWithSingleDigitTime = Regex("^(?<text1>\\D*)(?<number>\\d)(?<text2>\\D*)$")

/**
 * String used to represent an infinite duration.
 */
public const val INFINITE_SYMBOL: String = "∞"

/**
 * Number of hours in a day.
 */
public const val HOUR_IN_DAY: Int = 24

/**
 * Number of minutes in an hour.
 */
public const val MINUTE_IN_HOUR: Int = 60

/**
 * Number of seconds in a minute.
 */
public const val SECOND_IN_MINUTE: Int = 60

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
            val hours = if (hasValue) inWholeHours % HOUR_IN_DAY else inWholeHours
            if (hasValue || hours > 0) {
                append(prefixSingleDigitWithZero(formatter(hours.toString())))
                append(separator)
                hasValue = true
            }
        }

        format.minute?.let { formatter ->
            val minutes = if (hasValue) inWholeMinutes % MINUTE_IN_HOUR else inWholeMinutes
            if (hasValue || minutes > 0) {
                append(prefixSingleDigitWithZero(formatter(minutes.toString())))
                append(separator)
                hasValue = true
            }
        }

        val secondFormatter = format.second
        if (secondFormatter != null) {
            val seconds = if (hasValue) inWholeSeconds % SECOND_IN_MINUTE else inWholeSeconds
            append(prefixSingleDigitWithZero(secondFormatter(seconds.toString())))
        } else if (hasValue) {
            deleteLast(separator.length)
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
 * @param format The format to use.
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

        val secondFormatter = format.second
        if (secondFormatter != null) {
            append(secondFormatter(infiniteSymbol))
        } else {
            deleteLast(separator.length)
        }
    }
}
