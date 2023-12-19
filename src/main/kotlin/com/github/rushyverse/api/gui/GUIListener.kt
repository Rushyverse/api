package com.github.rushyverse.api.gui

import com.github.rushyverse.api.Plugin
import com.github.rushyverse.api.extension.event.cancel
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.ClientManager
import com.github.shynixn.mccoroutine.bukkit.callSuspendingEvent
import kotlinx.coroutines.joinAll
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.block.BlockFace
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

/**
 * Listener for GUI events.
 * @property clients Manager of clients.
 */
public class GUIListener(private val plugin: Plugin) : Listener {

    private val clients: ClientManager by inject(plugin.id)

    private val server: Server by inject()

    /**
     * Called when a player clicks on an item in an inventory.
     * If the click is detected in a GUI, the event is cancelled and the GUI is notified.
     * @param event Event of the click.
     */
    @EventHandler
    public suspend fun onInventoryClick(event: InventoryClickEvent) {
        if (event.isCancelled) return

        val item = event.currentItem
        // If the item is null or air, we should ignore the click
        if (item == null || item.type == Material.AIR) return

        // If the click is not in an inventory, this is not a GUI click
        val clickedInventory = event.clickedInventory ?: return

        val player = event.whoClicked
        if (clickedInventory is PlayerInventory) {
            handleClickOnPlayerInventory(player as Player, item, event)
        } else {
            handleClickOnGUI(player, clickedInventory, item, event)
        }
    }

    /**
     * Called when a player clicks on an item in an inventory.
     * @param player Player who clicked.
     * @param clickedInventory Inventory where the click was detected.
     * @param item Item that was clicked.
     * @param event Event of the click.
     */
    private suspend fun handleClickOnGUI(
        player: HumanEntity,
        clickedInventory: Inventory,
        item: ItemStack,
        event: InventoryClickEvent
    ) {
        val client = clients.getClient(player)
        val gui = client.gui() ?: return
        if (!gui.hasInventory(clickedInventory)) {
            return
        }

        // The item in a GUI is not supposed to be moved
        event.cancel()
        gui.onClick(client, clickedInventory, item, event)
    }

    /**
     * Called when a player clicks on an item in his inventory.
     * To trigger the action linked to the clicked item,
     * we produce a PlayerInteractEvent to simulate a right click with the item.
     * @param player Player who clicked.
     * @param item Item that was clicked.
     */
    private suspend fun handleClickOnPlayerInventory(player: Player, item: ItemStack, event: InventoryClickEvent) {
        event.cancel()

        val rightClickWithItemEvent = createRightClickWithItemEvent(player, item)
        server.pluginManager.callSuspendingEvent(rightClickWithItemEvent, plugin).joinAll()
    }

    /**
     * Create a PlayerInteractEvent to simulate a right click with an item.
     * @param player Player who clicked.
     * @param item Item that was clicked.
     * @return The new event.
     */
    private fun createRightClickWithItemEvent(
        player: Player,
        item: ItemStack
    ) = PlayerInteractEvent(
        player,
        Action.RIGHT_CLICK_AIR,
        item,
        null,
        BlockFace.NORTH // random value not null
    )

    /**
     * Called when a player closes an inventory.
     * If the inventory is a GUI, the GUI is notified that it is closed for this player.
     * @param event Event of the close.
     */
    @EventHandler
    public suspend fun onInventoryClose(event: InventoryCloseEvent) {
        val client = clients.getClientOrNull(event.player)
        val gui = client?.gui() ?: return
        // We don't close the inventory because it is closing due to event.
        // That avoids an infinite loop of events and consequently a stack overflow.
        gui.close(client, false)
    }

}
