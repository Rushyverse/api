package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.utils.workingDirectory
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileNotFoundException

/**
 * Configuration of the application.
 * @property server Configuration of server.
 */
public interface IConfiguration {

    public companion object {

        /**
         * Default name of the config file.
         * This name is used to create the default config file when the user does not provide one.
         */
        public const val DEFAULT_CONFIG_FILE_NAME: String = "server.conf"

        /**
         * Get the configuration file from the given path.
         * If the path is null, the default config file will be used.
         * If the default config file does not exist, it will be created with the default configuration from resources folder.
         * @param filePath Path of the configuration file.
         * @return The configuration file that must be used to load application configuration.
         */
        public fun getOrCreateConfigurationFile(filePath: String? = null): File {
            if (filePath != null) {
                val configFile = File(filePath)
                if (!configFile.isFile) {
                    throw FileNotFoundException("Config file $filePath does not exist or is not a regular file")
                }
                return configFile
            }

            return getOrCreateDefaultConfigurationFile(workingDirectory)
        }

        /**
         * Search for the default config file in the current directory.
         * If the file does not exist, it will be created with the default configuration from resources folder.
         * @return The default config file.
         */
        private fun getOrCreateDefaultConfigurationFile(parent: File): File =
            File(parent, DEFAULT_CONFIG_FILE_NAME).apply {
                if (exists()) {
                    return this
                }

                val defaultConfiguration =
                    IConfiguration::class.java.classLoader.getResourceAsStream(DEFAULT_CONFIG_FILE_NAME)
                        ?: error("Unable to find default configuration file in server resources")

                defaultConfiguration.use { inputStream ->
                    if (!createNewFile()) {
                        throw FileSystemException(this, null, "Unable to create configuration file $absolutePath")
                    }

                    outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
    }

    public val server: IServerConfiguration
}

/**
 * Configuration about the minestom server.
 * @property port Port of the server.
 * @property world Path of the world to load.
 * @property onlineMode `true` to enable the Mojang authentication.
 * @property velocity Velocity configuration.
 * @property bungeeCord BungeeCord configuration.
 */
public interface IServerConfiguration {

    public val port: Int

    public val world: String

    public val onlineMode: Boolean

    public val velocity: IVelocityConfiguration

    public val bungeeCord: IBungeeCordConfiguration
}

/**
 * Configuration to connect the server to the velocity proxy.
 * @property enabled Whether the velocity support is enabled.
 * @property secret Secret to verify if the client comes from the proxy.
 */
public interface IVelocityConfiguration {

    public val enabled: Boolean

    public val secret: String

}

/**
 * Configuration to connect the server to the velocity proxy.
 */
@Serializable
public data class VelocityConfiguration(
    override val enabled: Boolean,
    override val secret: String
) : IVelocityConfiguration


/**
 * Configuration to connect the server to the bungeeCord proxy.
 * @property enabled Whether the server should connect to the bungeeCord proxy.
 * @property secrets Secrets to verify if the client comes from the proxy.
 */
public interface IBungeeCordConfiguration {

    public val enabled: Boolean

    public val secrets: Set<String>

}

/**
 * Configuration to connect the server to the bungeeCord proxy.
 */
@Serializable
public data class BungeeCordConfiguration(
    override val enabled: Boolean,
    override val secrets: Set<String>
) : IBungeeCordConfiguration