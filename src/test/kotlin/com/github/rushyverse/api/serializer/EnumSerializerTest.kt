package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test

class EnumSerializerTest {

    @Suppress("unused")
    enum class TestEnum {
        TEST_VALUE1,
        TestValue2,
    }

    data object TestEnumSerializer : EnumSerializer<TestEnum>("testEnum", TestEnum.entries)

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @EnumSource(TestEnum::class)
        fun `should use enum name`(value: TestEnum) {
            val enumName = value.name
            Json.encodeToString(TestEnumSerializer, value) shouldEqualJson """
                "$enumName"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(TestEnum::class)
        fun `should find value with uppercase`(value: TestEnum) {
            val enumName = value.name.uppercase()
            Json.decodeFromString(TestEnumSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(TestEnum::class)
        fun `should find value with lowercase`(value: TestEnum) {
            val enumName = value.name.lowercase()
            Json.decodeFromString(TestEnumSerializer, "\"$enumName\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(TestEnum::class)
        fun `should find value with space`(value: TestEnum) {
            val enumName = value.name.replace("_", " ")
            Json.decodeFromString(TestEnumSerializer, "\"$enumName\"") shouldBe value
        }

        @Test
        fun `should throw exception if value is not found`() {
            val enumName = randomString()
            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(TestEnumSerializer, "\"$enumName\"")
            }
            exception.message shouldBe "Invalid enum value: $enumName. Valid values are: TEST_VALUE1, TestValue2"
        }
    }
}
