package io.github.distractic.bukkit.api

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import io.github.distractic.bukkit.api.extension.registerListener
import io.github.distractic.bukkit.api.koin.CraftContext
import io.github.distractic.bukkit.api.koin.loadModule
import io.github.distractic.bukkit.api.listener.PlayerListener
import io.github.distractic.bukkit.api.listener.VillagerListener
import io.github.distractic.bukkit.api.player.Client
import io.github.distractic.bukkit.api.player.ClientManager
import io.github.distractic.bukkit.api.player.ClientManagerImpl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.module.Module
import org.koin.dsl.bind

/**
 * Abstract plugin with the necessary component to create a plugin.
 */
public abstract class Plugin : SuspendingJavaPlugin() {

    public abstract val id: String

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        CraftContext.startKoin(id) { }
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
    }

    override suspend fun onDisableAsync() {
        CraftContext.stopKoin(id)
        super.onDisableAsync()
    }

    /**
     * Create a new instance of client.
     * @param player Player linked to the client.
     * @return C The instance of the client.
     */
    public abstract fun createClient(player: Player): Client
}