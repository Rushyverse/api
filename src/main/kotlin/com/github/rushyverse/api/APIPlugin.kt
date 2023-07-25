package com.github.rushyverse.api

import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Plugin to enable the API in server.
 */
public class APIPlugin : JavaPlugin() {

    public companion object {
        public const val ID: String = "api"
    }

    override fun onEnable() {
        CraftContext.startKoin(ID)
        loadModule(ID) {
            single { ScoreboardManager() }
        }
    }

    override fun onDisable() {
        CraftContext.stopKoin(ID)
    }
}
