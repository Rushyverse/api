package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.Material
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test

class MaterialSerializerTest {

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @EnumSource(Material::class)
        fun `should use enum name`(color: Material) {
            val enumName = color.name
            Json.encodeToString(MaterialSerializer, color) shouldEqualJson """
                "$enumName"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(Material::class)
        fun `should find value with uppercase`(color: Material) {
            val enumName = color.name.uppercase()
            Json.decodeFromString(MaterialSerializer, "\"$enumName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(Material::class)
        fun `should find value with lowercase`(color: Material) {
            val enumName = color.name.lowercase()
            Json.decodeFromString(MaterialSerializer, "\"$enumName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(Material::class)
        fun `should find value with space`(color: Material) {
            val enumName = color.name.replace("_", " ")
            Json.decodeFromString(MaterialSerializer, "\"$enumName\"") shouldBe color
        }

        @Test
        fun `should throw exception if value is not found`() {
            val enumName = randomString()
            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(MaterialSerializer, "\"$enumName\"")
            }
            exception.message shouldBe "Invalid enum value: $enumName. Valid values are: ${
                Material.entries.joinToString(
                    ", "
                )
            }"
        }
    }
}
