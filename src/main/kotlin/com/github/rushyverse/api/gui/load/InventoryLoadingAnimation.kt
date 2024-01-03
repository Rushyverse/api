package com.github.rushyverse.api.gui.load

import org.bukkit.inventory.Inventory

/**
 * Animate an inventory while it is being loaded in the background.
 * @param T Type of the key.
 */
public fun interface InventoryLoadingAnimation<T> {

    /**
     * Animate the inventory while the real inventory is being loaded in the background.
     * @param key Key to animate the inventory for.
     * @param inventory Inventory to animate.
     * @return A job that can be cancelled to stop the animation.
     */
    public suspend fun loading(key: T, inventory: Inventory)
}
