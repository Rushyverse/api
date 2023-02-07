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

public fun InventoryConditionSuspend.asNative(coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope) : InventoryCondition {
    return InventoryCondition { player, clickedSlot, clickType, result ->
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
           this@asNative.accept(player, clickedSlot, clickType, result)
        }
    }

}

public fun interface InventoryConditionSuspend {

    public suspend fun accept(player: Player, slot: Int, clickType: ClickType, inventoryConditionResult: InventoryConditionResult)
}