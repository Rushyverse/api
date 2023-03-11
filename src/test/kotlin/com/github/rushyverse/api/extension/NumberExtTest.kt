package com.github.rushyverse.api.extension

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberExtTest {

    @Nested
    inner class IntTest {

        @Nested
        inner class RomanNumerals {

            @ParameterizedTest
            @ValueSource(ints = [0, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10])
            fun `should throw exception when number is negative or zero`(number: Int) {
                val ex = assertThrows<IllegalArgumentException> { number.toRomanNumerals() }
                assertEquals("Number must be positive", ex.message)
            }

            @ParameterizedTest
            @ValueSource(ints = [4000, 4001, 4002, 4003, 4004, 4005, 4006, 4007, 4008, 4009, 4010])
            fun `should throw exception when number is greater than 3999`(number: Int) {
                val ex = assertThrows<IllegalArgumentException> { number.toRomanNumerals() }
                assertEquals("Number must be less than 4000", ex.message)
            }

            @ParameterizedTest
            @ValueSource(ints = [1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000])
            fun `should return pure roman numerals`(number: Int) {
                val expected = when (number) {
                    1 -> "I"
                    4 -> "IV"
                    5 -> "V"
                    9 -> "IX"
                    10 -> "X"
                    40 -> "XL"
                    50 -> "L"
                    90 -> "XC"
                    100 -> "C"
                    400 -> "CD"
                    500 -> "D"
                    900 -> "CM"
                    1000 -> "M"
                    else -> throw IllegalArgumentException("Invalid number")
                }
                assertEquals(expected, number.toRomanNumerals())
            }

            @Test
            fun `should return complex roman numerals`() {
                NumberExtTest::class.java.getResourceAsStream("/cases/roman/numerals.txt")!!.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val (number, expected) = line.split(" ")
                        assertEquals(expected, number.toInt().toRomanNumerals())
                    }
                }
            }
        }
    }
}