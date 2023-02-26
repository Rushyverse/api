package com.github.rushyverse.api.configuration

import kotlinx.serialization.KSerializer
import java.io.File
import kotlin.reflect.KClass

/**
 * Configuration reader.
 */
public interface ConfigurationReader {

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