package com.github.rushyverse.api.player.exception

import kotlin.coroutines.cancellation.CancellationException

/**
 * Exception throw when the player quits the server.
 * Allows canceling all coroutines linked to a player.
 */
public class PlayerQuitException(message: String? = null) : CancellationException(message)
