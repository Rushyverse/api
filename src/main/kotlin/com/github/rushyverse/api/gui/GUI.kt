package com.github.rushyverse.api.gui

import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.CancellationException
import mu.KotlinLogging
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

private val logger = KotlinLogging.logger {}

/**
 * Exception concerning the GUI.
 */
public open class GUIException(message: String) : CancellationException(message)

/**
 * Exception thrown when the GUI is closed.
 */
public class GUIClosedException(message: String) : GUIException(message)

/**
 * Exception thrown when the GUI is closed for a specific client.
 * @property client Client for which the GUI is closed.
 */
public class GUIClosedForClientException(public val client: Client) :
    GUIException("GUI closed for client ${client.playerUUID}")

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server.
 * @property manager Manager to register or unregister the GUI.
 * @property isClosed If true, the GUI is closed; otherwise it is open.
 */
public abstract class GUI {

    protected val server: Server by inject()

    private val manager: GUIManager by inject()

    public var isClosed: Boolean = false
        protected set

    /**
     * Open the GUI for the client only if the GUI is not closed.
     * If the client has another GUI opened, close it.
     * If the client has the same GUI opened, do nothing.
     * @param client Client to open the GUI for.
     * @return True if the GUI was opened, false otherwise.
     */
    public suspend fun open(client: Client): Boolean {
        requireOpen()

        val gui = client.gui()
        if (gui === this) return false
        // If the client has another GUI opened, close it.
        gui?.close(client, true)

        val player = client.player
        if (player === null) {
            logger.warn { "Cannot open inventory for player ${client.playerUUID}: player is null" }
            return false
        }
        // If the player is dead, do not open the GUI because the interface cannot be shown to the player.
        if (player.isDead) return false

        return openGUI(client)
    }

    /**
     * Open the GUI for the client.
     * Called by [open] after all the checks.
     * @param client Client to open the GUI for.
     * @return True if the GUI was opened, false otherwise.
     */
    protected abstract suspend fun openGUI(client: Client): Boolean

    /**
     * Action to do when the client clicks on an item in the inventory.
     * @param client Client who clicked.
     * @param clickedItem Item clicked by the client cannot be null or [AIR][Material.AIR]
     * @param clickedInventory Inventory where the click was detected.
     * @param event Event of the click.
     */
    public abstract suspend fun onClick(
        client: Client,
        clickedInventory: Inventory,
        clickedItem: ItemStack,
        event: InventoryClickEvent
    )

    /**
     * Remove the client has a viewer of the GUI.
     * @param client Client to close the GUI for.
     * @param closeInventory If true, the interface will be closed, otherwise it will be kept open.
     * @return True if the inventory was closed, false otherwise.
     */
    public abstract suspend fun close(client: Client, closeInventory: Boolean = true): Boolean

    /**
     * Check if the GUI contains the inventory.
     * @param inventory Inventory to check.
     * @return True if the GUI contains the inventory, false otherwise.
     */
    public abstract suspend fun hasInventory(inventory: Inventory): Boolean

    /**
     * Check if the inventory is loading.
     * @param inventory Inventory to check.
     * @return True if the inventory is loading, false otherwise.
     */
    public abstract suspend fun isInventoryLoading(inventory: Inventory): Boolean

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
     * Close the inventory.
     * The inventory will be closed for all the viewers.
     * The GUI will be removed from the listener and the [onClick] function will not be called anymore.
     */
    public open suspend fun close() {
        isClosed = true
        unregister()
    }

    /**
     * Verify that the GUI is open.
     * If the GUI is closed, throw an exception.
     */
    private fun requireOpen() {
        if (isClosed) throw GUIClosedException("Cannot use a closed GUI")
    }

    /**
     * Register the GUI to the listener.
     * @return True if the GUI was registered, false otherwise.
     */
    public suspend fun register(): Boolean {
        requireOpen()
        return manager.add(this)
    }

    /**
     * Unregister the GUI from the listener.
     * Should be called when the GUI is closed with [close].
     * @return True if the GUI was unregistered, false otherwise.
     */
    protected suspend fun unregister(): Boolean {
        return manager.remove(this)
    }

}
