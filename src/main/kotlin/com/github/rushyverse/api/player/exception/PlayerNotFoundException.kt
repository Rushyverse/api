package com.github.rushyverse.api.player.exception

/**
 * Exception if a player is not into the server.
 */
public class PlayerNotFoundException(message: String? = null) : RuntimeException(message)