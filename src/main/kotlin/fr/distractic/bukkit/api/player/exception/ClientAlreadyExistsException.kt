package fr.distractic.bukkit.api.player.exception

/**
 * Exception if a client is already exists for a player.
 */
public class ClientAlreadyExistsException(message: String? = null) : RuntimeException(message)