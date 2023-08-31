package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomEnum
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class PatternSerializerTest {

    @Nested
    inner class Serialize {

        @Test
        fun `should serialize using type identifier and color name`() {
            val dyeColor = randomEnum<DyeColor>()
            val patternType = randomEnum<PatternType>()
            val pattern = Pattern(dyeColor, patternType)
            Json.encodeToString(PatternSerializer, pattern) shouldEqualJson """
                {
                  "color": "${dyeColor.name}",
                  "type": "${patternType.identifier}"
                }
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should throw if color is missing`() {
            val patternType = randomEnum<PatternType>()
            val json = """
                {
                  "type": "${patternType.identifier}"
                }
            """.trimIndent()
            assertThrows<SerializationException> {
                Json.decodeFromString(PatternSerializer, json)
            }
        }

        @Test
        fun `should throw if type is missing`() {
            val dyeColor = randomEnum<DyeColor>()
            val json = """
                {
                  "color": "${dyeColor.name}"
                }
            """.trimIndent()
            assertThrows<SerializationException> {
                Json.decodeFromString(PatternSerializer, json)
            }
        }

        @Test
        fun `should deserialize using type and color name`() {
            val dyeColor = randomEnum<DyeColor>()
            val patternType = randomEnum<PatternType>()

            fun decode(color: String, type: String) {
                val json = """
                    {
                      "color": "$color",
                      "type": "$type"
                    }
                """.trimIndent()
                val pattern = Json.decodeFromString(PatternSerializer, json)
                pattern.color shouldBe dyeColor
                pattern.pattern shouldBe patternType
            }

            decode(dyeColor.name, patternType.identifier)
            decode(dyeColor.name, patternType.name)
        }

    }
}
