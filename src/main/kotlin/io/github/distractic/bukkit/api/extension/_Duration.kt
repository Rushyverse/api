package io.github.distractic.bukkit.api.extension

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