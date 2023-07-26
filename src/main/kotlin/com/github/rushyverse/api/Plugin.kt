package com.github.rushyverse.api

import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.rushyverse.api.extension.registerListener
import com.github.rushyverse.api.koin.*
import com.github.rushyverse.api.listener.*
import com.github.rushyverse.api.player.*
import com.github.rushyverse.api.translation.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.module.Module
import org.koin.dsl.bind
import java.util.*

/**
 * Abstract plugin with the necessary component to create a plugin.
 */
public abstract class Plugin : SuspendingJavaPlugin() {

    public abstract val id: String

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        CraftContext.startKoin(id)
        moduleBukkit()

        registerListener { PlayerListener(this) }
        registerListener { VillagerListener(this) }
    }

    protected inline fun <reified T : Plugin> modulePlugin(): Module = loadModule(id) {
        single { this@Plugin }
        single { this@Plugin as T }
    }

    protected fun moduleClients(): Module = loadModule(id) {
        single { ClientManagerImpl() } bind ClientManager::class
    }

    protected open fun moduleBukkit(): Module = loadModule(id) {
        single { Bukkit.getServer() }
        single { getLogger() }
    }

    override suspend fun onDisableAsync() {
        CraftContext.stopKoin(id)
        super.onDisableAsync()
    }

    /**
     * Create a new instance of a client.
     * @param player Player linked to the client.
     * @return C The instance of the client.
     */
    public abstract fun createClient(player: Player): Client

    /**
     * Create a translation provider to provide translations for the [supported languages][SupportedLanguage].
     * @return New translation provider.
     */
    protected open suspend fun createTranslationProvider(): ResourceBundleTranslationProvider =
        ResourceBundleTranslationProvider().apply {
            registerResourceBundleForSupportedLocales(BUNDLE_API, ResourceBundle::getBundle)
        }
}
