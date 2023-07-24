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
        }

        return existingFastBoard
    }

    /**
     * Deletes and removes an existing FastBoard.
     */
    public fun removeFastBoard(fastBoard: FastBoard) {
        if (!fastBoard.isDeleted)
            fastBoard.delete()
        fastBoards.remove(fastBoard)
    }
}