package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.AbstractTest
import com.github.rushyverse.api.TestConfiguration
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.decodeFromConfig
import net.minestom.server.coordinate.Pos
import org.junit.jupiter.api.Nested
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HoconConfigurationReaderTest : AbstractTest() {

    private lateinit var file: File

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        file = fileOfTmpDirectory("test.conf")
    }

    @Nested
    inner class HoconDefaultConfiguration {

        @Test
        fun `should support Pos serializer`() {
            val hocon = HoconConfigurationReader.hoconDefault

            val expected = Pos(1.0, 2.0, 3.0, 4f, 5f)
            file.writeText(
                """
                {
                    x: 1.0
                    y: 2.0
                    z: 3.0
                    yaw: 4.0
                    pitch: 5.0
                }
            """.trimIndent()
            )

            val configFile = ConfigFactory.parseFile(file)
            val result: Pos = hocon.decodeFromConfig(configFile)

            assertEquals(expected, result)
        }

    }

    @Nested
    inner class ReadConfigurationFileWithReifiedTypeParameter {

        @Test
        fun `should read configuration file`() {
            configurationToHoconFile(expectedDefaultConfiguration, file)
            val configuration = HoconConfigurationReader()
            val result: TestConfiguration = configuration.readConfigurationFile(file)
            assertEquals(expectedDefaultConfiguration, result)
        }

    }

    @Nested
    inner class ReadConfigurationFileWithClassParameter {

        @Test
        fun `should read configuration file`() {
            configurationToHoconFile(expectedDefaultConfiguration, file)
            val configuration = HoconConfigurationReader()
            val result = configuration.readConfigurationFile(TestConfiguration::class, file)
            assertEquals(expectedDefaultConfiguration, result)
        }

    }

    @Nested
    inner class ReadConfigurationFileWithSerializerParameter {

        @Test
        fun `should read configuration file`() {
            configurationToHoconFile(expectedDefaultConfiguration, file)
            val configuration = HoconConfigurationReader()
            val result = configuration.readConfigurationFile(TestConfiguration.serializer(), file)
            assertEquals(expectedDefaultConfiguration, result)
        }

    }
}