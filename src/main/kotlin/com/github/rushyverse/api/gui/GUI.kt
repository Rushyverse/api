package com.github.rushyverse.api.gui

import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.Client
import java.io.Closeable
import org.bukkit.Server
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server.
 * @property manager Manager to register or unregister the GUI.
 * @property isClosed If true, the GUI is closed; otherwise it is open.
 */
public abstract class GUI : Closeable {

    protected val server: Server by inject()

    private val manager: GUIManager by inject()

    public var isClosed: Boolean = false
        protected set

    init {
        register()
    }

    /**
     * Create the inventory of the GUI.
     * @return The inventory of the GUI.
     */
    protected abstract suspend fun createInventory(client: Client): Inventory

    /**
     * Open the inventory for the player.
     * @param client Client to open the inventory for.
     */
    public abstract suspend fun open(client: Client)

    /**
     * Action to do when the client clicks on an item in the inventory.
     * @param client Client who clicked.
     * @param clickedItem Item clicked by the client.
     * @param event Event of the click.
     */
    public abstract suspend fun onClick(client: Client, clickedItem: ItemStack, event: InventoryClickEvent)

    /**
     * Remove the client has a viewer of the GUI.
     * @param client Client to close the GUI for.
     * @param closeInventory If true, the interface will be closed, otherwise it will be kept open.
     * @return True if the inventory was closed, false otherwise.
     */
    public abstract suspend fun close(client: Client, closeInventory: Boolean = true): Boolean

    /**
     * Close the inventory.
     * The inventory will be closed for all the viewers.
     * The GUI will be removed from the listener and the [onClick] function will not be called anymore.
     */
    public override fun close() {
        isClosed = true
        unregister()
    }

    /**
     * Verify that the GUI is open.
     * If the GUI is closed, throw an exception.
     */
    protected fun requireOpen() {
        require(!isClosed) { "Cannot use a closed GUI" }
    }

    /**
     * Get the viewers of the GUI.
     * @return List of viewers.
     */
    public abstract suspend fun viewers(): List<HumanEntity>

    /**
     * Check if the GUI contains the player.
     * @param client Client to check.
     * @return True if the GUI contains the player, false otherwise.
     */
    public abstract suspend fun contains(client: Client): Boolean

    /**
     * Register the GUI to the listener.
     * @return True if the GUI was registered, false otherwise.
     */
    protected fun register(): Boolean {
        return manager.add(this)
    }

    /**
     * Unregister the GUI from the listener.
     * @return True if the GUI was unregistered, false otherwise.
     */
    protected fun unregister(): Boolean {
        return manager.remove(this)
    }

}
