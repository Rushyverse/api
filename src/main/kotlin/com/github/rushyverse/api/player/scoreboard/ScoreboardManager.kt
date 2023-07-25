package com.github.rushyverse.api.player.scoreboard

import fr.mrmicky.fastboard.FastBoard
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player

public class ScoreboardManager {

    private val mutex = Mutex()

    private val _scoreboards = mutableMapOf<String, FastBoard>()

    public val scoreboards: Map<String, FastBoard> = _scoreboards

    public suspend fun getOrCreate(player: Player): FastBoard = mutex.withLock {
        _scoreboards.getOrPut(player.name) {
            FastBoard(player)
        }
    }

    public suspend fun remove(player: Player) {
        mutex.withLock { _scoreboards.remove(player.name) }?.delete()
    }
}
