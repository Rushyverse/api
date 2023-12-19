package com.github.rushyverse.api.gui.load

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ShiftInventoryLoadingAnimationTest {

    private lateinit var inventory: Inventory

    @BeforeTest
    fun onBefore() {
        inventory = mockk {
            val contents = arrayOfNulls<ItemStack>(9 * 3)
            every { size } returns contents.size
            every { getContents() } returns contents
            every { setContents(any()) } answers {
                val items = firstArg<Array<ItemStack?>>()
                items.forEachIndexed { index, itemStack ->
                    contents[index] = itemStack
                }
        }
            }
    }

    @Test
    fun `should not change inventory if initialize is empty`() {
        val delay = 10.milliseconds
        val animation = ShiftInventoryLoadingAnimation<Unit>(
            initialize = { emptySequence() },
            shift = 1,
            delay = delay
        )

        runBlocking {
            val job = launch { animation.loading(Unit, inventory) }
            delay(delay * 3)
            job.cancelAndJoin()
        }

        inventory.contents shouldBe arrayOfNulls(inventory.size)
    }

    @Test
    fun `should just initialize if shift is zero`() = runBlocking {
        val delay = 10.milliseconds
        val items = Array(inventory.size) { mockk<ItemStack>() }

        val animation = ShiftInventoryLoadingAnimation<Unit>(
            initialize = { items.asSequence() },
            shift = 0,
            delay = delay
        )

        val job = launch { animation.loading(Unit, inventory) }
        delay(delay * 3)
        job.cancelAndJoin()

        inventory.contents.toList() shouldContainExactly items.toList()
    }
}
