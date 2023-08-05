package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.inventory.ItemFlag
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.Test

class ItemFlagSerializerTest {

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @EnumSource(ItemFlag::class)
        fun `should use enum name`(item: ItemFlag) {
            val name = item.name
            Json.encodeToString(ItemFlagSerializer, item) shouldEqualJson """
                "$name"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @EnumSource(ItemFlag::class)
        fun `should find value with uppercase`(value: ItemFlag) {
            val name = value.name.uppercase()
            Json.decodeFromString(ItemFlagSerializer, "\"$name\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(ItemFlag::class)
        fun `should find value with lowercase`(value: ItemFlag) {
            val name = value.name.lowercase()
            Json.decodeFromString(ItemFlagSerializer, "\"$name\"") shouldBe value
        }

        @ParameterizedTest
        @EnumSource(ItemFlag::class)
        fun `should find value with space`(value: ItemFlag) {
            val name = value.name.replace("_", " ")
            Json.decodeFromString(ItemFlagSerializer, "\"$name\"") shouldBe value
        }

        @Test
        fun `should throw exception if value is not found`() {
            val name = randomString()
            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(ItemFlagSerializer, "\"$name\"")
            }
            exception.message shouldBe "Invalid enum value: $name. Valid values are: HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS, HIDE_DYE, HIDE_ARMOR_TRIM"
        }
    }
}
