package com.github.rushyverse.api

import com.github.rushyverse.api.command.GamemodeCommand
import com.github.rushyverse.api.command.GiveCommand
import com.github.rushyverse.api.command.KickCommand
import com.github.rushyverse.api.command.StopCommand
import com.github.rushyverse.api.configuration.*
import com.github.rushyverse.api.translation.ResourceBundleTranslationsProvider
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import com.github.rushyverse.api.utils.workingDirectory
import mu.KLogger
import mu.KotlinLogging
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandManager
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import net.minestom.server.instance.AnvilLoader
import net.minestom.server.instance.InstanceContainer
import java.io.File
import java.util.*
import kotlin.reflect.KClass

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
    public abstract suspend fun start()

    /**
     * Initialize the server and start it using the given (or default) configuration.
     * @param configurationPath Path of the configuration file in working directory.
     * @param init Initialization function with the configuration loaded and the world container.
     */
    protected suspend inline fun <reified T : IConfiguration> start(
        configurationPath: String? = null,
        configurationReader: ConfigurationReader = HoconConfigurationReader(),
        init: T.(InstanceContainer) -> Unit
    ) {
        logger.info { "Loading configuration from $configurationPath" }
        val config = loadConfiguration<T>(configurationReader, configurationPath)
        logger.info { "Configuration loaded" }

        val minecraftServer = MinecraftServer.init()
        val instanceManager = MinecraftServer.getInstanceManager()
        val instanceContainer = instanceManager.createInstanceContainer()
        val serverConfig = config.server
        loadWorld(serverConfig.world, instanceContainer)

        applyVelocityConfiguration(serverConfig.velocity)
        applyBungeeCordConfiguration(serverConfig.bungeeCord)

        applyOnlineMode(serverConfig.onlineMode)

        init(config, instanceContainer)

        minecraftServer.start("0.0.0.0", serverConfig.port)
    }

    /**
     * Enable the online mode if [enabled] is true.
     * @param enabled If the online mode should be enabled.
     */
    protected open suspend fun applyOnlineMode(enabled: Boolean) {
        if (enabled) {
            logger.info { "Enabling Online mode" }
            MojangAuth.init()
            logger.info { "Online mode enabled" }
        }
    }

    /**
     * Enable the [Velocity system][VelocityProxy] if the configuration [IVelocityConfiguration] is enabled.
     * @param velocity Velocity configuration.
     */
    protected open suspend fun applyVelocityConfiguration(velocity: IVelocityConfiguration) {
        if (velocity.enabled) {
            logger.info { "Enabling Velocity support" }
            VelocityProxy.enable(velocity.secret)
            logger.info { "Velocity support enabled" }
        }
    }

    /**
     * Enable the [BungeeCord system][BungeeCordProxy] if the configuration [IBungeeCordConfiguration] is enabled.
     * @param bungeeCord BungeeCord configuration.
     */
    protected open suspend fun applyBungeeCordConfiguration(bungeeCord: IBungeeCordConfiguration) {
        if (bungeeCord.enabled) {
            logger.info { "Enabling BungeeCord support" }
            BungeeCordProxy.enable()
            BungeeCordProxy.setBungeeGuardTokens(bungeeCord.secrets)
            logger.info { "BungeeCord support enabled" }
        }
    }

    /**
     * With the [worldFolder], retrieve the file of the world and load it in the [instanceContainer].
     * @param worldFolder World folder.
     * @param instanceContainer Instance container of the server.
     */
    protected open suspend fun loadWorld(worldFolder: String, instanceContainer: InstanceContainer) {
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
     * @param configurationReader Configuration reader to use.
     * @param configFile Path of the configuration file.
     * @return The configuration of the server.
     */
    protected suspend inline fun <reified T : Any> loadConfiguration(
        configurationReader: ConfigurationReader,
        configFile: String?
    ): T {
        return loadConfiguration(T::class, configurationReader, configFile)
    }

    /**
     * Load the configuration using the file or the default config file.
     * @param clazz Type of configuration class to load.
     * @param configurationReader Configuration reader to use.
     * @param configFile Path of the configuration file.
     * @return The configuration of the server.
     */
    protected open suspend fun <T : Any> loadConfiguration(
        clazz: KClass<T>,
        configurationReader: ConfigurationReader,
        configFile: String?
    ): T {
        val configurationFile = IConfiguration.getOrCreateConfigurationFile(configFile)
        return configurationReader.readConfigurationFile(clazz, configurationFile)
    }

    /**
     * Create a translation provider to provide translations for the [supported languages][SupportedLanguage].
     * @param bundles Bundles to load.
     * @return New translation provider.
     */
    protected open suspend fun createTranslationsProvider(bundles: Iterable<String>): TranslationsProvider {
        return ResourceBundleTranslationsProvider().apply {
            bundles.forEach { registerResourceBundleForSupportedLocales(it, ResourceBundle::getBundle) }
        }
    }
}