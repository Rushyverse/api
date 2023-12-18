package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import com.github.shynixn.mccoroutine.bukkit.scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.Plugin

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server.
 * @property viewers List of viewers.
 */
public abstract class SingleGUI(
    protected val plugin: Plugin,
    loadingAnimation: InventoryLoadingAnimation<Any>? = null
) : GUI<Any>(
    loadingAnimation = loadingAnimation,
    initialNumberInventories = 1
) {

    public companion object {
        /**
         * Unique key for the GUI.
         * This GUI is shared by all the players, so the key is the same for all of them.
         * That allows creating a unique inventory.
         */
        private val KEY = Any()
    }

    override suspend fun fillScope(key: Any): CoroutineScope {
        val scope = plugin.scope
        return scope + SupervisorJob(scope.coroutineContext.job)
    }

    override suspend fun close(client: Client, closeInventory: Boolean): Boolean {
        return if (closeInventory && contains(client)) {
            client.player?.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            true
        } else false
    }

    override suspend fun getKey(client: Client): Any {
        return KEY
    }
}
