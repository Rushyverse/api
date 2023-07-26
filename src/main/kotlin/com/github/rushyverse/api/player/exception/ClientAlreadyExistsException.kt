package com.github.rushyverse.api.player.exception

/**
 * Exception if a client already exists for a player.
 */
public class ClientAlreadyExistsException(message: String? = null) : RuntimeException(message)
