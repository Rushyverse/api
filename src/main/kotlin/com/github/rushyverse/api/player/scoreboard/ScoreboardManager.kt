package com.github.rushyverse.api.player.scoreboard

import fr.mrmicky.fastboard.adventure.FastBoard
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player

/**
 * Manages the scoreboards for players within the game.
 * This class ensures thread-safe operations on the scoreboards by using mutex locks.
 */
public class ScoreboardManager {

    /**
     * Mutex used to ensure thread-safe operations on the scoreboards map.
     */
    private val mutex = Mutex()

    /**
     * Private mutable map storing scoreboards associated with player names.
     */
    private val _scoreboards = mutableMapOf<String, FastBoard>()

    /**
     * Public immutable view of the scoreboards map.
     */
    public val scoreboards: Map<String, FastBoard> = _scoreboards

    /**
     * Retrieves the scoreboard for the specified player or creates a new one if it doesn't exist.
     * This function is thread-safe and uses mutex locks to ensure atomic operations.
     *
     * @param player The player for whom the scoreboard is to be retrieved or created.
     * @return The scoreboard associated with the player.
     */
    public suspend fun getOrCreate(player: Player): FastBoard = mutex.withLock {
        _scoreboards.getOrPut(player.name) {
            FastBoard(player)
        }
    }

    /**
     * Removes and deletes the scoreboard associated with the specified player.
     * This function is thread-safe and uses mutex locks to ensure atomic operations.
     *
     * @param player The player whose scoreboard is to be removed.
     */
    public suspend fun remove(player: Player) {
        mutex.withLock { _scoreboards.remove(player.name) }?.delete()
    }
}
