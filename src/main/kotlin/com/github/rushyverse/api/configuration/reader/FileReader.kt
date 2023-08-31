package com.github.rushyverse.api.configuration.reader

import com.github.rushyverse.api.serializer.LocationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * Read configuration from YAML file.
 * @receiver Configuration reader.
 * @param configFile Configuration file to load.
 * @return The configuration loaded from the given file.
 */
public inline fun <reified T> IFileReader.readConfigurationFile(configFile: String): T =
    readConfigurationFile(format.serializersModule.serializer(), configFile)

/**
 * Configuration reader.
 */
public interface IFileReader {

    /**
     * Format to read string from configuration file.
     * The field should contain custom serializer for Bukkit classes like [LocationSerializer].
     */
    public val format: StringFormat

    /**
     * Load the configuration from the given file.
     * @param clazz Type of configuration class to load.
     * @param filename Configuration file to load based on the "plugins/${plugin.name}" directory.
     * So if the plugin name is "MyPlugin" and the config file is "config.yml",
     * the file will be loaded from "plugins/MyPlugin/config.yml".
     * @return The configuration loaded from the given file.
     */
    public fun <T : Any> readConfigurationFile(clazz: KClass<T>, filename: String): T

    /**
     * Load the configuration from the given file.
     * @param serializer Serializer to deserialize the configuration to the given type.
     * @param filename Configuration file to load  based on the "plugins/${plugin.name}" directory.
     * So if the plugin name is "MyPlugin" and the config file is "config.yml",
     * the file will be loaded from "plugins/MyPlugin/config.yml".
     * @return The configuration loaded from the given file.
     */
    public fun <T> readConfigurationFile(serializer: KSerializer<T>, filename: String): T
}
