package com.github.rushyverse.api.configuration.reader

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.Blocking
import java.io.File
import kotlin.io.path.createParentDirectories
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * Read configuration from YAML file.
 * If the file does not exist, it will be created from the resource with the same name.
 * @property plugin Plugin to get the data folder from.
 * @property format [Yaml] configuration to use.
 */
public class YamlFileReader(
    public val plugin: JavaPlugin,
    override val format: StringFormat
) : IFileReader {

    @Blocking
    override fun <T : Any> readConfigurationFile(clazz: KClass<T>, filename: String): T {
        val serializer = format.serializersModule.serializer(clazz.createType())
        @Suppress("UNCHECKED_CAST")
        return readConfigurationFile(serializer as KSerializer<T>, filename)
    }

    @Blocking
    override fun <T> readConfigurationFile(serializer: KSerializer<T>, filename: String): T {
        val dataFolder = plugin.dataFolder
        require(dataFolder.exists() || dataFolder.mkdirs()) {
            "Unable to get or create the plugin data folder ${dataFolder.absolutePath}."
        }

        val config = File(dataFolder, filename)
        createConfigurationFileIfNecessary(config, filename)

        return format.decodeFromString(serializer, config.readText())
    }

    /**
     * Creates the [configuration][config] file if it does not exist.
     * Will create the parent directories if necessary.
     *
     * @param config The configuration file to create.
     * @param resourceFile The resource file to copy if the configuration file does not exist.
     */
    private fun createConfigurationFileIfNecessary(config: File, resourceFile: String) {
        if (config.exists()) return

        config.toPath().createParentDirectories()
        require(config.createNewFile()) {
            "Unable to create the configuration file ${config.absoluteFile}."
        }

        createFileFromResource(config, resourceFile)
    }

    /**
     * Read the file [resourceFile] from the plugin jar and copy content to [target].
     * @param target File to write the resource to.
     * @param resourceFile Name of the resource to copy.
     */
    @Blocking
    private fun createFileFromResource(target: File, resourceFile: String) {
        val resource = plugin::class.java.getResourceAsStream("/$resourceFile")
            ?: throw IllegalStateException("Cannot find resource $resourceFile in the plugin.")

        resource.bufferedReader().use { reader ->
            target.bufferedWriter().use { writer ->
                reader.copyTo(writer)
            }
        }
    }
}
