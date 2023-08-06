package com.github.rushyverse.api

import com.github.rushyverse.api.game.SharedGameData
import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.koin.loadModule
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

/**
 * Plugin to enable the API in server.
 */
public class APIPlugin : JavaPlugin() {

    public companion object {
        /**
         * A unique identifier for this plugin. This ID is used for tasks like identifying
         * the Koin application, loading Koin modules, etc.
         */
        public const val ID_API: String = "api"

        /**
         * This ID is used to identify the resource bundle of API translations in the project files.
         */
        public const val BUNDLE_API: String = "api_translate"
    }

    override fun onEnable() {
        super.onEnable()
        CraftContext.startKoin(ID_API)
        loadModule(ID_API) {
            single { Bukkit.getServer() }
            single { ScoreboardManager() }
            single { SharedGameData() }
        }
    }

    override fun onDisable() {
        CraftContext.stopKoin(ID_API)
        super.onDisable()
    }
}
