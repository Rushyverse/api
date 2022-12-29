package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.randomPos
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryCondition
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InventoryExtTest {

    @Test
    fun `slots property should return the range of slots`() {
        val inventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
        assertEquals(0..8, inventory.slots)
    }

    @Nested
    inner class RemoveCondition {

        @Test
        fun `should remove the condition of interaction with the inventory`() {
            val inventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
            val condition = InventoryCondition { _, _, _, _ -> }
            inventory.addInventoryCondition(condition)
            assertTrue(inventory.removeCondition(condition))
        }

        @Test
        fun `should return false if the condition is not present`() {
            val inventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
            val condition = InventoryCondition { _, _, _, _ -> }
            assertFalse(inventory.removeCondition(condition))
        }

    }

    @EnvTest
    @Nested
    inner class LockItemPosition {

        @Test
        fun `should lock the item position`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory
            val condition = inventory.lockItemPositions()
            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))

            inventory.slots.forEach {
                assertFalse(inventory.leftClick(player, it))
                assertFalse(inventory.rightClick(player, it))
            }

            player.inventory.removeCondition(condition)

            inventory.slots.forEach {
                assertTrue(inventory.leftClick(player, it))
                assertTrue(inventory.rightClick(player, it))
            }
        }
    }

    @EnvTest
    @Nested
    inner class registerClickEvent {

        @Test
        fun `should register a click event on a slot`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory

            val slot = 0
            var count = 0
            inventory.registerClickEventOnSlot(slot) { _, _, _, _ ->
                count++
            }
            assertEquals(1, inventory.inventoryConditions.size)

            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))

            val playerSlot = 36
            assertEquals(0, count)
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)
            assertTrue(inventory.rightClick(player, playerSlot))
            assertEquals(2, count)

            assertTrue(inventory.leftClick(player, 37))
            assertEquals(2, count)
        }

        @Test
        fun `should register a click event on an item`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.registerClickEventOnItem(item) { _, _, _, _ ->
                count++
            }
            assertEquals(1, inventory.inventoryConditions.size)

            inventory.setItemStack(0, item)
            inventory.setItemStack(1, item)
            inventory.setItemStack(3, item)

            assertEquals(0, count)

            assertTrue(inventory.leftClick(player, 36)) // remove the item
            assertEquals(1, count)

            assertTrue(inventory.rightClick(player, 37))
            assertEquals(2, count)

            assertTrue(inventory.leftClick(player, 38))
            assertEquals(2, count)

            assertTrue(inventory.leftClick(player, 39))
            assertEquals(3, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var clicked = false
            inventory.registerClickEventOnItem(item) { playerClicked, slot, type, _ ->
                assertEquals(player, playerClicked)
                assertEquals(0, slot)
                assertEquals(ClickType.LEFT_CLICK, type)
                clicked = true
            }

            val item2 = item.withAmount(20)
            inventory.setItemStack(0, item2)
            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }
    }

    @EnvTest
    @Nested
    inner class setItemStackWithClickHandler {

        @Test
        fun `should set the item stack with click handler`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.setItemStack(0, item) { playerClicked, _, type, _ ->
                assertEquals(player, playerClicked)
                assertEquals(ClickType.LEFT_CLICK, type)
                count++
            }

            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(item, inventory.getItemStack(0))
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            assertTrue(inventory.leftClick(player, 36))
            assertEquals(1, count)

            inventory.setItemStack(1, item)
            assertTrue(inventory.leftClick(player, 37))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            var clicked = false
            inventory.setItemStack(0, item) { playerClicked, slot, type, _ ->
                assertEquals(player, playerClicked)
                assertEquals(0, slot)
                assertEquals(ClickType.LEFT_CLICK, type)
                clicked = true
            }

            val item2 = item.withAmount(10)
            inventory.setItemStack(0, item2)

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }
    }
}