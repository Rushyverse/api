package com.github.rushyverse.api.serializer

import be.seeseemelk.mockbukkit.MockBukkit
import com.github.rushyverse.api.utils.randomEnum
import com.github.rushyverse.api.utils.randomInt
import io.kotest.assertions.json.shouldEqualJson
import kotlinx.serialization.json.Json
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ItemStackSerializerTest {

    @BeforeTest
    fun onBefore() {
        MockBukkit.mock()
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Serialize {

        @Test
        fun `with only material`() {
            val material = randomEnum<Material>()
            val amount = randomInt(1, 64)
            val itemStack = ItemStack(material, amount)
            val json = Json.encodeToString(ItemStackSerializer, itemStack)
            json shouldEqualJson """
                {
                    "material": "${material.name}",
                    "amount": $amount,
                    "enchantments": {}
                }
            """.trimIndent()
        }

        @Test
        @Disabled // Waiting for https://github.com/MockBukkit/MockBukkit/pull/831
        fun `with enchantments`() {
            val itemStack = ItemStack(Material.WOODEN_AXE).apply {
                addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
                addUnsafeEnchantment(Enchantment.ARROW_FIRE, 42)
            }
            val json = Json.encodeToString(ItemStackSerializer, itemStack)
            json shouldEqualJson """
                {
                  "material": "WOODEN_AXE",
                  "amount": 1,
                  "enchantments": {
                    "power": 1,
                    "flame": 42
                  },
                  "unbreakable": null,
                  "customMetaModel": null,
                  "destroyableKeys": [],
                  "placeableKeys": null,
                  "displayName": null,
                  "lore": null,
                  "durability": null,
                  "texture": null,
                  "patterns": null,
                  "flags": null
                }
            """.trimIndent()
        }

    }
}
