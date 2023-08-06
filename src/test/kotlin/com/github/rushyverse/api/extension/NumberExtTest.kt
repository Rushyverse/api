package com.github.rushyverse.api.extension

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test

class NumberExtTest {

    @Test
    fun `roman numerals values associate all numbers to their roman numerals`() {
        val expected = mapOf(
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
        ROMAN_NUMERALS_VALUES shouldBe expected
    }

    @Test
    fun `roman numerals array should be ordered from the largest to the smallest`() {
        val expected = listOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        ROMAN_NUMERALS.toList() shouldBe expected
    }

    @Test
    fun `roman values array should be ordered from the largest to the smallest`() {
        val expected = listOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        ROMAN_VALUES.toList() shouldBe expected
    }

    @Nested
    inner class IntTest {

        @Nested
        inner class RomanNumerals {

            @ParameterizedTest
            @ValueSource(ints = [0, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10])
            fun `should throw exception when number is negative or zero`(number: Int) {
                val ex = assertThrows<IllegalArgumentException> { number.toRomanNumerals() }
                ex.message shouldBe "Number must be positive"
            }

            @ParameterizedTest
            @ValueSource(ints = [4000, 4001, 4002, 4003, 4004, 4005, 4006, 4007, 4008, 4009, 4010])
            fun `should throw exception when number is greater than 3999`(number: Int) {
                val ex = assertThrows<IllegalArgumentException> { number.toRomanNumerals() }
                ex.message shouldBe "Number must be less than 4000"
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
                number.toRomanNumerals() shouldBe expected
            }

            @Test
            fun `should return complex roman numerals`() {
                NumberExtTest::class.java.getResourceAsStream("/cases/roman/numerals.txt")!!.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val (number, expected) = line.split(" ")
                        number.toInt().toRomanNumerals() shouldBe expected
                    }
                }
            }
        }
    }
}
