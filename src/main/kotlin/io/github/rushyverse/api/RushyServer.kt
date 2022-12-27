package io.github.rushyverse.api

import io.github.rushyverse.api.command.GamemodeCommand
import io.github.rushyverse.api.command.GiveCommand
import io.github.rushyverse.api.command.KickCommand
import io.github.rushyverse.api.command.StopCommand
import io.github.rushyverse.api.configuration.IConfiguration
import io.github.rushyverse.api.translation.ResourceBundleTranslationsProvider
import io.github.rushyverse.api.translation.SupportedLanguage
import io.github.rushyverse.api.translation.TranslationsProvider
import io.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import io.github.rushyverse.api.utils.workingDirectory
import mu.KLogger
import mu.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.instance.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import java.io.File
import java.util.*

public val logger: KLogger = KotlinLogging.logger { }

/**
 * Abstract implementation of Minecraft server.
 */
public abstract class RushyServer {

    public companion object API {
        /**
         * Name of the bundle for API.
         */
        public const val BUNDLE_API: String = "api"

        /**
         * Register the API commands in the [CommandManager].
         * @param manager CommandManager to register the commands in.
         */
        public fun registerCommands(manager: CommandManager = MinecraftServer.getCommandManager()) {
            manager.register(StopCommand())
            manager.register(KickCommand())
            manager.register(GiveCommand())
            manager.register(GamemodeCommand())
        }
    }

    /**
     * Start the minecraft server.
     */
    public abstract fun start()

    /**
     * Initialize the server and start it using the given (or default) configuration.
     * @param configurationPath Path of the configuration file in working directory.
     * @param init Initialization function with the configuration loaded and the world container.
     */
    protected inline fun <reified T : IConfiguration> start(
        configurationPath: String? = null,
        init: T.(InstanceContainer) -> Unit
    ) {
        logger.info { "Loading configuration from $configurationPath" }
        val config = loadConfiguration<T>(configurationPath)
        logger.info { "Configuration loaded" }

        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val serverConfig = config.server
        loadWorld(serverConfig.world, instanceContainer)

        init(config, instanceContainer)

        minecraftServer.start("0.0.0.0", serverConfig.port)
    }

    /**
     * With the [worldFolder], retrieve the file of the world and load it in the [instanceContainer].
     * @param worldFolder World folder.
     * @param instanceContainer Instance container of the server.
     */
    protected open fun loadWorld(worldFolder: String, instanceContainer: InstanceContainer) {
        val anvilWorld = File(workingDirectory, worldFolder)
        if (!anvilWorld.isDirectory) {
            throw FileSystemException(
                anvilWorld,
                null,
                "World ${anvilWorld.absolutePath} does not exist or is not a directory"
            )
        }
        instanceContainer.chunkLoader = AnvilLoader(anvilWorld.toPath())
    }

    /**
     * Load the configuration using the file or the default config file.
     * @param configFile Path of the configuration file.
     * @return The configuration of the server.
     */
    protected inline fun <reified T> loadConfiguration(configFile: String?): T {
        val configurationFile = IConfiguration.getOrCreateConfigurationFile(configFile)
        return IConfiguration.readHoconConfigurationFile(configurationFile)
    }

    /**
     * Create a translation provider to provide translations for the [supported languages][SupportedLanguage].
     * @return New translation provider.
     */
    protected open fun createTranslationsProvider(bundles: Iterable<String>): TranslationsProvider {
        return ResourceBundleTranslationsProvider().apply {
            bundles.forEach { registerResourceBundleForSupportedLocales(it, ResourceBundle::getBundle) }
        }
    }
}