package com.github.rushyverse.api.configuration.reader

import com.charleskorn.kaml.Yaml
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test

class YamlFileReaderTest {

    @Serializable
    data class TestValue(val test: String)

    private lateinit var plugin: JavaPlugin
    private lateinit var reader: YamlFileReader

    @TempDir
    lateinit var tempDir: File

    @BeforeTest
    fun onBefore() {
        plugin = mockk()
        reader = YamlFileReader(plugin, Yaml.default)
    }

    @Nested
    inner class ReadConfigurationFileFromClass {

        @Test
        fun `should throw if class is not serializable`() {
            class Test
            shouldThrow<SerializationException> {
                reader.readConfigurationFile(Test::class, "test.yml")
            }
        }

        @ParameterizedTest
        @CsvSource(
            "fake_config.yml, withoutParent",
            "configuration/fake_config.yml, withParent"
        )
        fun `should create file and decode it`(configFile: String, value: String) {
            shouldCreateFileAndDecode(configFile, value) { reader.readConfigurationFile(TestValue::class, it) }
        }

        @Test
        fun `should not create file if exists and read it`() {
            shouldNotCreateFileIfExistsAndReadIt { reader.readConfigurationFile(TestValue::class, it) }
        }
    }

    @Nested
    inner class ReadConfigurationFileFromSerializer {

        @Test
        fun `should throw if plugin folder cannot be created`() {
            every { plugin.dataFolder } returns mockk {
                every { exists() } returns false
                every { mkdirs() } returns false
                every { absolutePath } returns "test"
            }

            val exception = shouldThrow<IllegalArgumentException> {
                reader.readConfigurationFile(mockk<KSerializer<Any>>(), "test.yml")
            }

            exception.message shouldBe "Unable to get or create the plugin data folder test."
        }

        @ParameterizedTest
        @CsvSource(
            "fake_config.yml, withoutParent",
            "configuration/fake_config.yml, withParent"
        )
        fun `should create file and decode it`(configFile: String, value: String) {
            shouldCreateFileAndDecode(configFile, value) { reader.readConfigurationFile(TestValue.serializer(), it) }
        }

        @Test
        fun `should not create file if exists and read it`() {
            shouldNotCreateFileIfExistsAndReadIt { reader.readConfigurationFile(TestValue.serializer(), it) }
        }
    }

    fun shouldCreateFileAndDecode(
        configFile: String,
        value: String,
        load: (String) -> TestValue
    ) {
        every { plugin.dataFolder } returns tempDir

        val expectedValue = TestValue(value)
        load(configFile) shouldBe expectedValue

        val file = File(tempDir, configFile)
        file.exists() shouldBe true
        file.readText() shouldBe "test: $value\r\n"
    }

    fun shouldNotCreateFileIfExistsAndReadIt(
        load: (String) -> TestValue
    ) {
        every { plugin.dataFolder } returns tempDir

        val value = "This is a custom value"

        val configFile = "fake_config.yml"
        val file = File(tempDir, configFile)
        val content = "test: $value"
        file.writeText(content)

        val expectedValue = TestValue(value)
        load(configFile) shouldBe expectedValue

        file.readText() shouldBe content
    }

}
