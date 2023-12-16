package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import java.util.*
import kotlinx.coroutines.sync.withLock
import org.bukkit.event.inventory.InventoryCloseEvent

/**
 * GUI where a new inventory is created for each language.
 * This is useful to share the GUI between multiple players with the same language.
 *
 * For example, if two players have the same language, they will share the same inventory.
 * If one of them changes their language, he will have another inventory dedicated to his new language.
 */
public abstract class TranslateSharedGUI : DedicatedGUI<Locale>() {

    override suspend fun getKey(client: Client): Locale {
        return client.lang().locale
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
