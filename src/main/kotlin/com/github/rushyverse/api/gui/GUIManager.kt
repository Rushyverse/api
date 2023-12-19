package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manages the GUIs for players within the game.
 * This class ensures thread-safe operations on the GUIs by using mutex locks.
 */
public class GUIManager {

    /**
     * Mutex used to ensure thread-safe operations.
     */
    private val mutex = Mutex()

    /**
     * Private mutable set storing GUIs.
     */
    private val _guis = mutableSetOf<GUI<*>>()

    /**
     * Immutable view of the GUIs set.
     */
    public val guis: Collection<GUI<*>> get() = _guis

    /**
     * Retrieves the GUI for the specified player.
     * This function is thread-safe and uses mutex locks to ensure atomic operations.
     *
     * @param client The player for whom the GUI is to be retrieved or created.
     * @return The language associated with the player.
     */
    public suspend fun get(client: Client): GUI<*>? {
        return mutex.withLock {
            guis.firstOrNull { it.contains(client) }
        }
    }

    /**
     * Add a GUI to the listener.
     * @param gui GUI to add.
     * @return True if the GUI was added, false otherwise.
     */
    public suspend fun add(gui: GUI<*>): Boolean {
        return mutex.withLock { _guis.add(gui) }
    }

    /**
     * Remove a GUI from the listener.
     * @param gui GUI to remove.
     * @return True if the GUI was removed, false otherwise.
     */
    public suspend fun remove(gui: GUI<*>): Boolean {
        return mutex.withLock { _guis.remove(gui) }
    }
}
