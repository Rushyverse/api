package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.block.banner.PatternType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test

class PatternTypeSerializerTest {

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should use identifier`(value: PatternType) {
            val identifier = value.identifier
            Json.encodeToString(PatternTypeSerializer, value) shouldEqualJson """
                "$identifier"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should find value with identifier lowercase`(value: PatternType) {
            val identifier = value.identifier.lowercase()
            Json.decodeFromString(PatternTypeSerializer, "\"$identifier\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should find value with identifier uppercase`(value: PatternType) {
            val identifier = value.identifier.uppercase()
            Json.decodeFromString(PatternTypeSerializer, "\"$identifier\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should find value with name uppercase`(value: PatternType) {
            val enumName = value.name.uppercase()
            Json.decodeFromString(PatternTypeSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should find value with name lowercase`(value: PatternType) {
            val enumName = value.name.lowercase()
            Json.decodeFromString(PatternTypeSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(PatternType::class)
        fun `should find value with name space`(value: PatternType) {
            val enumName = value.name.replace("_", " ")
            Json.decodeFromString(PatternTypeSerializer, "\"$enumName\"") shouldBe value
        }

        @Test
        fun `should throw exception if value is not found`() {
            val enumName = randomString()
            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(PatternTypeSerializer, "\"$enumName\"")
            }
            exception.message shouldBe "Invalid enum value: $enumName. Valid values are: ${
                PatternType.entries.joinToString(", ")
            }"
        }
    }
}
