package com.github.rushyverse.api.listener.api

import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.language.LanguageManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener to manage language data when a player enters or leaves the server.
 */
public class LanguageListener : Listener {

    private val languageManager: LanguageManager by inject()

    /**
     * Listen [PlayerQuitEvent] to remove the player from the language manager.
     * @param event Event.
     */
    @EventHandler
    public suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        languageManager.remove(player)
    }
}
