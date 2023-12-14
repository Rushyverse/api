package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import net.kyori.adventure.text.Component
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property inventoryType Type of the inventory.
 * @property title Title of the inventory.
 * @property server Server
 * @property listener Listener to listen to the clicks on the inventory.
 * @property viewers List of viewers.
 * @property inventory Inventory shared by all the viewers.
 */
public abstract class SharedGUI(
    public val inventoryType: InventoryType,
    public val title: Component
): GUI() {

    private var inventory: Inventory? = null

    override suspend fun createInventory(client: Client): Inventory {
        return server.createInventory(null, inventoryType, title).also {
            fill(it)
        }
    }

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
        require(!isClosed) { "The GUI is closed" }

        return inventory ?: createInventory(client).also {
            inventory = it
            register()
        }
    }

    override suspend fun close(client: Client): Boolean {
        val player = client.requirePlayer()
        if(player.openInventory.topInventory == inventory) {
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
