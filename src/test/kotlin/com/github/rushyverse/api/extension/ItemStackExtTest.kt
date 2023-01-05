package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemStackExtTest {

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