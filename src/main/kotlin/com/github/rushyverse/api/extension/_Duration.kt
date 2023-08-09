package com.github.rushyverse.api.extension

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.translation.Translator
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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

public fun Duration.format(
    translator: Translator,
    locale: Locale,
    bundle: String = APIPlugin.BUNDLE_API,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    return format(
        formatHour = { translator.get("time.hour.short", locale, arrayOf(it), bundle) },
        formatMinute = { translator.get("time.minute.short", locale, arrayOf(it), bundle) },
        formatSecond = { translator.get("time.second.short", locale, arrayOf(it), bundle) },
        separator = separator,
        infiniteSymbol = infiniteSymbol
    )
}

public inline fun Duration.format(
    formatHour: (String) -> String,
    formatMinute: (String) -> String,
    formatSecond: (String) -> String,
    separator: String = " ",
    infiniteSymbol: String = "∞"
): String {
    require(!this.isNegative()) { "Number must be positive" }

    val hours = this.inWholeHours
    val minutes = this.inWholeMinutes % 60
    val seconds = this.inWholeSeconds % 60

    val hoursString: String
    val minutesString: String
    val secondsString: String

    if(isInfinite()) {
        hoursString = infiniteSymbol
        minutesString = infiniteSymbol
        secondsString = infiniteSymbol
    } else {
        // TODO Change format in translation file
        hoursString = String.format("%02d", hours)
        minutesString = String.format("%02d", minutes)
        secondsString = String.format("%02d", seconds)
    }

    return buildString {
        if (hours > 0) {
            append(formatHour(hoursString))
            append(separator)
        }

        if (hours > 0 || minutes > 0) {
            append(formatMinute(minutesString))
            append(separator)
        }

        append(formatSecond(secondsString))
    }
}
