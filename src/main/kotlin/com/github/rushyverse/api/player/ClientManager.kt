package com.github.rushyverse.api.player

import org.bukkit.entity.Player

/**
 * Get a client from the player instance.
 * @param player Player.
 * @return The client linked to a player.
 */
public suspend inline fun <reified T : Client> ClientManager.getTypedClient(player: Player): T = getClient(player) as T

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
public suspend inline fun <reified T : Client> ClientManager.getTypedClientOrNull(player: Player): T? =
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
 * @property clients Synchronized mutable map of client as value and name of player as key.
 */
public interface ClientManager {

    public val clients: Map<String, Client>

    /**
     * Put a new client in the server.
     * @param client New client added.
     * @return The previous value associated with key, or null there is none.
     */
    public suspend fun put(player: Player, client: Client): Client?

    /**
     * Put a new client in the server if no client is linked to the player.
     * @param client New client added.
     * @return The previous value associated with key, or null there is none.
     */
    public suspend fun putIfAbsent(player: Player, client: Client): Client?

    /**
     * Remove a client from the server by a Player.
     * @param player Player linked to a Client.
     * @return The client that was removed, null otherwise.
     */
    public suspend fun removeClient(player: Player): Client?

    /**
     * Get a client from the player instance.
     * @param player Player.
     * @return The client linked to a player.
     */
    public suspend fun getClient(player: Player): Client

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
    public suspend fun getClientOrNull(player: Player): Client?

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
    public suspend fun contains(player: Player): Boolean
}