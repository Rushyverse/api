package io.github.distractic.bukkit.api.player

import io.github.distractic.bukkit.api.delegate.DelegatePlayer
import io.github.distractic.bukkit.api.player.exception.PlayerNotFoundException
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import java.util.*

/**
 * Client to store and manage data about player.*
 * @property playerUUID Player's uuid.
 * @property player Player linked to the client.
 */
public open class Client(pluginId: String, public val playerUUID: UUID, coroutineScope: CoroutineScope) : CoroutineScope by coroutineScope {

    public val player: Player? by DelegatePlayer(pluginId, playerUUID)

    /**
     * Retrieve the instance of player.
     * If the player is not found from the server, thrown an exception.
     * @return The instance of player.
     */
    public fun requirePlayer(): Player = player ?: throw PlayerNotFoundException("The player cannot be retrieved from the server")
}