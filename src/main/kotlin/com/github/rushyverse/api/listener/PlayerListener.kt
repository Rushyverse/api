package com.github.rushyverse.api.listener

import com.github.rushyverse.api.Plugin
import com.github.rushyverse.api.coroutine.exception.SilentCancellationException
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.exception.ClientAlreadyExistsException
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
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
public class PlayerListener(
    private val plugin: Plugin
) : Listener {

    private val clients: ClientManager by inject(plugin.id)
    private val scoreboardManager: ScoreboardManager by inject()

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
     * Create a new instance of a client and store it into [clients].
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
     * The life cycle of the client will be canceled.
     * @param event Event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        println(scoreboardManager)
        scoreboardManager.remove(player)

        val client = clients.removeClient(player) ?: return
        client.cancel(SilentCancellationException("The player ${player.name} (${player.uniqueId}) left"))
    }
}
