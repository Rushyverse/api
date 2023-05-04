package com.github.rushyverse.api

import com.github.rushyverse.api.configuration.*
import com.github.rushyverse.api.translation.ResourceBundleTranslationsProvider
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import com.github.rushyverse.api.translation.registerResourceBundleForSupportedLocales
import mu.KLogger
import mu.KotlinLogging
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.reflect.KClass

public val logger: KLogger = KotlinLogging.logger { }

/**
 * Abstract implementation of Minecraft server.
 */
public abstract class RushyServer : JavaPlugin() {

    public companion object API {
        /**
         * Name of the bundle for API.
         */
        public const val BUNDLE_API: String = "api"

        /**
         * Register the API commands.
         */
        public fun registerCommands() {
            // New commands here
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
        init: (T) -> Unit
    ) {
        logger.info { "Loading configuration from $configurationPath" }
        val config = loadConfiguration<T>(configurationPath)
        logger.info { "Configuration loaded" }

        val serverConfig = config.server

        applyVelocityConfiguration(serverConfig.velocity)
        applyBungeeCordConfiguration(serverConfig.bungeeCord)

        init(config)
    }

    /**
     * Enable the [Velocity system][VelocityProxy] if the configuration [IVelocityConfiguration] is enabled.
     * @param velocity Velocity configuration.
     */
    protected open suspend fun applyVelocityConfiguration(velocity: IVelocityConfiguration) {
        if (velocity.enabled) {
            logger.info { "Enabling Velocity support" }
            // TODO: Velocity configuration here
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
            // TODO: BungeeCord configuration here
            logger.info { "BungeeCord support enabled" }
        }
    }

    /**
     * Load the configuration using the file or the default config file.
     * Will use the [HoconConfigurationReader] to load the configuration.
     * @param configFile Path of the configuration file.
     * @return The configuration of the server.
     */
    protected suspend inline fun <reified T : Any> loadConfiguration(
        configFile: String?
    ): T {
        return loadConfiguration(T::class, configFile)
    }

    /**
     * Load the configuration using the file or the default config file.
     * @param clazz Type of configuration class to load.
     * @param configFile Path of the configuration file.
     * @return The configuration of the server.
     */
    protected open suspend fun <T : Any> loadConfiguration(
        clazz: KClass<T>,
        configFile: String?
    ): T {
        val configurationFile = IConfigurationReader.getOrCreateConfigurationFile(configFile)
        return HoconConfigurationReader().readConfigurationFile(clazz, configurationFile)
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