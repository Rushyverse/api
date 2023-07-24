package com.github.rushyverse.api

import fr.mrmicky.fastboard.FastBoard
import org.bukkit.entity.Player

public object API {

    private val fastBoards: MutableList<FastBoard> = mutableListOf()

    /**
     * Get or initialize a FastBoard for a given player.
     * This approach can be useful for running multiple API-dependant plugins
     * on the same server.
     */
    public fun getOrInitFastBoard(player: Player): FastBoard {
        var existingFastBoard = fastBoards.firstOrNull { it.player == player }

        if (existingFastBoard == null) {
            existingFastBoard = FastBoard(player)
            fastBoards.add(existingFastBoard)
            println("API: FastBoards: Create new for ${player.name}")
        } else {
            println("API: FastBoards: Get existing for ${player.name}")
        }

        println("API: FastBoards list: $fastBoards")

        return existingFastBoard
    }
}