package com.github.rushyverse.api.extension

import com.github.rushyverse.api.time.FormatTime
import com.github.rushyverse.api.time.INFINITE_SYMBOL
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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
    return buildString {
        if (isInfinite()) {
            formatInfiniteTime(format, separator, infiniteSymbol)
        } else {
            formatTime(format, this@format, separator)
        }

        if (endsWith(separator)) {
            deleteLast(separator.length)
        }
    }
}

/**
 * Format a [Duration] to a string.
 *
 * Example:
 * ```kotlin
 * formatTime(..) // 10d 01h 02m 03s
 * ```
 * @receiver The string builder to append the formatted string.
 * @param format The format to use.
 * @param separator Use to separate the hour, minute and second
 * @return Formatted string.
 */
private fun StringBuilder.formatTime(
    format: FormatTime,
    duration: Duration,
    separator: String = " "
) {
    var isFirstUnit = true
    format.getDay(duration)?.let {
        append(it)
        append(separator)
        isFirstUnit = false
    }

    format.getHour(duration, isFirstUnit)?.let {
        append(it)
        append(separator)
        isFirstUnit = false
    }

    format.getMinute(duration, isFirstUnit)?.let {
        append(it)
        append(separator)
        isFirstUnit = false
    }

    format.getSecond(duration, isFirstUnit)?.let {
        append(it)
    }
}

/**
 * Format an infinite time to a string.
 *
 * Example:
 * ```kotlin
 * formatInfiniteTime(..) // ∞d ∞h ∞m ∞s
 * ```
 * @receiver The string builder to append the formatted string.
 * @param format The format to use.
 * @param separator Use to separate the hour, minute and second
 * @param infiniteSymbol Symbol to use when the duration is infinite.
 * @return Formatted string.
 */
private fun StringBuilder.formatInfiniteTime(
    format: FormatTime,
    separator: String = " ",
    infiniteSymbol: String = INFINITE_SYMBOL
) {
    format.day?.let { append(it(infiniteSymbol)).append(separator) }
    format.hour?.let { append(it(infiniteSymbol)).append(separator) }
    format.minute?.let { append(it(infiniteSymbol)).append(separator) }
    format.second?.let { append(it(infiniteSymbol)) }
}
