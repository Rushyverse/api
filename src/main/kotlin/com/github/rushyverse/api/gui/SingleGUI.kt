package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server.
 * @property viewers List of viewers.
 */
public abstract class SingleGUI(
    protected val plugin: Plugin,
    loadingAnimation: InventoryLoadingAnimation<Unit>? = null
) : GUI<Unit>(
    loadingAnimation = loadingAnimation,
    initialNumberInventories = 1
) {

    public companion object {
        /**
         * Unique key for the GUI.
         * This GUI is shared by all the players, so the key is the same for all of them.
         * That allows creating a unique inventory.
         */
        private val KEY: Unit get() = Unit
    }

    override suspend fun getKey(client: Client) {
        return KEY
    }

    override suspend fun fillScope(key: Unit): CoroutineScope {
        val scope = plugin.scope
        return scope + SupervisorJob(scope.coroutineContext.job)
    }

    override suspend fun createInventory(key: Unit): Inventory {
        return createInventory()
    }

    /**
     * Create the inventory.
     * @return New created inventory.
     */
    protected abstract suspend fun createInventory(): Inventory

    /**
     * Update the inventory.
     * If the inventory is not loaded, the inventory will be updated.
     * If the inventory is loading, the inventory will be updated if [interruptLoading] is true.
     *
     * Call [getItems] to get the new items to fill the inventory.
     * @param interruptLoading If true and if the inventory is loading, the loading will be interrupted
     * to start a new loading animation.
     * @return True if the inventory was updated, false otherwise.
     */
    public suspend fun update(interruptLoading: Boolean = false): Boolean {
        return super.update(KEY, interruptLoading)
    }

    override suspend fun closeClient(client: Client, closeInventory: Boolean): Boolean {
        return if (closeInventory && contains(client)) {
            client.player?.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            true
        } else false
    }

    override fun getItems(key: Unit, size: Int): Flow<ItemStackIndex> {
        return getItems(size)
    }

    /**
     * @see getItems(key, size)
     */
    protected abstract fun getItems(size: Int): Flow<ItemStackIndex>
}
