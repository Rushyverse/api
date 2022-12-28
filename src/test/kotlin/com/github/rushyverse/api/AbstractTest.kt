package com.github.rushyverse.api

import com.github.rushyverse.api.configuration.IBungeeCordConfiguration
import com.github.rushyverse.api.configuration.IConfiguration
import com.github.rushyverse.api.configuration.IServerConfiguration
import com.github.rushyverse.api.configuration.IVelocityConfiguration
import com.github.rushyverse.api.utils.getAvailablePort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.Hocon
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@Serializable
data class TestConfiguration(
    override val server: ServerConfiguration
) : IConfiguration

@SerialName("server")
@Serializable
data class ServerConfiguration(
    override val port: Int,
    override val world: String,
    override val onlineMode: Boolean,
    override val velocity: IVelocityConfiguration.VelocityConfiguration,
    override val bungeeCord: IBungeeCordConfiguration.BungeeCordConfiguration
) : IServerConfiguration

abstract class AbstractTest {

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
        const val DEFAULT_WORLD = "world"
    }

    @TempDir
    lateinit var tmpDirectory: File

    private lateinit var initCurrentDirectory: String

    protected val expectedDefaultConfiguration: TestConfiguration
        get() = TestConfiguration(
            ServerConfiguration(
                25565,
                DEFAULT_WORLD,
                false,
                IVelocityConfiguration.VelocityConfiguration(false, ""),
                IBungeeCordConfiguration.BungeeCordConfiguration(false, "")
            )
        )

    @BeforeTest
    open fun onBefore() {
        initCurrentDirectory = System.getProperty(PROPERTY_USER_DIR)
        System.setProperty(PROPERTY_USER_DIR, tmpDirectory.absolutePath)
    }

    @AfterTest
    open fun onAfter() {
        System.setProperty(PROPERTY_USER_DIR, initCurrentDirectory)
    }

    protected fun fileOfTmpDirectory(fileName: String) = File(tmpDirectory, fileName)

    protected fun configurationToHocon(configuration: TestConfiguration) =
        Hocon.encodeToConfig(TestConfiguration.serializer(), configuration)

    protected fun configurationToHoconFile(configuration: TestConfiguration, file: File = fileOfTmpDirectory(IConfiguration.DEFAULT_CONFIG_FILE_NAME)) =
        file.writeText(configurationToHocon(configuration).root().render())

    protected fun copyFolderFromResourcesToFolder(folderName: String, destination: File) {
        val folder = File(javaClass.classLoader.getResource(folderName)!!.file)
        folder.copyRecursively(destination)
    }

    protected fun copyWorldInTmpDirectory(
        configuration: TestConfiguration = defaultConfigurationOnAvailablePort()
    ) {
        val worldFile = fileOfTmpDirectory(configuration.server.world)
        copyFolderFromResourcesToFolder(DEFAULT_WORLD, worldFile)
    }

    protected fun defaultConfigurationOnAvailablePort() = expectedDefaultConfiguration.let {
        it.copy(server = it.server.copy(port = getAvailablePort()))
    }
}