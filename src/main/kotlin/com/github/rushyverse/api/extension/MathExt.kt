package com.github.rushyverse.api.extension

/**
 * Get the lowest and highest value in a specific index.
 * The lowest value is placed at the index 0 and highest at the index 1.
 *
 * @param a First value.
 * @param b Second value.
 * @return Both values with a defined order.
 */
public fun <T : Comparable<T>> minMaxOf(a: T, b: T): Pair<T, T> = if (a <= b) {
    a to b
} else {
    b to a
}