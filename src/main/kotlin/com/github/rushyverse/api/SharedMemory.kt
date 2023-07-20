package com.github.rushyverse.api

import com.github.rushyverse.api.game.SharedGameData

/**
 * Ready to use Singleton object that represents a shared memory space that facilitates data exchange
 * between the API and the server plugins that rely on the API.
 */
public object SharedMemory {

    // Holds shared game-related data
    public val games: SharedGameData = SharedGameData()
}

