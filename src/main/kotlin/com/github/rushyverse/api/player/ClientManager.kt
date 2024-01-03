package com.github.rushyverse.api.player

import com.github.rushyverse.api.player.exception.ClientNotFoundException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.HumanEntity

/**
 * Get a client from the player instance.
 * @param player Player.
 * @return The client linked to a player.
 */
public suspend inline fun <reified T : Client> ClientManager.getTypedClient(player: HumanEntity): T =
    getClient(player) as T

/**
 * Get a client from the key linked to a player.
 * @param key Key to find a client.
 * @return The client linked to a key.
 */
public suspend inline fun <reified T : Client> ClientManager.getTypedClient(key: String): T = getClient(key) as T

/**
 * Get a client from the player instance.
 * @param player Player.
 * @return The client linked to a player, `null` if not found.
 */
public suspend inline fun <reified T : Client> ClientManager.getTypedClientOrNull(player: HumanEntity): T? =
    getClientOrNull(player) as T?

/**
 * Get a client from the key linked to a player.
 * @param key Key to find a client.
 * @return The client linked to a player, `null` if not found.
 */
public suspend inline fun <reified T : Client> ClientManager.getTypedClientOrNull(key: String): T? =
    getClientOrNull(key) as T?

/**
 * Manage the existing client present in the server.
 * @property clients Synchronized mutable map of clients as value and name of player as a key.
 */
public interface ClientManager {

    public val clients: Map<String, Client>

    /**
     * Put a new client in the server.
     * @param client New client added.
     * @return The previous value associated with a key, or null there is none.
     */
    public suspend fun put(player: HumanEntity, client: Client): Client?

    /**
     * Put a new client in the server if no client is linked to the player.
     * @param client New client added.
     * @return The previous value associated with a key, or null there is none.
     */
    public suspend fun putIfAbsent(player: HumanEntity, client: Client): Client?

    /**
     * Remove a client from the server by a Player.
     * @param player Player linked to a Client.
     * @return The client that was removed null otherwise.
     */
    public suspend fun removeClient(player: HumanEntity): Client?

    /**
     * Get a client from the player instance.
     * @param player Player.
     * @return The client linked to a player.
     */
    public suspend fun getClient(player: HumanEntity): Client

    /**
     * Get a client from the key linked to a player.
     * @param key Key to find a client.
     * @return The client linked to a key.
     */
    public suspend fun getClient(key: String): Client

    /**
     * Get a client from the player instance.
     * @param player Player.
     * @return The client linked to a player, `null` if not found.
     */
    public suspend fun getClientOrNull(player: HumanEntity): Client?

    /**
     * Get a client from the key linked to a player.
     * @param key Key to find a client.
     * @return The client linked to a player, `null` if not found.
     */
    public suspend fun getClientOrNull(key: String): Client?

    /**
     * Check if a client is linked to a player.
     * @param player Player.
     * @return `true` if there is a client for the player, `false` otherwise.
     */
    public suspend fun contains(player: HumanEntity): Boolean
}

/**
 * Manage the existing client present in the server.
 * The clients are stored with the name of the player.
 * @property _clients Synchronized mutable map of clients as value and name of player as a key.
 */
public class ClientManagerImpl : ClientManager {

    private val mutex = Mutex()

    /**
     * All clients in server linked by the name of player.
     */
    private val _clients = mutableMapOf<String, Client>()

    override val clients: Map<String, Client> = _clients

    override suspend fun put(
        player: HumanEntity,
        client: Client
    ): Client? = mutex.withLock {
        _clients.put(player.name, client)
    }

    override suspend fun putIfAbsent(
        player: HumanEntity,
        client: Client
    ): Client? = mutex.withLock {
        _clients.putIfAbsent(getKey(player), client)
    }

    override suspend fun removeClient(player: HumanEntity): Client? = mutex.withLock {
        _clients.remove(getKey(player))
    }

    override suspend fun getClient(player: HumanEntity): Client = getClient(getKey(player))

    override suspend fun getClient(key: String): Client =
        getClientOrNull(key) ?: throw ClientNotFoundException("No client is linked to the name [$key]")

    override suspend fun getClientOrNull(player: HumanEntity): Client? = getClientOrNull(getKey(player))

    override suspend fun getClientOrNull(key: String): Client? = mutex.withLock {
        _clients[key]
    }

    /**
     * Key use for the Map
     * @param p Player that has the key
     * @return The key for the Map
     */
    private fun getKey(p: HumanEntity): String = p.name

    override suspend fun contains(player: HumanEntity): Boolean = mutex.withLock {
        _clients.containsKey(getKey(player))
    }

}
