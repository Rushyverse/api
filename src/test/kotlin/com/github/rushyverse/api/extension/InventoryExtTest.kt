package com.github.rushyverse.api.extension

import com.github.rushyverse.api.item.ItemComparator
import com.github.rushyverse.api.utils.assertCoroutineContextFromScope
import com.github.rushyverse.api.utils.randomPos
import com.github.rushyverse.api.utils.randomString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.inventory.AbstractInventory
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryCondition
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext
import kotlin.test.*

@Timeout(5, unit = TimeUnit.SECONDS)
class InventoryExtTest {

    @Test
    fun `slots property should return the range of slots`() {
        val inventory: AbstractInventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
        assertEquals(0..8, inventory.slots)
    }

    @Nested
    inner class RemoveCondition {

        @Test
        fun `should remove the condition of interaction with the inventory`() {
            val inventory: AbstractInventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
            val condition = InventoryCondition { _, _, _, _ -> }
            inventory.addInventoryCondition(condition)
            assertTrue(inventory.removeCondition(condition))
        }

        @Test
        fun `should return false if the condition is not present`() {
            val inventory: AbstractInventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
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
            val inventory: AbstractInventory = player.inventory
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
    inner class AddInventoryConditionSuspend {

        @Test
        fun `should register a suspendable condition`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory
            var count = 0
            inventory.addInventoryConditionSuspend { playerClicker, clickedSlot, type, _ ->
                assertEquals(player, playerClicker)
                assertEquals(0, clickedSlot)
                if (count == 0) {
                    assertEquals(ClickType.LEFT_CLICK, type)
                } else {
                    assertEquals(ClickType.RIGHT_CLICK, type)
                }
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
        }

        @Test
        fun `should stay in current thread before suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            var isCalled = false
            val thread = Thread.currentThread().id
            inventory.addInventoryConditionSuspend(CoroutineScope(Dispatchers.Default)) { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
                isCalled = true
            }

            assertTrue(inventory.leftClick(player, 0))
            assertTrue(isCalled)
        }

        @Test
        fun `should change thread context after suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val latch = CountDownLatch(1)
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            inventory.addInventoryConditionSuspend(coroutineScope) { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
                latch.countDown()
            }

            assertTrue(inventory.leftClick(player, 0))
            latch.await()
        }
    }

    @EnvTest
    @Nested
    inner class RegisterClickEventOnSlotSuspend {

        @Test
        fun `should register a click event on a slot`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val slot = 0
            var count = 0
            inventory.registerClickEventOnSlotSuspend(slot) { _, _, _, _ ->
                count++
            }
            assertEquals(1, inventory.inventoryConditions.size)

            inventory.setItemStack(slot, ItemStack.of(Material.DIAMOND))

            val playerSlot = 36
            assertEquals(0, count)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            assertTrue(inventory.rightClick(player, playerSlot))
            assertEquals(2, count)

            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should stay in current thread before suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            var isCalled = false
            val thread = Thread.currentThread().id
            inventory.registerClickEventOnSlotSuspend(0, CoroutineScope(Dispatchers.Default)) { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
                isCalled = true
            }

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(isCalled)
        }

        @Test
        fun `should change thread context after suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val latch = CountDownLatch(1)
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            inventory.registerClickEventOnSlotSuspend(0, coroutineScope) { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
                latch.countDown()
            }

            assertTrue(inventory.leftClick(player, 36))
            latch.await()
        }

    }

    @EnvTest
    @Nested
    inner class RegisterClickEventOnSlot {

        @Test
        fun `should register a click event on a slot`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val slot = 0
            var count = 0
            inventory.registerClickEventOnSlot(slot) { _, _, _, _ ->
                count++
            }
            assertEquals(1, inventory.inventoryConditions.size)

            inventory.setItemStack(slot, ItemStack.of(Material.DIAMOND))

            val playerSlot = 36
            assertEquals(0, count)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            assertTrue(inventory.rightClick(player, playerSlot))
            assertEquals(2, count)

            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

    }

    @EnvTest
    @Nested
    inner class RegisterClickEventOnItemSuspend {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.registerClickEventOnItemSuspend(
                item,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            inventory.setItemStack(0, item)
            assertEquals(0, count)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, item.withAmount(2))
            assertTrue(inventory.rightClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(1, ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.registerClickEventOnItemSuspend(
                item,
                ItemComparator.EQUALS,
                CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
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
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var clicked = false
            inventory.registerClickEventOnItemSuspend(
                item,
                ItemComparator.SIMILAR,
                CoroutineScope(Dispatchers.Default)
            ) { playerClicked, slot, type, _ ->
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

        @Test
        fun `should stay in current thread before suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            var isCalled = false
            val thread = Thread.currentThread().id
            val item = ItemStack.of(Material.DIAMOND)
            inventory.registerClickEventOnItemSuspend(
                item,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
                isCalled = true
            }

            inventory.setItemStack(0, item)
            assertTrue(inventory.leftClick(player, 36))
            assertTrue(isCalled)
        }

