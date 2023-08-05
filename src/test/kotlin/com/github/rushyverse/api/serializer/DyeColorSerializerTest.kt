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
        fun `should use enum name`(value: DyeColor) {
            val enumName = value.name
            Json.encodeToString(DyeColorSerializer, value) shouldEqualJson """
                "$enumName"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with uppercase`(value: DyeColor) {
            val enumName = value.name.uppercase()
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with lowercase`(value: DyeColor) {
            val enumName = value.name.lowercase()
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with space`(value: DyeColor) {
            val enumName = value.name.replace("_", " ")
            Json.decodeFromString(DyeColorSerializer, "\"$enumName\"") shouldBe value
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
