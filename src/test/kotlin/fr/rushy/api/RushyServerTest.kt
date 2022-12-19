package fr.rushy.api

import fr.rushy.api.command.GamemodeCommand
import fr.rushy.api.command.GiveCommand
import fr.rushy.api.command.KickCommand
import fr.rushy.api.command.StopCommand
import fr.rushy.api.configuration.Configuration
import fr.rushy.api.utils.randomString
import net.minestom.server.MinecraftServer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TestServer : RushyServer() {
    override fun main(args: Array<String>) {
        start<TestConfiguration>(args.firstOrNull()) {
            registerCommands()
        }
    }
}

class RushyServerTest : AbstractTest() {

    @AfterTest
    override fun onAfter() {
        super.onAfter()
        MinecraftServer.stopCleanly()
    }

    @Nested
    inner class CreateOrGetConfiguration {

        @Test
        fun `should create a configuration file if it doesn't exist`() {
            assertThrows<IOException> {
                TestServer.main(emptyArray())
            }
            val configurationFile = fileOfTmpDirectory(Configuration.DEFAULT_CONFIG_FILE_NAME)
            assertTrue { configurationFile.isFile }

            val configuration = Configuration.readHoconConfigurationFile<TestConfiguration>(configurationFile)
            assertEquals(expectedDefaultConfiguration, configuration)
        }

        @Test
        fun `should use the configuration file if exists`() {
            val configurationFile = fileOfTmpDirectory(randomString())
            assertTrue { configurationFile.createNewFile() }

            val configuration = defaultConfigurationOnAvailablePort()
            configurationToHoconFile(configuration, configurationFile)

            val exception = assertThrows<FileSystemException> {
                TestServer.main(arrayOf(configurationFile.absolutePath))
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

            TestServer.main(arrayOf(configurationFile.absolutePath))

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
            TestServer.main(emptyArray())

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