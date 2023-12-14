package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import net.kyori.adventure.text.Component
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server
 * @property viewers List of viewers.
 * @property inventory Inventory shared by all the viewers.
 */
public abstract class SharedGUI : GUI() {

    private var inventory: Inventory? = null

    public override suspend fun open(client: Client) {
        val inventory = getOrCreateInventory(client)
        client.requirePlayer().openInventory(inventory)
    }

    /**
     * Get the inventory of the GUI.
     * If the inventory is not created, create it.
     * @return The inventory of the GUI.
     */
    private suspend fun getOrCreateInventory(client: Client): Inventory {
        requireOpen()
        return inventory ?: createInventory(client).also {
            inventory = it
        }
    }

    override suspend fun createInventory(client: Client): Inventory {
        return createInventory().also {
            fill(it)
        }
    }

    /**
     * Create the inventory of the GUI.
     * This function is called only once when the inventory is created.
     * @return A new inventory.
     */
    protected abstract fun createInventory(): Inventory

    override suspend fun close(client: Client, closeInventory: Boolean): Boolean {
        val player = client.requirePlayer()
        if (closeInventory && player.openInventory.topInventory == inventory) {
            player.closeInventory()
            return true
        }
        return false
    }

    override suspend fun viewers(): List<HumanEntity> {
        return inventory?.viewers ?: emptyList()
    }

    override suspend fun contains(client: Client): Boolean {
        return client.player?.let { it in viewers() } == true
    }

    override fun close() {
        super.close()
        inventory?.close()
        inventory = null
    }

    /**
     * Fill the inventory with items for the client.
     * This function is called when the inventory is created.
     * @param inventory The inventory to fill.
     */
    protected abstract suspend fun fill(inventory: Inventory)
}
