package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.utils.workingDirectory
import kotlinx.serialization.KSerializer
import java.io.File
import java.io.FileNotFoundException
import kotlin.reflect.KClass

/**
 * Configuration reader.
 */
public interface IConfigurationReader {

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
         * @param parent Parent directory of the default config file.
         * @return The default config file.
         */
        private fun getOrCreateDefaultConfigurationFile(parent: File): File =
            File(parent, DEFAULT_CONFIG_FILE_NAME).apply {
                if (exists()) {
                    return this
                }

                val defaultConfiguration =
                    IConfigurationReader::class.java.classLoader.getResourceAsStream(DEFAULT_CONFIG_FILE_NAME)
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

    /**
     * Load the configuration from the given file.
     * @param clazz Type of configuration class to load.
     * @param configFile Configuration file to load.
     * @return The configuration loaded from the given file.
     */
    public fun <T : Any> readConfigurationFile(clazz: KClass<T>, configFile: File): T

    /**
     * Load the configuration from the given file.
     * @param serializer Serializer to deserialize the configuration to the given type.
     * @param configFile Configuration file to load.
     * @return The configuration loaded from the given file.
     */
    public fun <T> readConfigurationFile(serializer: KSerializer<T>, configFile: File): T
}