package com.github.rushyverse.api.player

import com.github.rushyverse.api.player.exception.ClientNotFoundException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player

/**
 * Manage the existing client present in the server.
 * The clients are stored with the name of the player.
 * @property _clients Synchronized mutable map of client as value and name of player as key.
 */
public class ClientManagerImpl : ClientManager {

    private val mutex = Mutex()

    /**
     * All clients in server linked by the name of player.
     */
    private val _clients = mutableMapOf<String, Client>()

    override val clients: Map<String, Client> = _clients

    override suspend fun put(
        player: Player,
        client: Client
    ): Client? = mutex.withLock {
        _clients.put(player.name, client)
    }

    override suspend fun putIfAbsent(
        player: Player,
        client: Client
    ): Client? = mutex.withLock {
        _clients.putIfAbsent(getKey(player), client)
    }

    override suspend fun removeClient(player: Player): Client? = mutex.withLock {
        _clients.remove(getKey(player))
    }

    override suspend fun getClient(player: Player): Client = getClient(getKey(player))

    override suspend fun getClient(key: String): Client =
        getClientOrNull(key) ?: throw ClientNotFoundException("No client is linked to the name [$key]")

    override suspend fun getClientOrNull(player: Player): Client? = getClientOrNull(getKey(player))

    override suspend fun getClientOrNull(key: String): Client? = mutex.withLock {
        _clients[key]
    }

    /**
     * Key use for the Map
     * @param p Player that has the key
     * @return The key for the Map
     */
    private fun getKey(p: Player): String = p.name

    /**
     * Check if a client is linked to a player.
     * @param player Player
     * @return `true` if there is a client for the player, `false` otherwise.
     */
    override suspend fun contains(player: Player): Boolean = mutex.withLock {
        _clients.containsKey(getKey(player))
    }

}