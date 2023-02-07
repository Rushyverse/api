package com.github.rushyverse.api.item

import com.github.rushyverse.api.utils.assertCoroutineContextFromScope
import com.github.rushyverse.api.utils.randomString
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryConditionResult
import org.junit.jupiter.api.Nested
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class InventoryConditionSuspendTest {

    @Nested
    inner class AsNative {

        @Test
        fun `should use params sent to the native inventory`() {
            val expectedPlayer = mockk<Player>(randomString())
            val expectedSlot = 420
            val expectedClickType = mockk<ClickType>(randomString())
            val expectedInventoryConditionResult = mockk<InventoryConditionResult>(randomString())

            val inventoryConditionSuspend = InventoryConditionSuspend { player, clickedSlot, clickType, result ->
                assertEquals(expectedPlayer, player)
                assertEquals(expectedSlot, clickedSlot)
                assertEquals(expectedClickType, clickType)
                assertEquals(expectedInventoryConditionResult, result)
            }

            val coroutineScope = CoroutineScope(Dispatchers.Default)
            val inventoryCondition = inventoryConditionSuspend.asNative(coroutineScope)
            inventoryCondition.accept(expectedPlayer, expectedSlot, expectedClickType, expectedInventoryConditionResult)
        }

        @Test
        fun `should stay in current thread before suspend point`() {
            val thread = Thread.currentThread().id
            val inventoryConditionSuspend = InventoryConditionSuspend { _, _, _, _ ->
                assertEquals(thread, Thread.currentThread().id)
            }

            val coroutineScope = CoroutineScope(Dispatchers.Default)
            val inventoryCondition = inventoryConditionSuspend.asNative(coroutineScope)
            inventoryCondition.accept(mockk(), 0, mockk(), mockk())
        }

        @Test
        fun `should change thread context after suspend point`() {
            val coroutineScope = CoroutineScope(Dispatchers.Default)

            val inventoryConditionSuspend = InventoryConditionSuspend { _, _, _, _ ->
                yield()
                assertCoroutineContextFromScope(coroutineScope, coroutineContext)
            }

            val inventoryCondition = inventoryConditionSuspend.asNative(coroutineScope)
            inventoryCondition.accept(mockk(), 0, mockk(), mockk())
        }

    }
}