package fr.rushy.api

import fr.rushy.api.command.GamemodeCommand
import fr.rushy.api.command.GiveCommand
import fr.rushy.api.command.KickCommand
import fr.rushy.api.command.StopCommand
import fr.rushy.api.configuration.Configuration
import fr.rushy.api.configuration.ServerConfiguration
import fr.rushy.api.translation.ResourceBundleTranslationsProvider
import fr.rushy.api.translation.TranslationsProvider
import fr.rushy.api.translation.registerResourceBundleForSupportedLocales
import fr.rushy.api.utils.workingDirectory
import mu.KLogger
import mu.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.instance.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import java.io.File
import java.util.*

public val logger: KLogger = KotlinLogging.logger { }

public abstract class RushyServer {

    public companion object {
        public const val API_BUNDLE_NAME: String = "api"
    }

    public abstract fun main(args: Array<String>)

    public inline fun <reified T : Configuration> start(
        configurationPath: String? = null,
        init: T.(InstanceContainer) -> Unit
    ) {
        logger.info { "Loading configuration from $configurationPath" }
        val config = loadConfiguration<T>(configurationPath)
        logger.info { "Configuration loaded" }

        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        loadWorld(config.server, instanceContainer)

        init(config, instanceContainer)

        minecraftServer.start("0.0.0.0", config.server.port)
    }

    /**
     * With the [serverConfig], retrieve the file of the world and load it in the [instanceContainer].
     * @param serverConfig Configuration of the minestom server.
     * @param instanceContainer Instance container of the server.
     */
    public open fun loadWorld(
        serverConfig: ServerConfiguration,
        instanceContainer: InstanceContainer
    ) {
        val anvilWorld = File(workingDirectory, serverConfig.world)
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
    public inline fun <reified T> loadConfiguration(configFile: String?): T {
        val configurationFile = Configuration.getOrCreateConfigurationFile(configFile)
        return Configuration.readHoconConfigurationFile(configurationFile)
    }

    /**
     * Create a translation provider to provide translations for the [supported languages][fr.rushy.api.translation.SupportedLanguage].
     * @return New translation provider.
     */
    public open fun createTranslationsProvider(bundles: Iterable<String>): TranslationsProvider {
        return ResourceBundleTranslationsProvider().apply {
            registerResourceBundleForSupportedLocales(API_BUNDLE_NAME, ResourceBundle::getBundle)
            bundles.forEach { registerResourceBundleForSupportedLocales(it, ResourceBundle::getBundle) }
        }
    }

    /**
     * Register all commands.
     */
    public open fun registerCommands(manager: CommandManager = MinecraftServer.getCommandManager()) {
        manager.register(StopCommand())
        manager.register(KickCommand())
        manager.register(GiveCommand())
        manager.register(GamemodeCommand())
    }
}