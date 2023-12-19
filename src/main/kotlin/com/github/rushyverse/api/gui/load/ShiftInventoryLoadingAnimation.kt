package com.github.rushyverse.api.gui.load

import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Animation that shifts the items in the inventory.
 * The items are shifted by [shift] slots every [delay].
 * The items are placed in the inventory by calling [initialize].
 * If too few items are returned by [initialize], the remaining slots will be filled with null items.
 * If too many items are returned by [initialize], the overflowing items will be ignored.
 * @param T Type of the key.
 * @property initialize Function that returns the sequence of items to place in the inventory.
 * @property shift Number of slots to shift the items by.
 * @property delay Delay between each shift.
 */
public class ShiftInventoryLoadingAnimation<T>(
    private val initialize: (T) -> Sequence<ItemStack>,
    private val shift: Int = 1,
    private val delay: Duration = 100.milliseconds,
) : InventoryLoadingAnimation<T> {

    override suspend fun loading(key: T, inventory: Inventory) {
        val size = inventory.size
        val contents = arrayOfNulls<ItemStack>(size)
        // Fill the inventory with the initial items.
        // If the sequence is too short, it will be filled with null items.
        // If the sequence is too long, the overflowing items will be ignored.
        initialize(key).take(size).forEachIndexed { index, item ->
            contents[index] = item
        }

        if(shift == 0) {
            inventory.contents = contents
            awaitCancellation()
        }

        coroutineScope {
            val contentList = contents.toMutableList()
            while (isActive) {
                inventory.contents = contentList.toTypedArray()
                delay(delay)
                Collections.rotate(contentList, shift)
            }
        }
    }
}
