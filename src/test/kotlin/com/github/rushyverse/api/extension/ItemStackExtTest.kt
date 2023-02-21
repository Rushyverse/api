package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.junit.jupiter.api.Nested
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemStackExtTest {

    @Nested
    inner class Builder {

        @Nested
        inner class FormattedLore {

            private lateinit var builder: ItemStack.Builder

            @BeforeTest
            fun onBefore() {
                builder = ItemStack.builder(Material.DIAMOND_SWORD)
            }

            @Test
            fun `should return an empty sequence if the string is empty`() {
                val expected = ItemStack.builder(Material.DIAMOND_SWORD).lore().build()
                assertEquals(expected, builder.formattedLore("").build())
            }

            @Test
            fun `should cut sentence without space`() {
                val expected = ItemStack.builder(Material.DIAMOND_SWORD).lore(
                    Component.text("0123-").color(NamedTextColor.GRAY),
                    Component.text("4567-").color(NamedTextColor.GRAY),
                    Component.text("89ab-").color(NamedTextColor.GRAY),
                    Component.text("cdef").color(NamedTextColor.GRAY)
                ).build()
                assertEquals(
                    expected,
                    builder.formattedLore("0123456789abcdef", 5).build()
                )
            }

            @Test
            fun `should create only one component if line length is equals to the string size`() {
                val sentence = "Hello World"
                val expected = ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(Component.text(sentence).color(NamedTextColor.GRAY))
                    .build()
                assertEquals(expected, builder.formattedLore(sentence).build())
            }

            @Test
            fun `should create multiple components by cut on the line length char adding a '-'`() {
                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Hel-").color(NamedTextColor.GRAY),
                            Component.text("lo").color(NamedTextColor.GRAY),
                            Component.text("Wor-").color(NamedTextColor.GRAY),
                            Component.text("ld").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Hello World", 4).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("He-").color(NamedTextColor.GRAY),
                            Component.text("llo").color(NamedTextColor.GRAY),
                            Component.text("Wo-").color(NamedTextColor.GRAY),
                            Component.text("rld").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Hello World", 3).build()
                )
            }

            @Test
            fun `should create multiple element by cut on the previous space char`() {
                val sentence = "Hello World"
                // Indexes of "W" to "d" chars
                for (i in 6..10) {
                    assertEquals(
                        ItemStack.builder(Material.DIAMOND_SWORD)
                            .lore(
                                Component.text("Hello").color(NamedTextColor.GRAY),
                                Component.text("World").color(NamedTextColor.GRAY)
                            )
                            .build(),
                        builder.formattedLore(sentence, i).build()
                    )
                }
            }

            @Test
            fun `should create multiple components with long sentence`() {
                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("This is a tool").color(NamedTextColor.GRAY),
                            Component.text("to create a").color(NamedTextColor.GRAY),
                            Component.text("game").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("This is a tool to create a game", 15).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("This is a tool").color(NamedTextColor.GRAY),
                            Component.text("to create a").color(NamedTextColor.GRAY),
                            Component.text("game0123456789-").color(NamedTextColor.GRAY),
                            Component.text("0123456789").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("This is a tool to create a game01234567890123456789", 15).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                            Component.text("vos amis à travers le serveur").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Ajoutez, chattez et rejoignez vos amis à travers le serveur", 30).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                            Component.text("v").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Ajoutez, chattez et rejoignez v", 30).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                            Component.text(" ").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Ajoutez, chattez et rejoignez  ", 30).build()
                )

                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Add, chat and join your friends through").color(NamedTextColor.GRAY),
                            Component.text("the server").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    builder.formattedLore("Add, chat and join your friends through the server", 40).build()
                )
            }

            @Test
            fun `should apply transformation for created components`() {
                val expected = ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Hello").color(NamedTextColor.RED),
                        Component.text("World").color(NamedTextColor.RED)
                    )
                    .build()
                assertEquals(
                    expected,
                    builder.formattedLore("Hello World", 5) { color(NamedTextColor.RED) }.build()
                )
            }

        }
    }

    @Nested
    inner class FormattedLore {

        private lateinit var item: ItemStack

        @BeforeTest
        fun onBefore() {
            item = ItemStack.of(Material.DIAMOND_SWORD)
        }

        @Test
        fun `should return an empty sequence if the string is empty`() {
            val expected = ItemStack.builder(Material.DIAMOND_SWORD).lore().build()
            assertEquals(expected, item.withFormattedLore(""))
        }

        @Test
        fun `should cut sentence without space`() {
            val expected = ItemStack.builder(Material.DIAMOND_SWORD).lore(
                Component.text("0123-").color(NamedTextColor.GRAY),
                Component.text("4567-").color(NamedTextColor.GRAY),
                Component.text("89ab-").color(NamedTextColor.GRAY),
                Component.text("cdef").color(NamedTextColor.GRAY)
            ).build()
            assertEquals(
                expected,
                item.withFormattedLore("0123456789abcdef", 5)
            )
        }

        @Test
        fun `should create only one component if line length is equals to the string size`() {
            val sentence = "Hello World"
            val expected = ItemStack.builder(Material.DIAMOND_SWORD)
                .lore(Component.text(sentence).color(NamedTextColor.GRAY))
                .build()
            assertEquals(expected, item.withFormattedLore(sentence))
        }

        @Test
        fun `should create multiple components by cut on the line length char adding a '-'`() {
            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Hel-").color(NamedTextColor.GRAY),
                        Component.text("lo").color(NamedTextColor.GRAY),
                        Component.text("Wor-").color(NamedTextColor.GRAY),
                        Component.text("ld").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Hello World", 4)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("He-").color(NamedTextColor.GRAY),
                        Component.text("llo").color(NamedTextColor.GRAY),
                        Component.text("Wo-").color(NamedTextColor.GRAY),
                        Component.text("rld").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Hello World", 3)
            )
        }

        @Test
        fun `should create multiple element by cut on the previous space char`() {
            val sentence = "Hello World"
            // Indexes of "W" to "d" chars
            for (i in 6..10) {
                assertEquals(
                    ItemStack.builder(Material.DIAMOND_SWORD)
                        .lore(
                            Component.text("Hello").color(NamedTextColor.GRAY),
                            Component.text("World").color(NamedTextColor.GRAY)
                        )
                        .build(),
                    item.withFormattedLore(sentence, i)
                )
            }
        }

        @Test
        fun `should create multiple components with long sentence`() {
            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("This is a tool").color(NamedTextColor.GRAY),
                        Component.text("to create a").color(NamedTextColor.GRAY),
                        Component.text("game").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("This is a tool to create a game", 15)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("This is a tool").color(NamedTextColor.GRAY),
                        Component.text("to create a").color(NamedTextColor.GRAY),
                        Component.text("game0123456789-").color(NamedTextColor.GRAY),
                        Component.text("0123456789").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("This is a tool to create a game01234567890123456789", 15)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                        Component.text("vos amis à travers le serveur").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Ajoutez, chattez et rejoignez vos amis à travers le serveur", 30)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                        Component.text("v").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Ajoutez, chattez et rejoignez v", 30)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Ajoutez, chattez et rejoignez").color(NamedTextColor.GRAY),
                        Component.text(" ").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Ajoutez, chattez et rejoignez  ", 30)
            )

            assertEquals(
                ItemStack.builder(Material.DIAMOND_SWORD)
                    .lore(
                        Component.text("Add, chat and join your friends through").color(NamedTextColor.GRAY),
                        Component.text("the server").color(NamedTextColor.GRAY)
                    )
                    .build(),
                item.withFormattedLore("Add, chat and join your friends through the server", 40)
            )
        }

        @Test
        fun `should apply transformation for created components`() {
            val expected = ItemStack.builder(Material.DIAMOND_SWORD)
                .lore(
                    Component.text("Hello").color(NamedTextColor.RED),
                    Component.text("World").color(NamedTextColor.RED)
                )
                .build()
            assertEquals(
                expected,
                item.withFormattedLore("Hello World", 5) { color(NamedTextColor.RED) }
            )
        }

    }

    @Nested
    inner class WithLore {

        @Test
        fun `should set a single lore`() {
            val item = ItemStack.of(Material.STONE).withLore(Component.text("Hello"))
            assertEquals(
                ItemStack.builder(Material.STONE).lore(Component.text("Hello")).build(),
                item
            )
        }

    }
}