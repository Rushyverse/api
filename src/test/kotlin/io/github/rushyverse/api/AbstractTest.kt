package io.github.rushyverse.api

import io.github.rushyverse.api.configuration.IConfiguration
import io.github.rushyverse.api.configuration.IServerConfiguration
import io.github.rushyverse.api.utils.getAvailablePort
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
    override val world: String
) : IServerConfiguration

abstract class AbstractTest {

    companion object {
        private const val PROPERTY_USER_DIR = "user.dir"
    }

    @TempDir
    lateinit var tmpDirectory: File

    private lateinit var initCurrentDirectory: String

    protected val expectedDefaultConfiguration: TestConfiguration
        get() = TestConfiguration(
            ServerConfiguration(25565, "world")
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

    protected fun configurationToHoconFile(configuration: TestConfiguration, file: File) =
        file.writeText(configurationToHocon(configuration).root().render())

    protected fun copyFolderFromResourcesToFolder(folderName: String, destination: File) {
        val folder = File(javaClass.classLoader.getResource(folderName)!!.file)
        folder.copyRecursively(destination)
    }

    protected fun copyWorldInTmpDirectory(
        configuration: TestConfiguration = defaultConfigurationOnAvailablePort()
    ) {
        val worldFile = fileOfTmpDirectory(configuration.server.world)
        copyFolderFromResourcesToFolder("world", worldFile)
    }

    protected fun defaultConfigurationOnAvailablePort() = expectedDefaultConfiguration.let {
        it.copy(server = it.server.copy(port = getAvailablePort()))
    }
}