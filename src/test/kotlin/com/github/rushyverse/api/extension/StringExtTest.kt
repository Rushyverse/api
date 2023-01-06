package com.github.rushyverse.api.extension

import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {

    @Nested
    inner class ToFormattedLore {

        @Test
        fun `should return an empty sequence if the string is empty`() {
            assertEquals(emptyList(), "".toFormattedLore(10))
        }

        @Test
        fun `should cut sentence without space`() {
            val sentence = "0123456789abcdef"
            assertEquals(
                listOf("0123-", "4567-", "89ab-", "cdef"),
                sentence.toFormattedLore(5)
            )
        }

        @Test
        fun `should create only one element if max size is greater than string size`() {
            for (i in 1..100) {
                val string = "a".repeat(i)
                val sequence = string.toFormattedLore(i + 1)
                assert(sequence.count() == 1)
            }
        }

        @Test
        fun `should create only one element if max size is  equals to the string size`() {
            val sentence = "Hello World"
            assertEquals(listOf(sentence), sentence.toFormattedLore(sentence.lastIndex))
        }

        @Test
        fun `should create multiple elements by cut on the space char`() {
            val sequence = "Hello World".toFormattedLore(5)
            assertEquals(listOf("Hello", "World"), sequence)
        }

        @Test
        fun `should create multiple elements by cut on the max size char adding a '-'`() {
            val sequence1 = "Hello World".toFormattedLore(4)
            assertEquals(listOf("Hel-", "lo", "Wor-", "ld"), sequence1)

            val sequence2 = "Hello World".toFormattedLore(3)
            assertEquals(listOf("He-", "llo", "Wo-", "rld"), sequence2)
        }

        @Test
        fun `should create multiple element by cut on the previous space char`() {
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLore(6))
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLore(7))
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLore(8))
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLore(9))
        }

        @Test
        fun `should create multiple element with long sentence`() {
            assertEquals(
                listOf("This is a tool", "to create a", "game"),
                "This is a tool to create a game".toFormattedLore(15)
            )

            assertEquals(
                listOf("This is a tool", "to create a", "game0123456789-", "0123456789"),
                "This is a tool to create a game01234567890123456789".toFormattedLore(15)
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", "vos amis à travers le", "serveur"),
                "Ajoutez, chattez et rejoignez vos amis à travers le serveur".toFormattedLore(30),
            )
        }
    }

    @Nested
    inner class ToFormattedLoreSequence {

        @Test
        fun `should return an empty sequence if the string is empty`() {
            assertEquals(emptySequence(), "".toFormattedLoreSequence(10))
        }

        @Test
        fun `should cut sentence without space`() {
            val sentence = "0123456789abcdef"
            assertEquals(
                listOf("0123-", "4567-", "89ab-", "cdef"),
                sentence.toFormattedLoreSequence(5).toList()
            )
        }

        @Test
        fun `should create only one element if max size is greater than string size`() {
            for (i in 1..100) {
                val string = "a".repeat(i)
                val sequence = string.toFormattedLoreSequence(i + 1)
                assert(sequence.count() == 1)
            }
        }

        @Test
        fun `should create only one element if max size is  equals to the string size`() {
            val sentence = "Hello World"
            assertEquals(listOf(sentence), sentence.toFormattedLoreSequence(sentence.lastIndex).toList())
        }

        @Test
        fun `should create multiple elements by cut on the space char`() {
            val sequence = "Hello World".toFormattedLoreSequence(5).toList()
            assertEquals(listOf("Hello", "World"), sequence)
        }

        @Test
        fun `should create multiple elements by cut on the max size char adding a '-'`() {
            val sequence1 = "Hello World".toFormattedLoreSequence(4).toList()
            assertEquals(listOf("Hel-", "lo", "Wor-", "ld"), sequence1)

            val sequence2 = "Hello World".toFormattedLoreSequence(3).toList()
            assertEquals(listOf("He-", "llo", "Wo-", "rld"), sequence2)
        }

        @Test
        fun `should create multiple element by cut on the previous space char`() {
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLoreSequence(6).toList())
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLoreSequence(7).toList())
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLoreSequence(8).toList())
            assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLoreSequence(9).toList())
        }

        @Test
        fun `should create multiple element with long sentence`() {
            assertEquals(
                listOf("This is a tool", "to create a", "game"),
                "This is a tool to create a game".toFormattedLoreSequence(15).toList()
            )

            assertEquals(
                listOf("This is a tool", "to create a", "game0123456789-", "0123456789"),
                "This is a tool to create a game01234567890123456789".toFormattedLoreSequence(15).toList()
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", "vos amis à travers le", "serveur"),
                "Ajoutez, chattez et rejoignez vos amis à travers le serveur".toFormattedLoreSequence(30).toList(),
            )
        }
    }


}