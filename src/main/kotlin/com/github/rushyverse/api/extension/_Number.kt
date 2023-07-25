package com.github.rushyverse.api.extension

/**
 * The values of roman number linked to their roman numerals.
 * The values are ordered from the largest to the smallest.
 */
public val ROMAN_NUMERALS_VALUES: Map<Int, String> = mapOf(
    1000 to "M",
    900 to "CM",
    500 to "D",
    400 to "CD",
    100 to "C",
    90 to "XC",
    50 to "L",
    40 to "XL",
    10 to "X",
    9 to "IX",
    5 to "V",
    4 to "IV",
    1 to "I"
)

/**
 * The roman numerals.
 * The numerals are ordered from the largest to the smallest.
 * @see ROMAN_VALUES
 */
public val ROMAN_NUMERALS: Array<String> = ROMAN_NUMERALS_VALUES.values.toTypedArray()

/**
 * The values of roman number.
 * The values are ordered from the largest to the smallest.
 * @see ROMAN_NUMERALS
 */
public val ROMAN_VALUES: IntArray = ROMAN_NUMERALS_VALUES.keys.toIntArray()

/**
 * Convert a number to roman numerals.
 * @receiver Int between 1 and 3999.
 * @return A string of roman numerals.
 */
public fun Int.toRomanNumerals(): String {
    require(this > 0) { "Number must be positive" }
    require(this < 4000) { "Number must be less than 4000" }

    var remaining = this
    var i = 0
    return buildString {
        while(remaining > 0) {
            val romanValue = ROMAN_VALUES[i]
            repeat(remaining / romanValue) {
                append(ROMAN_NUMERALS[i])
                remaining -= romanValue
            }
            i++
        }
    }
}
