package com.github.rushyverse.api

import com.github.rushyverse.api.command.GamemodeCommand
import com.github.rushyverse.api.command.GiveCommand
import com.github.rushyverse.api.command.KickCommand
import com.github.rushyverse.api.command.StopCommand
import com.github.rushyverse.api.configuration.*
import com.github.rushyverse.api.utils.randomString
import kotlinx.coroutines.test.runTest
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.velocity.VelocityProxy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import kotlin.test.*

class TestServer(private val configuration: String? = null) : RushyServer() {

    override suspend fun start() {
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
        assertEquals("api", RushyServer.API.BUNDLE_API)
    }

    @Nested
    inner class CreateOrGetConfiguration {

        @Test
        fun `should create a configuration file if it doesn't exist`() = runTest {
            assertThrows<IOException> {
                TestServer().start()
            }
            val configurationFile = fileOfTmpDirectory(IConfigurationReader.DEFAULT_CONFIG_FILE_NAME)
            assertTrue { configurationFile.isFile }

            val configuration = HoconConfigurationReader().readConfigurationFile<TestConfiguration>(configurationFile)
            assertEquals(expectedDefaultConfiguration, configuration)
        }

        @Test
        fun `should use the configuration file if exists`() = runTest {
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
        fun `should use configuration to turn on the server`() = runTest {
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
        fun `should load all commands`() = runTest {
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

    @Nested
    inner class Velocity {

        @BeforeTest
        fun onBefore() {
            VelocityProxy::class.java.getDeclaredField("enabled").apply {
                isAccessible = true
                setBoolean(null, false)
            }
        }

        @Test
        fun `should load velocity`() = runTest {
            test(true, "secret")
        }

        @Test
        fun `should not load velocity`() = runTest {
            test(false, "")
        }

        private suspend fun test(enabled: Boolean, secret: String) {
            val defaultConfiguration = expectedDefaultConfiguration
            val configuration = expectedDefaultConfiguration.copy(
                defaultConfiguration.server.copy(
                    velocity = VelocityConfiguration(enabled, secret)
                )
            )
            configurationToHoconFile(configuration)
            copyWorldInTmpDirectory(configuration)
            TestServer().start()

            assertEquals(enabled, VelocityProxy.isEnabled())
        }
    }

    @Nested
    inner class BungeeCord {

        @BeforeTest
        fun onBefore() {
            BungeeCordProxy::class.java.getDeclaredField("enabled").apply {
                isAccessible = true
                setBoolean(null, false)
            }
            BungeeCordProxy.setBungeeGuardTokens(null)
        }

        @Test
        fun `should load bungeecord`() = runTest {
            test(true, "test")
            assertTrue(BungeeCordProxy.isValidBungeeGuardToken("test"))
        }

        @Test
        fun `should not load bungeecord`() = runTest {
            test(false, "")
        }

        private suspend fun test(enabled: Boolean, secret: String) {
            val defaultConfiguration = expectedDefaultConfiguration
            val configuration = expectedDefaultConfiguration.copy(
                server = defaultConfiguration.server.copy(
                    bungeeCord = BungeeCordConfiguration(enabled, setOf(secret))
                )
            )

            configurationToHoconFile(configuration)
            copyWorldInTmpDirectory(configuration)

            TestServer().start()

            assertEquals(enabled, BungeeCordProxy.isEnabled())
            assertEquals(enabled, BungeeCordProxy.isBungeeGuardEnabled())
        }
    }

    @Nested
    inner class OnlineMode {

        @BeforeTest
        fun onBefore() {
            MojangAuth::class.java.getDeclaredField("enabled").apply {
                isAccessible = true
                setBoolean(null, false)
            }
        }

        @Test
        fun `should set online mode`() = runTest {
            test(true)
        }

        @Test
        fun `should set offline mode`() = runTest {
            test(false)
        }

        private suspend fun test(onlineMode: Boolean) {
            val defaultConfiguration = expectedDefaultConfiguration
            val configuration = expectedDefaultConfiguration.copy(
                server = defaultConfiguration.server.copy(
                    onlineMode = onlineMode
                )
            )
            configurationToHoconFile(configuration)
            copyWorldInTmpDirectory(configuration)
            TestServer().start()

            assertEquals(onlineMode, MojangAuth.isEnabled())
        }

    }
}