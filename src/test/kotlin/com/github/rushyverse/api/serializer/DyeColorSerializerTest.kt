package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
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
            val colorName = color.name
            Json.encodeToString(DyeColorSerializer, color) shouldEqualJson """
                "$colorName"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with uppercase`(color: DyeColor) {
            val colorName = color.name.uppercase()
            Json.decodeFromString(DyeColorSerializer, "\"$colorName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with lowercase`(color: DyeColor) {
            val colorName = color.name.lowercase()
            Json.decodeFromString(DyeColorSerializer, "\"$colorName\"") shouldBe color
        }

        @ParameterizedTest
        @EnumSource(DyeColor::class)
        fun `should find value with space`(color: DyeColor) {
            val colorName = color.name.replace("_", " ")
            Json.decodeFromString(DyeColorSerializer, "\"$colorName\"") shouldBe color
        }

        @Test
        fun `should throw exception if value is not found`() {
            val colorName = randomString()
            val exception = assertThrows<IllegalArgumentException> {
                Json.decodeFromString(DyeColorSerializer, "\"$colorName\"")
            }
            exception.message shouldBe "Invalid enum value: $colorName. Valid values are: WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK"
        }
    }
}
