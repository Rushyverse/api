package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.serializer.Pos
import com.github.rushyverse.api.serializer.PosSerializer
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * Read configuration from HOCON file.
 * @receiver Configuration reader.
 * @param configFile Configuration file to load.
 * @return The configuration loaded from the given file.
 */
public inline fun <reified T> HoconConfigurationReader.readConfigurationFile(configFile: File): T =
    readConfigurationFile(hocon.serializersModule.serializer(), configFile)

/**
 * Read configuration from HOCON file.
 * @property hocon [Hocon] configuration to use.
 */
public class HoconConfigurationReader(public val hocon: Hocon = hoconDefault) : IConfigurationReader {

    public companion object {
        /**
         * Default [Hocon] configuration using custom serializer.
         * @see PosSerializer
         */
        public val hoconDefault: Hocon = Hocon {
            serializersModule = SerializersModule {
                contextual(Pos::class, PosSerializer)
            }
        }
    }

    override fun <T : Any> readConfigurationFile(clazz: KClass<T>, configFile: File): T {
        val serializer = hocon.serializersModule.serializer(clazz.createType())
        @Suppress("UNCHECKED_CAST")
        return readConfigurationFile(serializer as KSerializer<T>, configFile)
    }

    override fun <T> readConfigurationFile(serializer: KSerializer<T>, configFile: File): T {
        val config = ConfigFactory.parseFile(configFile)
        return hocon.decodeFromConfig(serializer, config)
    }
}