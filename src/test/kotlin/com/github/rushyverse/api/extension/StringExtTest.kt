package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtTest {

    @Test
    fun `default lore line length`() {
        assertEquals(30, DEFAULT_LORE_LINE_LENGTH)
    }

    @Nested
    inner class SequenceToLore {

        @Test
        fun `should return empty component if sequence is empty`() {
            assertEquals(emptyList(), emptySequence<String>().toLore())
        }

        @Test
        fun `should set color gray by default`() {
            val components = sequenceOf("a", "b", "c").toLore()
            assertEquals(
                listOf(
                    Component.text().content("a").color(NamedTextColor.GRAY).build(),
                    Component.text().content("b").color(NamedTextColor.GRAY).build(),
                    Component.text().content("c").color(NamedTextColor.GRAY).build()
                ),
                components
            )
        }

        @Test
        fun `should return component with all strings`() {
            val components = sequenceOf("Hello", "World").toLore() {}
            assertEquals(
                listOf(
                    Component.text().content("Hello").build(),
                    Component.text().content("World").build()
                ),
                components
            )
        }

        @Test
        fun `should return component with all strings and transform`() {
            val components = sequenceOf("Hello", "World").toLore { color(NamedTextColor.RED) }
            assertEquals(
                listOf(
                    Component.text().content("Hello").color(NamedTextColor.RED).build(),
                    Component.text().content("World").color(NamedTextColor.RED).build()
                ),
                components
            )
        }

    }

    @Nested
    inner class CollectionToLore {

        @Test
        fun `should return empty component if sequence is empty`() {
            assertEquals(emptyList(), emptyList<String>().toLore())
        }

        @Test
        fun `should set color gray by default`() {
            val components = listOf("a", "b", "c").toLore()
            assertEquals(
                listOf(
                    Component.text().content("a").color(NamedTextColor.GRAY).build(),
                    Component.text().content("b").color(NamedTextColor.GRAY).build(),
                    Component.text().content("c").color(NamedTextColor.GRAY).build()
                ),
                components
            )
        }

        @Test
        fun `should return component with all strings`() {
            val components = listOf("Hello", "World").toLore() {}
            assertEquals(
                listOf(
                    Component.text().content("Hello").build(),
                    Component.text().content("World").build()
                ),
                components
            )
        }

        @Test
        fun `should return component with all strings and transform`() {
            val components = listOf("Hello", "World").toLore { color(NamedTextColor.RED) }
            assertEquals(
                listOf(
                    Component.text().content("Hello").color(NamedTextColor.RED).build(),
                    Component.text().content("World").color(NamedTextColor.RED).build()
                ),
                components
            )
        }

    }

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
        fun `should create only one element if line length is greater than string size`() {
            for (i in 1..100) {
                val string = "a".repeat(i)
                val sequence = string.toFormattedLore(i + 1)
                assert(sequence.count() == 1)
            }
        }

        @Test
        fun `should create only one element if line length is equals to the string size`() {
            val sentence = "Hello World"
            assertEquals(listOf(sentence), sentence.toFormattedLore(sentence.length))
        }

        @Test
        fun `should create multiple elements by cut on the space char`() {
            val sequence = "Hello World".toFormattedLore(5)
            assertEquals(listOf("Hello", "World"), sequence)
        }

        @Test
        fun `should create multiple elements by cut on the line length char adding a '-'`() {
            val sequence1 = "Hello World".toFormattedLore(4)
            assertEquals(listOf("Hel-", "lo", "Wor-", "ld"), sequence1)

            val sequence2 = "Hello World".toFormattedLore(3)
            assertEquals(listOf("He-", "llo", "Wo-", "rld"), sequence2)
        }

        @Test
        fun `should create multiple element by cut on the previous space char`() {
            // Indexes of "W" to "d" chars
            for (i in 6..10) {
                assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLore(i))
            }
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
                listOf("Ajoutez, chattez et rejoignez", "vos amis à travers le serveur"),
                "Ajoutez, chattez et rejoignez vos amis à travers le serveur".toFormattedLore(30),
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", "v"),
                "Ajoutez, chattez et rejoignez v".toFormattedLore(30),
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", " "),
                "Ajoutez, chattez et rejoignez  ".toFormattedLore(30),
            )

            assertEquals(
                listOf("Add, chat and join your friends through", "the server"),
                "Add, chat and join your friends through the server".toFormattedLore(40),
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
        fun `should create only one element if line length is greater than string size`() {
            for (i in 1..100) {
                val string = "a".repeat(i)
                val sequence = string.toFormattedLoreSequence(i + 1)
                assert(sequence.count() == 1)
            }
        }

        @Test
        fun `should create only one element if line length is equals to the string size`() {
            val sentence = "Hello World"
            assertEquals(listOf(sentence), sentence.toFormattedLoreSequence(sentence.length).toList())
        }

        @Test
        fun `should create multiple elements by cut on the space char`() {
            val sequence = "Hello World".toFormattedLoreSequence(5).toList()
            assertEquals(listOf("Hello", "World"), sequence)
        }

        @Test
        fun `should create multiple elements by cut on the line length char adding a '-'`() {
            val sequence1 = "Hello World".toFormattedLoreSequence(4).toList()
            assertEquals(listOf("Hel-", "lo", "Wor-", "ld"), sequence1)

            val sequence2 = "Hello World".toFormattedLoreSequence(3).toList()
            assertEquals(listOf("He-", "llo", "Wo-", "rld"), sequence2)
        }

        @Test
        fun `should create multiple element by cut on the previous space char`() {
            // Indexes of "W" to "d" chars
            for (i in 6..10) {
                assertEquals(listOf("Hello", "World"), "Hello World".toFormattedLoreSequence(i).toList())
            }
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
                listOf("Ajoutez, chattez et rejoignez", "vos amis à travers le serveur"),
                "Ajoutez, chattez et rejoignez vos amis à travers le serveur".toFormattedLoreSequence(30).toList(),
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", "v"),
                "Ajoutez, chattez et rejoignez v".toFormattedLoreSequence(30).toList(),
            )

            assertEquals(
                listOf("Ajoutez, chattez et rejoignez", " "),
                "Ajoutez, chattez et rejoignez  ".toFormattedLoreSequence(30).toList(),
            )

            assertEquals(
                listOf("Add, chat and join your friends through", "the server"),
                "Add, chat and join your friends through the server".toFormattedLoreSequence(40).toList(),
            )
        }
    }
}