        @Test
        fun `should change thread context after suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val latch = CountDownLatch(1)
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            val item = ItemStack.of(Material.DIAMOND)
            inventory.registerClickEventOnItemSuspend(item, coroutineScope = coroutineScope) { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
                latch.countDown()
            }

            inventory.setItemStack(0, item)
            assertTrue(inventory.leftClick(player, 36))
            latch.await()
        }
    }

    @EnvTest
    @Nested
    inner class RegisterClickEventOnItem {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.registerClickEventOnItem(item) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            inventory.setItemStack(0, item)
            assertEquals(0, count)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, item.withAmount(2))
            assertTrue(inventory.rightClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(1, ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            inventory.registerClickEventOnItem(item, ItemComparator.EQUALS) { _, _, _, _ ->
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
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var clicked = false
            inventory.registerClickEventOnItem(item, ItemComparator.SIMILAR) { playerClicked, slot, type, _ ->
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
    inner class SetItemStackSuspendWithClickHandler {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            inventory.setItemStackSuspend(
                slot,
                item,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(slot))
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot, item.withAmount(2))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            inventory.setItemStackSuspend(
                slot,
                item,
                ItemComparator.EQUALS,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(item, inventory.getItemStack(slot))
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, item)
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            val slot = 0
            var clicked = false
            inventory.setItemStackSuspend(
                slot,
                item,
                ItemComparator.SIMILAR,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { playerClicked, clickedSlot, type, _ ->
                assertEquals(player, playerClicked)
                assertEquals(slot, clickedSlot)
                assertEquals(ClickType.LEFT_CLICK, type)
                clicked = true
            }

            val item2 = item.withAmount(10)
            inventory.setItemStack(slot, item2)

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }

        @Test
        fun `should stay in current thread before suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            val slot = 0
            var clicked = false
            val thread = Thread.currentThread().id
            inventory.setItemStackSuspend(
                slot,
                item,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
                clicked = true
            }

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }

        @Test
        fun `should change thread context after suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            val slot = 0

            val latch = CountDownLatch(1)
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            inventory.setItemStackSuspend(slot, item, coroutineScope = coroutineScope) { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
                latch.countDown()
            }

            assertTrue(inventory.leftClick(player, 36))
            latch.await()
        }
    }

    @EnvTest
    @Nested
    inner class SetItemStackWithClickHandler {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            inventory.setItemStack(slot, item) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(slot))
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot, item.withAmount(2))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            inventory.setItemStack(slot, item, ItemComparator.EQUALS) { _, _, _, _ ->
                count++
            }

            val playerSlot = 36
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(item, inventory.getItemStack(slot))
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, item)
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            val slot = 0
            var clicked = false
            inventory.setItemStack(slot, item, ItemComparator.SIMILAR) { playerClicked, clickedSlot, type, _ ->
                assertEquals(player, playerClicked)
                assertEquals(slot, clickedSlot)
                assertEquals(ClickType.LEFT_CLICK, type)
                clicked = true
            }

            val item2 = item.withAmount(10)
            inventory.setItemStack(slot, item2)

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }
    }

    @EnvTest
    @Nested
    inner class SlotIsEmpty {

        @Test
        fun `should return true if the slot is empty`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            assertTrue(inventory.slotIsEmpty(0))
        }

        @Test
        fun `should return false if the slot is not empty`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            assertFalse(inventory.slotIsEmpty(0))
        }
    }

    @EnvTest
    @Nested
    inner class FirstAvailableSlot {

        @Test
        fun `should return the first slot if all slots are empty`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            assertEquals(0, inventory.firstAvailableSlot())
        }

        @Test
        fun `should return the first available slot`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            assertEquals(1, inventory.firstAvailableSlot())
        }

        @Test
        fun `should return -1 if all slots are full`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            inventory.slots.forEach { inventory.setItemStack(it, ItemStack.of(Material.DIAMOND)) }
            assertEquals(-1, inventory.firstAvailableSlot())
        }
    }

    @EnvTest
    @Nested
    inner class AddItemStackSuspendWithClickHandler {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            val condition = inventory.addItemStackSuspend(
                item,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                count++
            }

            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(0))

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, ItemStack.AIR)
            inventory.addItemStack(item.withAmount(2))

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, ItemStack.AIR)
            inventory.addItemStack(ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            val condition = inventory.addItemStackSuspend(
                item,
                ItemComparator.EQUALS,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                count++
            }

            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(slot))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, item)
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            var clicked = false
            val condition = inventory.addItemStackSuspend(
                item,
                ItemComparator.SIMILAR,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ ->
                clicked = true
            }

            assertNotNull(condition)

            val item2 = item.withAmount(10)
            inventory.setItemStack(1, item2)

            assertTrue(inventory.leftClick(player, 37))
            assertTrue(clicked)
        }

        @Test
        fun `should not add item and create condition if item can't be added`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            inventory.slots.forEach { inventory.setItemStack(it, item) }

            val diamondItem = ItemStack.of(Material.DIAMOND)
            val condition = inventory.addItemStackSuspend(
                diamondItem,
                coroutineScope = CoroutineScope(Dispatchers.Default)
            ) { _, _, _, _ -> }
            assertNull(condition)
            inventory.itemStacks.forEach { assertNotEquals(diamondItem, it) }
        }

        @Test
        fun `should stay in current thread before suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            var clicked = false
            val thread = Thread.currentThread().id
            inventory.addItemStackSuspend(item, coroutineScope = CoroutineScope(Dispatchers.Default)) { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
                clicked = true
            }

            assertTrue(inventory.leftClick(player, 36))
            assertTrue(clicked)
        }

        @Test
        fun `should change thread context after suspend point`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)

            val latch = CountDownLatch(1)
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            inventory.addItemStackSuspend(item, coroutineScope = coroutineScope) { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
                latch.countDown()
            }

            assertTrue(inventory.leftClick(player, 36))
            latch.await()
        }
    }

    @EnvTest
    @Nested
    inner class AddItemStackWithClickHandler {

        @Test
        fun `default identifier should be equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            var count = 0
            val condition = inventory.addItemStack(item) { _, _, _, _ ->
                count++
            }

            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(0))

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, ItemStack.AIR)
            inventory.addItemStack(item.withAmount(2))

            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(0, ItemStack.AIR)
            inventory.addItemStack(ItemStack.of(Material.DIAMOND))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is equals`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.DIAMOND)
            val slot = 0
            var count = 0
            val condition = inventory.addItemStack(item, ItemComparator.EQUALS) { _, _, _, _ ->
                count++
            }

            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(1, inventory.itemStacks.filterNot { it.isAir }.size)

            val playerSlot = 36
            assertEquals(item, inventory.getItemStack(slot))
            assertTrue(inventory.leftClick(player, playerSlot))
            assertEquals(1, count)

            inventory.setItemStack(slot + 1, item)
            assertTrue(inventory.leftClick(player, playerSlot + 1))
            assertEquals(2, count)
        }

        @Test
        fun `should trigger the click handler when the item is similar`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            var clicked = false
            val condition = inventory.addItemStack(item, ItemComparator.SIMILAR) { _, _, _, _ ->
                clicked = true
            }

            assertNotNull(condition)

            val item2 = item.withAmount(10)
            inventory.setItemStack(1, item2)

            assertTrue(inventory.leftClick(player, 37))
            assertTrue(clicked)
        }

        @Test
        fun `should not add item and create condition if item can't be added`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())
            val inventory: AbstractInventory = player.inventory

            val item = ItemStack.of(Material.ARROW)
            inventory.slots.forEach { inventory.setItemStack(it, item) }

            val diamondItem = ItemStack.of(Material.DIAMOND)
            val condition = inventory.addItemStack(diamondItem) { _, _, _, _ -> }
            assertNull(condition)
            inventory.itemStacks.forEach { assertNotEquals(diamondItem, it) }
        }
    }

    @EnvTest
    @Nested
    inner class SetCloseButton {

        @Test
        fun `should set the close button`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())

            val inventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
            val condition = inventory.setCloseButton(0)
            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(
                ItemStack.of(Material.BARRIER).withDisplayName(Component.text("❌").color(NamedTextColor.RED)),
                inventory.getItemStack(0)
            )

            player.openInventory(inventory)
            assertFalse(inventory.leftClick(player, 0))
            assertFalse(inventory.isViewer(player))
            assertNull(player.openInventory)
        }

        @Test
        fun `should set the close button who override item at the slot`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())

            val inventory = Inventory(InventoryType.CHEST_1_ROW, "Test")
            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            inventory.setCloseButton(0)
            assertTrue { inventory.getItemStack(0).material() == Material.BARRIER }

            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            player.openInventory(inventory)
            assertTrue(inventory.leftClick(player, 0))
            assertTrue(inventory.isViewer(player))
            assertEquals(inventory, player.openInventory)
        }
    }

    @EnvTest
    @Nested
    inner class SetPreviousButton {

        private fun getItem(backInventory: Inventory): ItemStack {
            return getChangeItem("< ", backInventory)
        }

        @Test
        fun `should set the previous button`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())

            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val backInventory = Inventory(InventoryType.BEACON, randomString())
            val condition = inventory.setPreviousButton(0, backInventory)
            val expectedNavItem = getItem(backInventory)
            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(expectedNavItem, inventory.getItemStack(0))

            player.openInventory(inventory)
            assertTrue(inventory.isViewer(player))
            assertEquals(inventory, player.openInventory)
            assertFalse(inventory.leftClick(player, 0))

            assertFalse(inventory.isViewer(player))
            assertTrue(backInventory.isViewer(player))
            assertEquals(backInventory, player.openInventory)
        }

        @Test
        fun `should set the previous button who override item at the slot`(env: Env) {
            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val backInventory = Inventory(InventoryType.BEACON, randomString())
            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            inventory.setPreviousButton(0, backInventory)
            val expectedNavItem = getItem(backInventory)
            assertEquals(expectedNavItem, inventory.getItemStack(0))
        }
    }

    @EnvTest
    @Nested
    inner class SetNextButton {

        private fun getItem(inventory: Inventory): ItemStack {
            return getChangeItem("> ", inventory)
        }

        @Test
        fun `should set the next button`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())

            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val nextInventory = Inventory(InventoryType.BLAST_FURNACE, randomString())
            val condition = inventory.setNextButton(0, nextInventory)
            val expectedNavItem = getItem(nextInventory)
            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(expectedNavItem, inventory.getItemStack(0))

            player.openInventory(inventory)
            assertTrue(inventory.isViewer(player))
            assertEquals(inventory, player.openInventory)
            assertFalse(inventory.leftClick(player, 0))

            assertFalse(inventory.isViewer(player))
            assertTrue(nextInventory.isViewer(player))
            assertEquals(nextInventory, player.openInventory)
        }

        @Test
        fun `should set the next button who override item at the slot`(env: Env) {
            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val nextInventory = Inventory(InventoryType.ENCHANTMENT, randomString())
            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            inventory.setNextButton(0, nextInventory)
            val expectedNavItem = getItem(nextInventory)
            assertEquals(expectedNavItem, inventory.getItemStack(0))
        }
    }

    @EnvTest
    @Nested
    inner class SetItemChangeInventory {

        private val textItem = "test"

        private fun getItem(inventory: Inventory): ItemStack {
            return getChangeItem(textItem, inventory)
        }

        @Test
        fun `should set the change button`(env: Env) {
            val instance = env.createFlatInstance()
            val player = env.createPlayer(instance, randomPos())

            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val changeInventory = Inventory(InventoryType.BLAST_FURNACE, randomString())
            val condition = inventory.setItemChangeInventory(0, changeInventory, textItem)
            val expectedNavItem = getItem(changeInventory)
            assertNotNull(condition)
            assertEquals(1, inventory.inventoryConditions.size)
            assertEquals(expectedNavItem, inventory.getItemStack(0))

            player.openInventory(inventory)
            assertTrue(inventory.isViewer(player))
            assertEquals(inventory, player.openInventory)
            assertFalse(inventory.leftClick(player, 0))

            assertFalse(inventory.isViewer(player))
            assertTrue(changeInventory.isViewer(player))
            assertEquals(changeInventory, player.openInventory)
        }

        @Test
        fun `should set the change button who override item at the slot`(env: Env) {
            val inventory = Inventory(InventoryType.CHEST_1_ROW, randomString())
            val nextInventory = Inventory(InventoryType.ENCHANTMENT, randomString())
            inventory.setItemStack(0, ItemStack.of(Material.DIAMOND))
            inventory.setItemChangeInventory(0, nextInventory, textItem)
            val expectedNavItem = getItem(nextInventory)
            assertEquals(expectedNavItem, inventory.getItemStack(0))
        }
    }

    private fun getChangeItem(text: String, inventory: Inventory): ItemStack {
        return ItemStack.of(Material.ARROW)
            .withDisplayName(
                Component.text(text)
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.BOLD, true)
                    .append(
                        inventory.title
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, true)
                            .decoration(TextDecoration.BOLD, false)
                    )
            )
    }
}