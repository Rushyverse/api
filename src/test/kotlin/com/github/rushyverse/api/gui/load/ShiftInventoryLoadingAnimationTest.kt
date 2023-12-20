package com.github.rushyverse.api.gui.load

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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

    @ParameterizedTest
    @ValueSource(ints = [0, -1, -2, -3, -4, -5, -6, -7, -8])
    fun `should throw if duration is not positive`(duration: Int) {
        shouldThrow<IllegalArgumentException> {
            ShiftInventoryLoadingAnimation<Unit>(
                initialize = { emptySequence() },
                shift = 1,
                delay = duration.milliseconds
            )
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8])
    fun `should not throw if duration is positive`(duration: Int) {
        shouldNotThrow<IllegalArgumentException> {
            ShiftInventoryLoadingAnimation<Unit>(
                initialize = { emptySequence() },
                shift = 1,
                delay = duration.nanoseconds
            )
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
        shouldJustInitializeWithoutShift(0)
    }

    @Test
    fun `should just initialize if shift is inventory size`() = runBlocking {
        shouldJustInitializeWithoutShift(inventory.size)
    }

    private suspend fun shouldJustInitializeWithoutShift(shift: Int) = coroutineScope {
        val delay = 10.milliseconds
        val items = Array(inventory.size) { mockk<ItemStack>() }

        val animation = ShiftInventoryLoadingAnimation<Unit>(
            initialize = { items.asSequence() },
            shift = shift,
            delay = delay
        )

        val job = launch { animation.loading(Unit, inventory) }
        delay(delay * 3)
        job.cancelAndJoin()

        inventory.contents.toList() shouldContainExactly items.toList()
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8])
    fun `should shift initialized items`(shift: Int) = runBlocking {
        val delay = 100.milliseconds
        val items = Array(inventory.size) { mockk<ItemStack>() }
        val itemList = items.toList()

        val animation = ShiftInventoryLoadingAnimation<Unit>(
            initialize = { items.asSequence() },
            shift = shift,
            delay = delay
        )

        val job = launch { animation.loading(Unit, inventory) }

        delay(delay / 2)
        repeat(5) {
            inventory.contents.toList() shouldContainExactly itemList
            delay(delay)
            Collections.rotate(itemList, shift)
            job.isActive shouldBe true
        }

        job.cancelAndJoin()
    }
}
