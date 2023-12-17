package com.github.rushyverse.api.gui.load

import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

public class ShiftInventoryLoadingAnimation<T>(
    private val initialize: (T) -> Sequence<ItemStack>,
    private val shift: Int = 1,
    private val delay: Duration = 100.milliseconds,
) : InventoryLoadingAnimation<T> {

    override fun loading(scope: CoroutineScope, key: T, inventory: Inventory): Job {
        return scope.launch {
            val size = inventory.size
            val contents = arrayOfNulls<ItemStack>(size)
            // Fill the inventory with the initial items.
            // If the sequence is too short, it will be filled with null items.
            // If the sequence is too long, the overflowing items will be ignored.
            initialize(key).take(size).forEachIndexed { index, item ->
                contents[index] = item
            }

            val contentList = contents.toMutableList()
            while (isActive) {
                inventory.contents = contentList.toTypedArray()
                delay(delay)
                Collections.rotate(contentList, shift)
            }
        }
    }
}
