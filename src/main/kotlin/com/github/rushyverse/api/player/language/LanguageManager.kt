package com.github.rushyverse.api.player.language

import com.github.rushyverse.api.translation.SupportedLanguage
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.Player

/**
 * Manages the languages for players within the game.
 * This class ensures thread-safe operations on the languages by using mutex locks.
 */
public class LanguageManager {

    /**
     * Mutex used to ensure thread-safe operations on the language map.
     */
    private val mutex = Mutex()

    /**
     * Private mutable map storing languages associated with player names.
     */
    private val _languages = mutableMapOf<String, SupportedLanguage>()

    /**
     * Public immutable view of the languages map.
     */
    public val languages: Map<String, SupportedLanguage> = _languages

    /**
     * Retrieves the languages for the specified player or creates a new one if it doesn't exist.
     * This function is thread-safe and uses mutex locks to ensure atomic operations.
     *
     * @param player The player for whom the language is to be retrieved or created.
     * @return The language associated with the player.
     */
    public suspend fun get(player: Player): SupportedLanguage = mutex.withLock {
        _languages.getOrDefault(player.name, SupportedLanguage.ENGLISH)
    }

    /**
     * Sets the language for the specified player.
     * @param player The player for whom the language is to be set.
     * @param lang The language to set.
     */
    public suspend fun set(player: Player, lang: SupportedLanguage) {
        mutex.withLock { _languages[player.name] = lang }
    }

    /**
     * Removes and deletes the language associated with the specified player.
     * This function is thread-safe and uses mutex locks to ensure atomic operations.
     *
     * @param player The player whose language is to be removed.
     */
    public suspend fun remove(player: Player) {
        mutex.withLock { _languages.remove(player.name) }
    }
}
