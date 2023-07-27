package com.github.rushyverse.api.configuration.reader

import org.jetbrains.annotations.Blocking

/**
 * Configuration reader.
 * Useful to read configuration and transform it to a specific type.
 * @param T Final type to obtain after transformation.
 */
public fun interface ConfigurationReader<T : Any> {

    /**
     * Read the configuration from the given file.
     * @param file File to read.
     * @return Configuration read from the given file.
     */
    @Blocking
    public fun readConfiguration(file: String): T
}
