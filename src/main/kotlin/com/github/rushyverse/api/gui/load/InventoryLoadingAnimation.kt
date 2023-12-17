package com.github.rushyverse.api.gui.load

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bukkit.inventory.Inventory

/**
 * Animate an inventory while it is being loaded in the background.
 * @param T Type of the key.
 */
public fun interface InventoryLoadingAnimation<T> {

    /**
     * Animate the inventory while the real inventory is being loaded in the background.
     * @param key Key to animate the inventory for.
     * @return A job that can be cancelled to stop the animation.
     */
    public fun loading(scope: CoroutineScope, key: T, inventory: Inventory): Job
}
