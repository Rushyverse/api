package com.github.rushyverse.api.gui

import com.github.rushyverse.api.extension.toTranslatedComponent
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.translation.Translator
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

/**
 * GUI where a new inventory is created for each viewer.
 * @property inventoryType InventoryType
 * @property title String
 * @property translator Translator
 */
public abstract class PersonalGUI(
    public val inventoryType: InventoryType,
    public val title: String
) : GUI() {

    private val translator: Translator by inject()

    private var inventories: MutableMap<Client, Inventory> = mutableMapOf()

    private val mutex = Mutex()

    override suspend fun open(client: Client) {
        requireOpen()

        val inventory = createInventory(client)

        mutex.withLock {
            val oldInventory = inventories[client]
            inventories[client] = inventory
            oldInventory
        }?.close()

        client.requirePlayer().openInventory(inventory)
    }

    override suspend fun createInventory(client: Client): Inventory {
        val player = client.requirePlayer()
        val translatedTitle = title.toTranslatedComponent(translator, client.lang().locale)
        return server.createInventory(player, inventoryType, translatedTitle).also {
            fill(client, it)
        }
    }

    /**
     * Fill the inventory with items for the client.
     * This function is called when the inventory is created.
     * @param client The client to fill the inventory for.
     * @param inventory The inventory to fill.
     */
    protected abstract suspend fun fill(client: Client, inventory: Inventory)

    override suspend fun close(client: Client): Boolean {
        return mutex.withLock { inventories.remove(client) }?.close() == 1
    }

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

    override fun close() {
        super.close()
        inventories.values.forEach(Inventory::close)
        inventories.clear()
    }
}
