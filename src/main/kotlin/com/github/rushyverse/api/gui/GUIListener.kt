package com.github.rushyverse.api.gui

import com.github.rushyverse.api.Plugin
import com.github.rushyverse.api.extension.event.cancel
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.ClientManager
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener for GUI events.
 * @property clients Manager of clients.
 */
public class GUIListener(plugin: Plugin) : Listener {

    private val clients: ClientManager by inject(plugin.id)

    /**
     * Called when a player clicks on an item in an inventory.
     * If the click is detected in a GUI, the event is cancelled and the GUI is notified.
     * @param event Event of the click.
     */
    @EventHandler
    public suspend fun onInventoryClick(event: InventoryClickEvent) {
        if (event.isCancelled) return

        val item = event.currentItem
        if (item == null || item.type == Material.AIR) return

        val player = event.whoClicked
        val client = clients.getClient(player)
        val gui = client.gui() ?: return

        // The item in a GUI is not supposed to be moved
        event.cancel()
        gui.onClick(client, item, event)
    }

    /**
     * Called when a player closes an inventory.
     * If the inventory is a GUI, the GUI is notified that it is closed for this player.
     * @param event Event of the close.
     */
    @EventHandler
    public suspend fun onInventoryClose(event: InventoryCloseEvent) {
        quitOpenedGUI(event.player)
    }

    /**
     * Called when a player quits the server.
     * If the player has a GUI opened, the GUI is notified that it is closed for this player.
     * @param event Event of the quit.
     */
    @EventHandler
    public suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        quitOpenedGUI(event.player)
    }

    /**
     * Quit the opened GUI for the player.
     * @param player Player to quit the GUI for.
     */
    private suspend fun quitOpenedGUI(player: HumanEntity) {
        val client = clients.getClientOrNull(player)
        val gui = client?.gui() ?: return
        // We don't close the inventory because it is closing due to event.
        gui.close(client, false)
    }

}
