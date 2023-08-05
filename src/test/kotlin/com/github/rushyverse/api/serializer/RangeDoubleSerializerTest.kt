package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomDouble
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class RangeDoubleSerializerTest {

    @Nested
    inner class Serialize {

        @Test
        fun `should serialize using start and end`() {
            val start = randomDouble(-100.0, 100.0)
            val end = randomDouble(100.0, 200.0)
            Json.encodeToString(RangeDoubleSerializer, start..end) shouldEqualJson """
                {
                    "start": $start,
                    "end": $end
                }
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should throw if start is missing`() {
            val end = randomDouble(100.0, 200.0)
            val json = """
                {
                  "end": $end
                }
            """.trimIndent()
            assertThrows<SerializationException> {
                Json.decodeFromString(RangeDoubleSerializer, json)
            }
        }

        @Test
        fun `should throw if end is missing`() {
            val start = randomDouble(-100.0, 100.0)
            val json = """
                {
                  "start": $start
                }
            """.trimIndent()
            assertThrows<SerializationException> {
                Json.decodeFromString(RangeDoubleSerializer, json)
            }
        }

        @Test
        fun `should deserialize using start and end`() {
            val start = randomDouble(-100.0, 100.0)
            val end = randomDouble(100.0, 200.0)
            val json = """
                {
                  "start": $start,
                  "end": $end
                }
            """.trimIndent()
            Json.decodeFromString(RangeDoubleSerializer, json) shouldBe (start..end)
        }

    }
}
