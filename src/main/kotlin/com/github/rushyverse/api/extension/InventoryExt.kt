package com.github.rushyverse.api.extension

import com.github.rushyverse.api.item.ItemComparator
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
 * @param identifier Allows to identify if an item is equivalent to another.
 * @param handler Handler that will be called when the item is clicked.
 */
public fun AbstractInventory.registerClickEventOnItem(
    item: ItemStack,
    identifier: ItemComparator = ItemComparator.EQUALS,
    handler: InventoryCondition
): InventoryCondition {
    val condition = InventoryCondition { player, clickedSlot, clickType, result ->
        if (clickedSlot in slots && identifier.areSame(item, getItemStack(clickedSlot))) {
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
 * @param identifier Allows to identify if an item is equivalent to another.
 * @param handler Handler that will be called when the item is clicked.
 */
public fun AbstractInventory.setItemStack(
    slot: Int,
    item: ItemStack,
    identifier: ItemComparator = ItemComparator.EQUALS,
    handler: InventoryCondition,
): InventoryCondition {
    this.setItemStack(slot, item)
    return registerClickEventOnItem(item, identifier, handler)
}

/**
 * Know if the slot has no item.
 * @receiver Inventory checked.
 * @param slot Slot checked.
 * @return `true` if the slot is empty, `false` otherwise.
 */
public fun AbstractInventory.slotIsEmpty(slot: Int): Boolean = getItemStack(slot).isAir

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
 * @param identifier Allows to identify if an item is equivalent to another.
 * @param handler Handler that will be called when the item is clicked.
 * @return The created handler or `null` if there is no available slot.
 */
public fun AbstractInventory.addItemStack(
    item: ItemStack,
    identifier: ItemComparator = ItemComparator.EQUALS,
    handler: InventoryCondition,
): InventoryCondition? {
    return if (addItemStack(item)) {
        registerClickEventOnItem(item, identifier, handler)
    } else null
}

/**
 * Set the item on the specific slot to go back to the previous inventory.
 * @receiver Inventory where the item will be added.
 * @param slot Slot where the item will be set.
 * @param backInventory Redirection inventory.
 * @return Condition of interaction with the inventory.
 */
public fun AbstractInventory.setPreviousButton(slot: Int, backInventory: Inventory): InventoryCondition {
    return setItemChangeInventory(slot, backInventory, "< ")
}

/**
 * Set the item on the specific slot to go to the next inventory.
 * @receiver Inventory where the item will be added.
 * @param slot Slot where the item will be set.
 * @param nextInventory Redirection inventory.
 * @return Condition of interaction with the inventory.
 */
public fun AbstractInventory.setNextButton(slot: Int, nextInventory: Inventory): InventoryCondition {
    return setItemChangeInventory(slot, nextInventory, "> ")
}

/**
 * Set the item on the specific slot to go to the another inventory.
 * @receiver Inventory where the item will be added.
 * @param slot Slot where the item will be set.
 * @param otherInventory Redirection inventory.
 * @param textItem Text of the item.
 * @return Condition of interaction with the inventory.
 */
public fun AbstractInventory.setItemChangeInventory(
    slot: Int,
    otherInventory: Inventory,
    textItem: String
): InventoryCondition {
    val inventoryTitle = otherInventory.title
        .color(NamedTextColor.GRAY)
        .decoration(TextDecoration.ITALIC, true)
        .decoration(TextDecoration.BOLD, false)

    val item = ItemStack.of(Material.ARROW)
        .withDisplayName(
            Component.text(textItem)
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.BOLD, true)
                .append(inventoryTitle)
        )

    return setItemStack(slot, item) { player, _, _, result ->
        result.isCancel = true
        player.openInventory(otherInventory)
    }
}

/**
 * Add a close button to the inventory and a handler to close the inventory.
 * The item will override the item on the specific slot.
 * @receiver Inventory where the button will be added.
 * @param slot Slot where the item will be added.
 * @return The created handler.
 */
public fun AbstractInventory.setCloseButton(slot: Int): InventoryCondition {
    val closeItem = ItemStack.of(Material.BARRIER)
        .withDisplayName(Component.text("❌").color(NamedTextColor.RED))

    return setItemStack(slot, closeItem) { player, _, _, result ->
        result.isCancel = true
        player.closeInventory()
    }
}