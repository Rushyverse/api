package com.github.rushyverse.api.extension

import io.github.distractic.bukkit.api.item.CraftSlot
import io.github.distractic.bukkit.api.item.exception.CraftResultMissingException
import io.github.distractic.bukkit.api.utils.getRandomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class JavaPluginExtTest {

    private lateinit var plugin: JavaPlugin
    private lateinit var server: Server

    @BeforeTest
    fun onBefore() {
        plugin = mockk()
        server = mockk()

        every { plugin.server } returns server
        every { plugin.name } returns getRandomString()
        every { server.pluginManager } returns mockk()
    }

    @Nested
    @DisplayName("Register craft")
    inner class RegisterCraft {

        @Test
        fun `no add a final item throws error`() {
            assertThrows<CraftResultMissingException> {
                plugin.registerCraft {}
            }
        }

        @Test
        fun `add recipe created into the server`() {
            val slotRecipe = slot<Recipe>()
            every { server.addRecipe(capture(slotRecipe)) } returns true

            val expectedResult: ItemStack
            val expectedKey = "test"
            plugin.registerCraft(key = "test") {
                result = mockk<ItemStack>().also {
                    every { it.type } returns Material.STICK
                    every { it.amount } returns 1
                    every { it.hasItemMeta() } returns false
                }
                expectedResult = result!!
            }

            val recipe = slotRecipe.captured as ShapedRecipe
            assertEquals(expectedKey, recipe.key.key)

            val resultRecipe = recipe.result
            assertEquals(expectedResult.amount, resultRecipe.amount)
            assertEquals(expectedResult.type, resultRecipe.type)
            assertContentEquals(arrayOf("   ", "   ", "   "), recipe.shape)
        }

        @Test
        fun `add the defined recipe from the builder`() {
            val slotRecipe = slot<Recipe>()
            every { server.addRecipe(capture(slotRecipe)) } returns true

            plugin.registerCraft {
                val item1 = mockk<ItemStack>()
                val item2 = mockk<ItemStack>()

                set(CraftSlot.TopLeft, item = item1)
                set(CraftSlot.Top, item = item2)
                set(CraftSlot.TopRight, item = mockk())

                set(CraftSlot.CenterLeft, item = mockk())
                set(CraftSlot.Center, item = item1)
                set(CraftSlot.CenterRight, item = mockk())

                set(CraftSlot.BottomLeft, item = item2)
                set(CraftSlot.Bottom, item = mockk())
                set(CraftSlot.BottomRight, item = mockk())
                result = mockk<ItemStack>().also {
                    every { it.type } returns Material.ACACIA_BUTTON
                    every { it.amount } returns 1
                    every { it.hasItemMeta() } returns false
                }
            }

            val recipe = slotRecipe.captured as ShapedRecipe
            assertContentEquals(arrayOf("ABC", "DAE", "BFG"), recipe.shape)
        }

        @Test
        fun `add all different item will create different designation`() {
            val slotRecipe = slot<Recipe>()
            every { server.addRecipe(capture(slotRecipe)) } returns true

            plugin.registerCraft {
                for (index in CraftSlot.values()) {
                    set(index, item = mockk())
                }

                result = mockk<ItemStack>().also {
                    every { it.type } returns Material.ACACIA_BUTTON
                    every { it.amount } returns 1
                    every { it.hasItemMeta() } returns false
                }
            }

            val recipe = slotRecipe.captured as ShapedRecipe
            assertContentEquals(arrayOf("ABC", "DEF", "GHI"), recipe.shape)
        }

        @Test
        fun `add several times the same item will create once designation`() {
            val slotRecipe = slot<Recipe>()
            every { server.addRecipe(capture(slotRecipe)) } returns true

            plugin.registerCraft {
                val item = mockk<ItemStack>()
                for (index in CraftSlot.values()) {
                    set(index, item = item)
                }
                result = mockk<ItemStack>().also {
                    every { it.type } returns Material.ACACIA_BUTTON
                    every { it.amount } returns 1
                    every { it.hasItemMeta() } returns false
                }
            }

            val recipe = slotRecipe.captured as ShapedRecipe
            assertContentEquals(arrayOf("AAA", "AAA", "AAA"), recipe.shape)
        }

        @Test
        fun `no key add a default UUID name`() {
            val slotRecipe = slot<Recipe>()
            every { server.addRecipe(capture(slotRecipe)) } returns true

            plugin.registerCraft {
                result = mockk<ItemStack>().also {
                    every { it.type } returns Material.STICK
                    every { it.amount } returns 1
                    every { it.hasItemMeta() } returns false
                }
            }

            val recipe = slotRecipe.captured as ShapedRecipe
            val key = recipe.key.key
            UUID.fromString(key)
        }

    }

}