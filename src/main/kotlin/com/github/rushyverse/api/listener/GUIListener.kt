package com.github.rushyverse.api.listener

import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.ClientManager
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent

public class GUIListener : Listener {

    private val clients: ClientManager by inject()

    @EventHandler
    public suspend fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val player = event.whoClicked
        val client = clients.getClient(player)
        client.gui()?.onClick(client, item, event)
    }

    @EventHandler
    public suspend fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        quitOpenedGUI(player)
    }

    @EventHandler
    public suspend fun onPlayerQuit(event: PlayerQuitEvent) {
        quitOpenedGUI(event.player)
    }

    /**
     * Quit the opened GUI for the player.
     * @param player Player to quit the GUI for.
     */
    private suspend fun quitOpenedGUI(player: HumanEntity) {
        clients.getClientOrNull(player)?.let {
            it.gui()?.close(it)
        }
    }

}
