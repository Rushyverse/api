package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory

/**
 * GUI where a new inventory is created for each key used.
 * @param T Type of the key.
 * @property inventories Map of inventories for each key.
 * @property mutex Mutex to process thread-safe operations.
 */
public abstract class DedicatedGUI<T> : GUI() {

    protected var inventories: MutableMap<T, Inventory> = mutableMapOf()

    protected val mutex: Mutex = Mutex()

    override suspend fun openGUI(client: Client): Boolean {
        val key = getKey(client)
        val inventory = getOrCreateInventory(key)

        val player = client.requirePlayer()
        player.openInventory(inventory)

        return true
    }

    /**
     * Get the inventory for the key.
     * If the inventory does not exist, create it.
     * @param key Key to get the inventory for.
     * @return The inventory for the key.
     */
    private suspend fun getOrCreateInventory(key: T): Inventory {
        return mutex.withLock {
            inventories[key] ?: createInventory(key).also {
                inventories[key] = it
                fill(key, it)
            }
        }
    }

    /**
     * Get the key linked to the client to interact with the GUI.
     * @param client Client to get the key for.
     * @return The key.
     */
    protected abstract suspend fun getKey(client: Client): T

    /**
     * Create the inventory for the key.
     * @param key Key to create the inventory for.
     * @return New created inventory.
     */
    protected abstract suspend fun createInventory(key: T): Inventory

    /**
     * Fill the inventory for the key.
     * @param key Key to fill the inventory for.
     * @param inventory Inventory to fill.
     */
    protected abstract suspend fun fill(key: T, inventory: Inventory)

    override suspend fun hasInventory(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.contains(inventory)
        }
    }

    override suspend fun viewers(): List<HumanEntity> {
        return mutex.withLock {
            unsafeViewers()
        }
    }

    /**
     * Get the viewers of the inventory.
     * This function is not thread-safe.
     * @return The viewers of the inventory.
     */
    protected open fun unsafeViewers(): List<HumanEntity> {
        return inventories.values.flatMap(Inventory::getViewers)
    }

    override suspend fun contains(client: Client): Boolean {
        return mutex.withLock {
            unsafeContains(client)
        }
    }

    /**
     * Check if the GUI contains the client.
     * This function is not thread-safe.
     * @param client Client to check.
     * @return True if the GUI contains the client, false otherwise.
     */
    protected open fun unsafeContains(client: Client): Boolean {
        val player = client.player ?: return false
        return inventories.values.any { it.viewers.contains(player) }
    }

    override suspend fun close() {
        super.close()
        mutex.withLock {
            inventories.values.forEach(Inventory::close)
            inventories.clear()
        }
    }
}
