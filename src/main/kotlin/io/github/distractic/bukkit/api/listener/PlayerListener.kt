package io.github.distractic.bukkit.api.listener

import io.github.distractic.bukkit.api.Plugin
import io.github.distractic.bukkit.api.coroutine.exception.SilentCancellationException
import io.github.distractic.bukkit.api.koin.inject
import io.github.distractic.bukkit.api.player.Client
import io.github.distractic.bukkit.api.player.ClientManager
import io.github.distractic.bukkit.api.player.exception.ClientAlreadyExistsException
import kotlinx.coroutines.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Main listener to manager instance of clients for the player entering and exiting the server.
 * @property plugin Java plugin.
 * @property clients Client manager to store and remove client instances.
 */
public class PlayerListener(private val plugin: Plugin) : Listener {

    private val clients: ClientManager by inject(plugin.id)

    /**
     * Handle the join event to create and store a new client.
     * The client will be linked to the player.
     * @param event Event.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public suspend fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        createAndSaveClient(player)
    }

    /**
     * Create a new instance of client and store it into [clients].
     * If a client is already found for the player, throw an exception.
     * @param player Player linked to the client.
     * @return The instance of the client.
     */
    private suspend fun createAndSaveClient(player: Player): Client {
        val client = plugin.createClient(player)
        if (clients.putIfAbsent(player, client) != null) {
            throw ClientAlreadyExistsException("A client linked to the player already exists.")
        }
        return client
    }

    /**
     * Handle the quit event to remove the client linked to the player leaving.
     * The life cycle of the client will be cancelled.
     * @param event Event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val client = clients.removeClient(player) ?: return
        client.cancel(SilentCancellationException("The player ${player.uniqueId} left"))
    }
}