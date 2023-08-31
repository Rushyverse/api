package com.github.rushyverse.api.listener.api

import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener to manage scoreboard data when a player enters or leaves the server.
 */
public class ScoreboardListener : Listener {

    private val scoreboardManager: ScoreboardManager by inject()

    /**
     * Listen [PlayerQuitEvent] to remove the player from the scoreboard manager.
     * @param event Event.
     */
    @EventHandler
    public suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        scoreboardManager.remove(player)
    }
}
