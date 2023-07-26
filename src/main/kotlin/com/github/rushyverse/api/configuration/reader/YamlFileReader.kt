package com.github.rushyverse.api.configuration.reader

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
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

    override fun <T : Any> readConfigurationFile(clazz: KClass<T>, filename: String): T {
        val serializer = format.serializersModule.serializer(clazz.createType())
        @Suppress("UNCHECKED_CAST")
        return readConfigurationFile(serializer as KSerializer<T>, filename)
    }

    override fun <T> readConfigurationFile(serializer: KSerializer<T>, filename: String): T {
        val dataFolder = plugin.dataFolder
        require(dataFolder.exists() || dataFolder.mkdirs()) {
            "Unable to get or create the plugin data folder ${dataFolder.absoluteFile}."
        }

        val config = File(dataFolder, filename)
        if (!config.exists()) {
            createFileFromResource(config, filename)
        }

        return format.decodeFromString(serializer, config.readText())
    }

    /**
     * Read the file [filename] from the plugin jar and copy content to [target].
     * @param target File to write the resource to.
     * @param filename Name of the resource to copy.
     */
    private fun createFileFromResource(target: File, filename: String) {
        val resource = plugin::class.java.getResourceAsStream("/$filename")
            ?: throw IllegalStateException("Cannot find resource $filename in the plugin jar. Unable to create the configuration file ${target.absoluteFile}.")

        resource.bufferedReader().use { reader ->
            target.bufferedWriter().use { writer ->
                reader.copyTo(writer)
            }
        }
    }
}
