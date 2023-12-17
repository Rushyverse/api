package com.github.rushyverse.api.gui

import com.github.rushyverse.api.Plugin
import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import com.github.shynixn.mccoroutine.bukkit.scope
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.withLock
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * GUI where a new inventory is created for each [locale][Locale].
 * This is useful to share the GUI between multiple players with the same language.
 *
 * For example, if two players have the same language, they will share the same inventory.
 * If one of them changes their language, he will have another inventory dedicated to his new language.
 */
public abstract class LocaleGUI(
    private val plugin: Plugin,
    loadingAnimation: InventoryLoadingAnimation<Locale>? = null
) : DedicatedGUI<Locale>(loadingAnimation) {

    override suspend fun getKey(client: Client): Locale {
        return client.lang().locale
    }

    override suspend fun loadingScope(key: Locale): CoroutineScope {
        val scope = plugin.scope
        return scope + SupervisorJob(scope.coroutineContext.job)
    }

    override suspend fun close(client: Client, closeInventory: Boolean): Boolean {
        if (!closeInventory) {
            return false
        }

        return mutex.withLock {
            if (unsafeContains(client)) {
                client.player?.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
                true
            } else false
        }
    }
}
