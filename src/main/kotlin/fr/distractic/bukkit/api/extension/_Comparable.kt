package fr.distractic.bukkit.api.extension

/**
 * Get the lowest and highest value in a specific index.
 * The lowest value is placed at the index 0 and highest at the index 1.
 *
 * @param a First value.
 * @param b Second value.
 * @return Both values with a defined order.
 */
public fun <T> minMax(a: T, b: T): Pair<T, T> where T : Comparable<T> = if (a <= b) {
    a to b
} else {
    b to a
}