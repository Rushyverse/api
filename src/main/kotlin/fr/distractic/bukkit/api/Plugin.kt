package fr.distractic.bukkit.api

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import fr.distractic.bukkit.api.extension.registerListener
import fr.distractic.bukkit.api.koin.CraftContext
import fr.distractic.bukkit.api.koin.loadModule
import fr.distractic.bukkit.api.listener.PlayerListener
import fr.distractic.bukkit.api.listener.VillagerListener
import fr.distractic.bukkit.api.player.Client
import fr.distractic.bukkit.api.player.ClientManager
import fr.distractic.bukkit.api.player.ClientManagerImpl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.bind

/**
 * Abstract plugin with the necessary component to create a plugin.
 */
public abstract class Plugin : SuspendingJavaPlugin(), KoinComponent {

    public abstract val id: String

    override suspend fun onEnableAsync() {
        super.onEnableAsync()

        CraftContext.startKoin(id) { }
        moduleBukkit()

        registerListener { PlayerListener(this) }
        registerListener(::VillagerListener)
    }

    protected open fun modulePlugin(): Module = loadModule(id) {
        single { this@Plugin }
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