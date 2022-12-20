@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)

package io.github.rushyverse.api.configuration

import io.github.rushyverse.api.AbstractTest
import io.github.rushyverse.api.TestConfiguration
import io.github.rushyverse.api.configuration.IConfiguration.Companion.getOrCreateConfigurationFile
import io.github.rushyverse.api.utils.randomString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigurationTest : AbstractTest() {

    @Test
    fun `name of default configuration file is correct`() = runTest {
        assertEquals("server.conf", IConfiguration.DEFAULT_CONFIG_FILE_NAME)
    }

    @Nested
    inner class GetOrCreateConfigurationFile {

        @Nested
        inner class GetExistingConfigurationFile {

            @Test
            fun `should return the given configuration file`() = runTest {
                createConfigFileAndCheckIfFound(randomString()) {
                    getOrCreateConfigurationFile(it.absolutePath)
                }
            }

            @Test
            fun `should return the default config file without edit it`() = runTest {
                createConfigFileAndCheckIfFound(IConfiguration.DEFAULT_CONFIG_FILE_NAME) {
                    getOrCreateConfigurationFile()
                }
            }

            private inline fun createConfigFileAndCheckIfFound(fileName: String, block: (File) -> File) {
                val configurationFile = fileOfTmpDirectory(fileName)
                assertTrue { configurationFile.createNewFile() }

                val content = UUID.randomUUID().toString()
                configurationFile.writeText(content)

                val file = block(configurationFile)
                assertEquals(configurationFile, file)
                assertEquals(content, file.readText())
            }
        }

        @Nested
        inner class GetNonExistingConfigurationFile {

            @Test
            fun `should throw exception if file not found`() = runTest {
                assertThrows<FileNotFoundException> {
                    getOrCreateConfigurationFile(getRandomFileInTmpDirectory().absolutePath)
                }
            }

            @Test
            fun `should throw exception if file is not a regular file`() = runTest {
                assertThrows<FileNotFoundException> {
                    getOrCreateConfigurationFile(tmpDirectory.absolutePath)
                }
            }
        }

        @Nested
        inner class CreateDefaultConfiguration {
            @Test
            fun `should create the config file if it's not found in the current directory`() = runTest {
                val configurationFile = getOrCreateConfigurationFile()
                assertTrue { configurationFile.isFile }

                val expectedConfigurationFile = fileOfTmpDirectory(IConfiguration.DEFAULT_CONFIG_FILE_NAME)
                assertEquals(expectedConfigurationFile, configurationFile)

                inputStreamOfDefaultConfiguration().bufferedReader().use {
                    assertEquals(it.readText(), configurationFile.readText())
                }
            }
        }

        @Nested
        inner class ReadHoconConfiguration {
            @Test
            fun `should create default configuration and read it`() = runTest {
                val configurationFile = getOrCreateConfigurationFile()

                val configuration = IConfiguration.readHoconConfigurationFile<TestConfiguration>(configurationFile)
                assertEquals(expectedDefaultConfiguration, configuration)
            }

            @Test
            fun `should throw exception if file not found`() = runTest {
                assertThrows<MissingFieldException> {
                    IConfiguration.readHoconConfigurationFile<TestConfiguration>(getRandomFileInTmpDirectory())
                }
            }

            @Test
            fun `should throw exception if fields missing`() = runTest {
                val file = getRandomFileInTmpDirectory()
                assertTrue { file.createNewFile() }
                file.writeText("server { }")

                assertThrows<MissingFieldException> {
                    IConfiguration.readHoconConfigurationFile<TestConfiguration>(file)
                }
            }
        }

        private fun getRandomFileInTmpDirectory() = fileOfTmpDirectory(randomString())
    }

    private fun inputStreamOfDefaultConfiguration() =
        IConfiguration::class.java.classLoader.getResourceAsStream(IConfiguration.DEFAULT_CONFIG_FILE_NAME)
            ?: error("Unable to find default configuration file in server resources")
}