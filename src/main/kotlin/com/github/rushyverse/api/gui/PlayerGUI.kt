package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.withLock
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * GUI where a new inventory is created for each player.
 * An inventory is created when the player opens the GUI and he is not sharing the GUI with another player.
 */
public abstract class PlayerGUI(
    loadingAnimation: InventoryLoadingAnimation<Client>? = null
) : GUI<Client>(loadingAnimation = loadingAnimation) {

    override suspend fun getKey(client: Client): Client {
        return client
    }

    override suspend fun fillScope(key: Client): CoroutineScope {
        return key + SupervisorJob(key.coroutineContext.job)
    }

    override suspend fun updateClient(client: Client, interruptLoading: Boolean): Boolean {
        // Little optimization to avoid checking if the client is contained in map's values.
        return super.update(client, interruptLoading)
    }

    override fun unsafeContains(client: Client): Boolean {
        // Little optimization to avoid checking if the client is contained in map's values.
        return inventories.containsKey(client)
    }

    /**
     * Create the inventory for the client.
     * Will translate the title and fill the inventory.
     * @param key The client to create the inventory for.
     * @return The inventory for the client.
     */
    override suspend fun createInventory(key: Client): Inventory {
        val player = key.requirePlayer()
        return createInventory(player, key)
    }

    /**
     * Create the inventory for the client.
     * This function is called when the [owner] wants to open the inventory.
     * @param owner Player who wants to open the inventory.
     * @param client The client to create the inventory for.
     * @return The inventory for the client.
     */
    protected abstract suspend fun createInventory(owner: InventoryHolder, client: Client): Inventory

    override suspend fun closeClient(client: Client, closeInventory: Boolean): Boolean {
        val (inventory, job) = mutex.withLock { inventories.remove(client) } ?: return false

        job.cancel(GUIClosedForClientException(client))
        job.join()

        if (closeInventory) {
            // Call out of the lock to avoid slowing down the mutex.
            inventory.close()
        }

        return true
    }
}
