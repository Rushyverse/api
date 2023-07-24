package com.github.rushyverse.api.listener

import com.github.rushyverse.api.API
import com.github.rushyverse.api.Plugin
import com.github.rushyverse.api.coroutine.exception.SilentCancellationException
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.*
import com.github.rushyverse.api.player.exception.ClientAlreadyExistsException
import kotlinx.coroutines.cancel
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger

/**
 * Main listener to manager instance of clients for the player entering and exiting the server.
 * @property plugin Java plugin.
 * @property clients Client manager to store and remove client instances.
 */
public class PlayerListener(
    private val plugin: Plugin
) : Listener {

    private val clients: ClientManager by inject(plugin.id)
    private val logger: Logger by inject(plugin.id)

    /**
     * Handle the join event to create and store a new client.
     * The client will be linked to the player.
     * @param event Event.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public suspend fun onJoin(event: PlayerJoinEvent) {
        println("Player join $event for plugin $plugin")
        val player = event.player
        val clientCreated = createAndSaveClient(player)
        val joinMessage = AtomicReference<Component?>(event.joinMessage())

        plugin.clientEvents.onJoin(clientCreated, joinMessage)

        event.joinMessage(joinMessage.get())
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
        val quitMessage = AtomicReference<Component?>(event.quitMessage())

        plugin.clientEvents.onQuit(client, quitMessage)

        client.cancel(SilentCancellationException("The player ${player.name} (${player.uniqueId}) left"))

        event.quitMessage(quitMessage.get())

        API.removeFastBoard(client.fastBoard)
    }
}