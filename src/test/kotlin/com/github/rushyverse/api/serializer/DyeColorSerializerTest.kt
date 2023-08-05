package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.DyeColor
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test

class DyeColorSerializerTest {

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should use enum name`(color: DyeColor) {
            val enumName = color.name
            Json.encodeToString(DyeColorSerializer, color) shouldEqualJson """
                "$enumName"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with uppercase`(color: DyeColor) {
            val enumName = color.name.uppercase()
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with lowercase`(color: DyeColor) {
            val enumName = color.name.lowercase()
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with space`(color: DyeColor) {
            val enumName = color.name.replace("_", " ")
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe color
        }

        @Test
        fun `should throw exception if value is not found`() {
            val enumName = randomString()
            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(DyeColorSerializer, "\"$enumName\"")
            }
            exception.message shouldBe "Invalid enum value: $enumName. Valid values are: ${
                DyeColor.entries.joinToString(
                    ", "
                )
            }"
        }
    }
}
