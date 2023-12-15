package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * GUI where a new inventory is created for each viewer.
 */
public abstract class PersonalGUI : GUI() {

    private var inventories: MutableMap<Client, Inventory> = mutableMapOf()

    private val mutex = Mutex()

    override suspend fun openGUI(client: Client): Boolean {
        val inventory = createInventory(client)

        mutex.withLock { inventories[client] }?.close()

        val player = client.requirePlayer()
        player.openInventory(inventory)

        mutex.withLock { inventories[client] = inventory }
        return true
    }

    /**
     * Create the inventory for the client.
     * Will translate the title and fill the inventory.
     * @param client The client to create the inventory for.
     * @return The inventory for the client.
     */
    private suspend fun createInventory(client: Client): Inventory {
        val player = client.requirePlayer()
        return createInventory(player, client).also {
            fill(client, it)
        }
    }

    /**
     * Create the inventory for the client.
     * This function is called when the [owner] wants to open the inventory.
     * @param owner Player who wants to open the inventory.
     * @param client The client to create the inventory for.
     * @return The inventory for the client.
     */
    protected abstract fun createInventory(owner: InventoryHolder, client: Client): Inventory

    /**
     * Fill the inventory with items for the client.
     * This function is called when the inventory is created.
     * @param client The client to fill the inventory for.
     * @param inventory The inventory to fill.
     */
    protected abstract suspend fun fill(client: Client, inventory: Inventory)

    override suspend fun viewers(): List<HumanEntity> {
        return mutex.withLock {
            inventories.values.flatMap(Inventory::getViewers)
        }
    }

    override suspend fun contains(client: Client): Boolean {
        return mutex.withLock {
            inventories.containsKey(client)
        }
    }

    override suspend fun hasInventory(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.contains(inventory)
        }
    }

    override suspend fun getInventory(client: Client): Inventory? {
        return mutex.withLock {
            inventories[client]
        }
    }

    override suspend fun close(client: Client, closeInventory: Boolean): Boolean {
        return mutex.withLock { inventories.remove(client) }?.run {
            if (closeInventory) {
                client.player?.closeInventory()
            }
            true
        } == true
    }

    override suspend fun close() {
        super.close()
        mutex.withLock {
            inventories.values.forEach(Inventory::close)
            inventories.clear()
        }
    }
}
