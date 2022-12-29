package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.inventory.AbstractInventory
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.condition.InventoryCondition
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

/**
 * Range of slots.
 */
public val AbstractInventory.slots: IntRange
    get() = this.itemStacks.indices

/**
 * Remove the condition of interaction with the inventory.
 * @receiver Inventory.
 * @param condition Condition to remove.
 * @return `true` if the condition was removed, `false` otherwise.
 */
public fun AbstractInventory.removeCondition(condition: InventoryCondition): Boolean =
    inventoryConditions.remove(condition)

/**
 * Lock the position of all items in the inventory.
 * If a player tries to move an item, the item will not move.
 * @receiver Inventory.
 * @return Condition of interaction with the inventory.
 */
public fun AbstractInventory.lockItemPositions(): InventoryCondition {
    val condition = InventoryCondition { _, _, _, result ->
        result.isCancel = true
    }
    addInventoryCondition(condition)
    return condition
}

/**
 * Handle the click event on a specific slot.
 * @receiver Inventory where the handler will be used
 * @param slot Slot that should be clicked.
 * @param handler Handler that will be called when the slot is clicked.
 * @return Condition of interaction with the inventory.
 */
public fun AbstractInventory.registerClickEventOnSlot(slot: Int, handler: InventoryCondition): InventoryCondition {
    val condition = InventoryCondition { player, clickedSlot, clickType, result ->
        if (clickedSlot == slot) {
            handler.accept(player, clickedSlot, clickType, result)
        }
    }
    addInventoryCondition(condition)
    return condition
}

/**
 * Add a handler when the player click on the item.
 * @receiver Inventory.
 * @param item Item that should be clicked.
 * @param handler Handler that will be called when the item is clicked.
 */
public fun AbstractInventory.registerClickEventOnItem(
    item: ItemStack,
    handler: InventoryCondition
): InventoryCondition {
    val condition = InventoryCondition { player, clickedSlot, clickType, result ->
        if (clickedSlot in slots && item.isSimilar(getItemStack(clickedSlot))) {
            handler.accept(player, clickedSlot, clickType, result)
        }
    }
    addInventoryCondition(condition)
    return condition
}

/**
 * Add the item to the inventory on the specific slot and add a handler when the player click on the item.
 * @receiver Inventory.
 * @param slot Slot where the item will be added.
 * @param item Item that will be added.
 * @param handler Handler that will be called when the item is clicked.
 */
public fun AbstractInventory.setItemStack(slot: Int, item: ItemStack, handler: InventoryCondition): InventoryCondition {
    this.setItemStack(slot, item)
    return registerClickEventOnItem(item, handler)
}

/**
 * Know if the slot has no item.
 * @receiver Inventory checked.
 * @param slot Slot checked.
 * @return `true` if the slot is empty, `false` otherwise.
 */
public fun AbstractInventory.isEmpty(slot: Int): Boolean = getItemStack(slot).isAir

/**
 * Get the first available slot.
 * @receiver Inventory.
 * @return The slot number or `-1` if there is no available slot.
 */
public fun AbstractInventory.firstAvailableSlot(): Int = this.itemStacks.indexOfFirst { it.isAir }

/**
 * Add the item on the first available slot and add a handler when the player click on the item.
 * If there is no available slot, the item will not be added.
 * @receiver Inventory.
 * @param item Item that should be added.
 * @param handler Handler that will be called when the item is clicked.
 * @return The created handler or `null` if there is no available slot.
 */
public fun AbstractInventory.addItemStack(item: ItemStack, handler: InventoryCondition): InventoryCondition? {
    return if (addItemStack(item)) {
        registerClickEventOnItem(item, handler)
    } else null
}

/**
 * Set the item to back in the previous inventory on the specific slot.
 * @receiver Inventory where the item will be added.
 * @param slot Slot where the item will be added.
 * @param backInventory Redirection inventory.
 */
public fun AbstractInventory.setBackButton(slot: Int, backInventory: Inventory) {
    val backInvTitle = backInventory.title
    val backItem = ItemStack.of(Material.ARROW)
        .withDisplayName(
            Component.text("˿")
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .append(
                    backInvTitle
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true)
                        .decoration(TextDecoration.BOLD, false)
                )
        )

    setItemStack(slot, backItem) { player, _, _, result ->
        result.isCancel = true
        player.openInventory(backInventory)
    }
}

/**
 * Add a close button to the inventory and a handler to close the inventory.
 * @receiver Inventory where the button will be added.
 * @param slot Slot where the item will be added.
 */
public fun AbstractInventory.setCloseButton(slot: Int) {
    val closeItem = ItemStack.of(Material.BARRIER)
        .withDisplayName(Component.text("❌").color(NamedTextColor.RED))

    setItemStack(slot, closeItem) { player, _, _, result ->
        result.isCancel = true
        player.closeInventory()
    }
}