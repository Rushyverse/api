package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.shynixn.mccoroutine.bukkit.scope
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.Plugin

/**
 * GUI where a new inventory is created for each [locale][Locale].
 * This is useful to share the GUI between multiple players with the same language.
 *
 * For example, if two players have the same language, they will share the same inventory.
 * If one of them changes their language, he will have another inventory dedicated to his new language.
 */
public abstract class LocaleGUI(
    protected val plugin: Plugin,
    loadingAnimation: InventoryLoadingAnimation<Locale>? = null,
    initialNumberInventories: Int = SupportedLanguage.entries.size
) : GUI<Locale>(
    loadingAnimation = loadingAnimation,
    initialNumberInventories = initialNumberInventories
) {

    override suspend fun getKey(client: Client): Locale {
        return client.lang().locale
    }

    override suspend fun fillScope(key: Locale): CoroutineScope {
        val scope = plugin.scope
        return scope + SupervisorJob(scope.coroutineContext.job)
    }

    override suspend fun close(client: Client, closeInventory: Boolean): Boolean {
        return if (closeInventory && contains(client)) {
            client.player?.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
            true
        } else false
    }
}
