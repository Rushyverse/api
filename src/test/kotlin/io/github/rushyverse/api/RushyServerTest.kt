package io.github.rushyverse.api

import io.github.rushyverse.api.command.GamemodeCommand
import io.github.rushyverse.api.command.GiveCommand
import io.github.rushyverse.api.command.KickCommand
import io.github.rushyverse.api.command.StopCommand
import io.github.rushyverse.api.configuration.IConfiguration
import io.github.rushyverse.api.utils.randomString
import net.minestom.server.MinecraftServer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestServer(private val configuration: String? = null) : RushyServer() {

    override fun start() {
        start<TestConfiguration>(configuration) {
            registerCommands()
        }
    }
}

class RushyServerTest : AbstractTest() {

    @AfterTest
    override fun onAfter() {
        super.onAfter()
        if (MinecraftServer.process() != null) {
            MinecraftServer.stopCleanly()
        }
    }

    @Test
    fun `should have the correct bundle name`() {
        assertEquals("api", RushyServer.API_BUNDLE_NAME)
    }

    @Nested
    inner class CreateOrGetConfiguration {

        @Test
        fun `should create a configuration file if it doesn't exist`() {
            assertThrows<IOException> {
                TestServer().start()
            }
            val configurationFile = fileOfTmpDirectory(IConfiguration.DEFAULT_CONFIG_FILE_NAME)
            assertTrue { configurationFile.isFile }

            val configuration = IConfiguration.readHoconConfigurationFile<TestConfiguration>(configurationFile)
            assertEquals(expectedDefaultConfiguration, configuration)
        }

        @Test
        fun `should use the configuration file if exists`() {
            val configurationFile = fileOfTmpDirectory(randomString())
            assertTrue { configurationFile.createNewFile() }

            val configuration = defaultConfigurationOnAvailablePort()
            configurationToHoconFile(configuration, configurationFile)

            val exception = assertThrows<FileSystemException> {
                TestServer(configurationFile.absolutePath).start()
            }
            assertEquals(configuration.server.world, exception.file.name)
        }

    }

    @Nested
    inner class UseConfiguration {

        @Test
        fun `should use configuration to turn on the server`() {
            val configuration = defaultConfigurationOnAvailablePort()
            val configurationFile = fileOfTmpDirectory(randomString())
            configurationToHoconFile(configuration, configurationFile)

            copyWorldInTmpDirectory(configuration)

            TestServer(configurationFile.absolutePath).start()

            // If no exception is thrown, the world is loaded
            assertTrue { MinecraftServer.isStarted() }

            val server = MinecraftServer.getServer()
            assertEquals(configuration.server.port, server.port)
            assertEquals("0.0.0.0", server.address)
        }
    }

    @Nested
    inner class Command {

        @Test
        fun `should load all commands`() {
            copyWorldInTmpDirectory()
            TestServer().start()

            val commandManager = MinecraftServer.getCommandManager()
            assertContentEquals(
                commandManager.commands.asSequence().map { it::class.java }.sortedBy { it.simpleName }.toList(),
                sequenceOf(
                    StopCommand::class.java,
                    KickCommand::class.java,
                    GiveCommand::class.java,
                    GamemodeCommand::class.java
                ).sortedBy { it.simpleName }.toList()
            )
        }
    }
}