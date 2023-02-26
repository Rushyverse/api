@file:OptIn(ExperimentalCoroutinesApi::class)

package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.AbstractTest
import com.github.rushyverse.api.configuration.IConfigurationReader.Companion.getOrCreateConfigurationFile
import com.github.rushyverse.api.utils.randomString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IConfigurationTest : AbstractTest() {

    @Test
    fun `name of default configuration file is correct`() = runTest {
        assertEquals("server.conf", IConfigurationReader.DEFAULT_CONFIG_FILE_NAME)
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
                createConfigFileAndCheckIfFound(IConfigurationReader.DEFAULT_CONFIG_FILE_NAME) {
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

                val expectedConfigurationFile = fileOfTmpDirectory(IConfigurationReader.DEFAULT_CONFIG_FILE_NAME)
                assertEquals(expectedConfigurationFile, configurationFile)

                inputStreamOfDefaultConfiguration().bufferedReader().use {
                    assertEquals(it.readText(), configurationFile.readText())
                }
            }
        }

        private fun getRandomFileInTmpDirectory() = fileOfTmpDirectory(randomString())
    }

    private fun inputStreamOfDefaultConfiguration() =
        IConfiguration::class.java.classLoader.getResourceAsStream(IConfigurationReader.DEFAULT_CONFIG_FILE_NAME)
            ?: error("Unable to find default configuration file in server resources")
}