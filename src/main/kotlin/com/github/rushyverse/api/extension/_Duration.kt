package com.github.rushyverse.api.extension

import com.github.rushyverse.api.time.FormatPartTime
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
        var hasValue = format.day?.let {
            appendDayTime(it, separator)
        } ?: false

        hasValue = hasValue or (format.hour?.let {
            appendHourTime(it, hasValue, separator)
        } ?: false)

        hasValue = hasValue or (format.minute?.let {
            appendMinuteTime(it, hasValue, separator)
        } ?: false)

        val secondFormat = format.second
        if (secondFormat != null) {
            appendSecondTime(secondFormat, hasValue)
        } else if (hasValue) {
            /**
             * If there is another formatter (minute, hour, etc.) applied and there is no second formatter, we need
             * to remove the last separator added because the other formatter doesn't know if there is a next formatter
             * or not.
             */
            deleteLast(separator.length)
        }
    }
}

/**
 * Appends the day time to a StringBuilder object.
 *
 * @param format The function to format the day to a string representation.
 * @param separator The separator to append between two time values.
 *
 * @receiver The Duration value representing the time.
 * @receiver The StringBuilder to append the day to.
 */
context(Duration, StringBuilder)
private inline fun appendDayTime(format: FormatPartTime, separator: String): Boolean {
    val days = inWholeDays
    if (days > 0) {
        append(prefixSingleDigitWithZero(format(days.toString())))
        append(separator)
        return true
    }
    return false
}

/**
 * Appends the hour time to a StringBuilder object.
 *
 * @param format The function to format the hour to a string representation.
 * @param hasValue `true` if another time (day, etc.) has been appended to the StringBuilder before,
 * `false` otherwise.
 * @param separator The separator to append between two time values.
 *
 * @receiver The Duration value representing the time.
 * @receiver The StringBuilder to append the hour to.
 */
context(Duration, StringBuilder)
private inline fun appendHourTime(format: FormatPartTime, hasValue: Boolean, separator: String): Boolean {
    val hours = if (hasValue) inWholeHours % HOUR_IN_DAY else inWholeHours
    if (hasValue || hours > 0) {
        append(prefixSingleDigitWithZero(format(hours.toString())))
        append(separator)
        return true
    }
    return false
}

/**
 * Appends the minute time to a StringBuilder object.
 *
 * @param format The function to format the minute to a string representation.
 * @param hasValue `true` if another time (hour, etc.) has been appended to the StringBuilder before,
 * `false` otherwise.
 * @param separator The separator to append between two time values.
 *
 * @receiver The Duration value representing the time.
 * @receiver The StringBuilder to append the minute to.
 */
context(Duration, StringBuilder)
private inline fun appendMinuteTime(format: FormatPartTime, hasValue: Boolean, separator: String): Boolean {
    val minutes = if (hasValue) inWholeMinutes % MINUTE_IN_HOUR else inWholeMinutes
    if (hasValue || minutes > 0) {
        append(prefixSingleDigitWithZero(format(minutes.toString())))
        append(separator)
        return true
    }
    return false
}

/**
 * Appends the second time to a StringBuilder object.
 *
 * @param format The function to format the seconds to a string representation.
 * @param hasValue `true` if another time (minute, hour, etc.) has been appended to the StringBuilder before,
 * `false` otherwise.
 *
 * @receiver The Duration value representing the time.
 * @receiver The StringBuilder to append the seconds to.
 */
context(Duration, StringBuilder)
private inline fun appendSecondTime(format: FormatPartTime, hasValue: Boolean) {
    val seconds = if (hasValue) inWholeSeconds % SECOND_IN_MINUTE else inWholeSeconds
    append(prefixSingleDigitWithZero(format(seconds.toString())))
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
