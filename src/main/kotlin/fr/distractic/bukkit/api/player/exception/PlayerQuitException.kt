package fr.distractic.bukkit.api.player.exception

import kotlin.coroutines.cancellation.CancellationException

/**
 * Exception throw when the player quits the server.
 * Allows canceling all coroutine linked to a player.
 */
public class PlayerQuitException(message: String? = null) : CancellationException(message)