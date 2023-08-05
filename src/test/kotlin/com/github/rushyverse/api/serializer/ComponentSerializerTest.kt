package com.github.rushyverse.api.serializer

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ComponentSerializerTest {

    private lateinit var world: WorldMock

    @BeforeTest
    fun onBefore() {
        world = WorldMock()
        MockBukkit.mock().apply {
            addWorld(world)
        }
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Serialize {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                " ",
                "My string"
            ]
        )
        fun `without decoration`(value: String) {
            val component = Component.text(value)
            Json.encodeToString(ComponentSerializer, component) shouldEqualJson """
                "$value"
            """.trimIndent()
        }

        @Test
        fun `with named color`() {
            fun assertColor(color: NamedTextColor) {
                val string = randomString()
                val colorName = color.toString()
                val component = Component.text(string, color)
                Json.encodeToString(ComponentSerializer, component) shouldEqualJson """
                "<$colorName>$string</$colorName>"
            """.trimIndent()
            }

            assertColor(NamedTextColor.AQUA)
            assertColor(NamedTextColor.BLACK)
            assertColor(NamedTextColor.BLUE)
            assertColor(NamedTextColor.DARK_AQUA)
            assertColor(NamedTextColor.DARK_BLUE)
        }

        @Test
        fun `with custom color`() {
            fun assertColor(hex: String, color: NamedTextColor? = null) {
                val string = randomString()
                val component = Component.text(string, TextColor.fromHexString(hex))

                val expected = if (color != null) {
                    val colorName = color.toString()
                    """
                        "<$colorName>$string</$colorName>"
                    """.trimIndent()
                } else {
                    val colorName = hex.lowercase()
                    """
                        "<$colorName>$string</$colorName>"
                    """.trimIndent()
                }

                Json.encodeToString(ComponentSerializer, component) shouldEqualJson expected
            }

            assertColor("#e3e3e3")
            assertColor("#000000", NamedTextColor.BLACK)
            assertColor("#FFFFFF", NamedTextColor.WHITE)
            assertColor("#FF0000")
        }

        @ParameterizedTest
        @EnumSource(TextDecoration::class)
        fun `with decoration`(decoration: TextDecoration) {
            val string = randomString()
            val component = Component.text(string).decorate(decoration)
            val decorationName = decoration.toString()
            Json.encodeToString(ComponentSerializer, component) shouldEqualJson """
                    "<$decorationName>$string</$decorationName>"
            """.trimIndent()
        }

        @Test
        fun `with color and decoration`() {
            val string = randomString()
            val component = Component.text(string, NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
            Json.encodeToString(ComponentSerializer, component) shouldEqualJson """
                    "<bold><aqua>$string</aqua></bold>"
            """.trimIndent()
        }


    }

    @Nested
    inner class Deserialize {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                " ",
                "My string"
            ]
        )
        fun `without decoration`(value: String) {
            Json.decodeFromString(
                ComponentSerializer,
                """
                  "$value"
                """.trimIndent()
            ) shouldBe Component.text(value)
        }

        @Test
        fun `with named color`() {
            fun assertColor(color: NamedTextColor) {
                val string = randomString()
                val colorName = color.toString()
                val expectedComponent = Component.text(string).color(color)

                // Non strict mode supported
                Json.decodeFromString(
                    ComponentSerializer,
                    """
                      "<$colorName>$string"
                    """.trimIndent()
                ) shouldBe expectedComponent

                // Strict mode supported
                Json.decodeFromString(
                    ComponentSerializer,
                    """
                      "<$colorName>$string</$colorName>"
                    """.trimIndent()
                ) shouldBe expectedComponent
            }

            assertColor(NamedTextColor.AQUA)
            assertColor(NamedTextColor.BLACK)
            assertColor(NamedTextColor.BLUE)
            assertColor(NamedTextColor.DARK_AQUA)
            assertColor(NamedTextColor.DARK_BLUE)
        }

        @Test
        fun `with custom color`() {
            fun assertColor(hex: String) {
                val string = randomString()
                val expectedComponent = Component.text(string, TextColor.fromHexString(hex))

                // Non strict mode supported
                Json.decodeFromString(
                    ComponentSerializer,
                    """
                      "<$hex>$string"
                    """.trimIndent()
                ) shouldBe expectedComponent

                // Strict mode supported
                Json.decodeFromString(
                    ComponentSerializer,
                    """
                      "<$hex>$string</$hex>"
                    """.trimIndent()
                ) shouldBe expectedComponent
            }

            assertColor("#e3e3e3")
            assertColor("#000000")
            assertColor("#FFFFFF")
            assertColor("#FF0000")
        }

        @ParameterizedTest
        @EnumSource(TextDecoration::class)
        fun `with decoration`(decoration: TextDecoration) {
            val string = randomString()
            val expectedComponent = Component.text(string).decorate(decoration)
            val decorationName = decoration.toString()

            // Non strict mode supported
            Json.decodeFromString(
                ComponentSerializer,
                """
                      "<$decorationName>$string"
                """.trimIndent()
            ) shouldBe expectedComponent

            // Strict mode supported
            Json.decodeFromString(
                ComponentSerializer,
                """
                      "<$decorationName>$string</$decorationName>"
                """.trimIndent()
            ) shouldBe expectedComponent
        }

        @Test
        fun `with color and decoration`() {
            val string = randomString()
            val expectedComponent = Component.text(string, NamedTextColor.AQUA).decorate(TextDecoration.BOLD)

            Json.decodeFromString(
                ComponentSerializer,
                """
                      "<aqua><bold>$string"
                """.trimIndent()
            ) shouldBe expectedComponent

            Json.decodeFromString(
                ComponentSerializer,
                """
                      "<aqua><bold>$string</bold></aqua>"
                """.trimIndent()
            ) shouldBe expectedComponent
        }

    }
}
