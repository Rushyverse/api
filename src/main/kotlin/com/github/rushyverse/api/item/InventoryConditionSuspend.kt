package com.github.rushyverse.api.item

import com.github.rushyverse.api.coroutine.MinestomSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minestom.server.entity.Player
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.inventory.condition.InventoryCondition
import net.minestom.server.inventory.condition.InventoryConditionResult

/**
 * Converts this [InventoryConditionSuspend] to a [InventoryCondition].
 * When the inventory condition is called, the code will be executed in the current thread.
 * However, when the first suspension point is reached, the code will be executed in a thread obtained using [coroutineScope].
 * @receiver Inventory condition suspendable.
 * @param coroutineScope Coroutine scope where the inventory condition will be handled.
 * @return The native inventory condition.
 */
public fun InventoryConditionSuspend.asNative(coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope) : InventoryCondition {
    return InventoryCondition { player, clickedSlot, clickType, result ->
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
           this@asNative.accept(player, clickedSlot, clickType, result)
        }
    }

}

/**
 * Allows to handle the click event on a specific slot in a coroutine context.
 */
public fun interface InventoryConditionSuspend {

    /**
     * Handler of the click event on a specific slot in a coroutine scope.
     * According to the implementation, [inventoryConditionResult] can be ignored.
     * If the value of [inventoryConditionResult] is changed before the suspension point, the value will be used.
     * If the value of [inventoryConditionResult] is changed after the suspension point, the value could be ignored.
     * @param player Player who clicked in the inventory.
     * @param slot Slot clicked, can be -999 if the click is out of the inventory.
     * @param clickType Click type.
     * @param inventoryConditionResult Result of this callback.
     */
    public suspend fun accept(player: Player, slot: Int, clickType: ClickType, inventoryConditionResult: InventoryConditionResult)

}