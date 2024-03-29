package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.randomString
import io.kotest.matchers.shouldBe
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringExtTest {

    @Nested
    @DisplayName("Base64")
    inner class Base64Test {

        @Test
        fun `encode string`() {
            assertEquals("SGVsbG8gd29ybGQ=", "Hello world".encodeBase64ToString())
            assertEquals("w6LDr8O5LSo=", "âïù-*".encodeBase64ToString())
            assertEquals("2LXYqNin2K0g2KfZhNiu2YrYsQ==", "صباح الخير".encodeBase64ToString())
            assertEquals("44GT44KT44Gr44Gh44Gv", "こんにちは".encodeBase64ToString())
        }

        @Test
        fun `decode string`() {
            assertEquals("Hello world", "SGVsbG8gd29ybGQ=".decodeBase64ToString())
            assertEquals("âïù-*", "w6LDr8O5LSo=".decodeBase64ToString())
            assertEquals("صباح الخير", "2LXYqNin2K0g2KfZhNiu2YrYsQ==".decodeBase64ToString())
            assertEquals("こんにちは", "44GT44KT44Gr44Gh44Gv".decodeBase64ToString())
        }
    }

    @Nested
    @DisplayName("Conversion UUID")
    inner class ConversionUUID {

        @Nested
        inner class Strict {

            @Test
            fun `can convert if the string is valid`() {
                val uuid = UUID.randomUUID()
                val string = uuid.toString()
                assertEquals(uuid, string.toUUIDStrict())
            }

            @ParameterizedTest
            @ValueSource(
                strings = [
                    "",
                    "a",
                    "c7e4ca3236d942408e53de44bef8eeeb",
                    "c7e4ca32-36d9-4240-8e53-de44bef8eeeba"
                ]
            )
            fun `throws exception if invalid`(value: String) {
                assertThrows<IllegalArgumentException> {
                    value.toUUIDStrict()
                }
            }

            @Test
            fun `non null value when value can be converted`() {
                val uuid = UUID.randomUUID()
                val string = uuid.toString()
                assertEquals(uuid, string.toUUIDStrictOrNull())
            }

            @ParameterizedTest
            @ValueSource(
                strings = [
                    "",
                    "a",
                    "c7e4ca3236d942408e53de44bef8eeeb",
                    "c7e4ca32-36d9-4240-8e53-de44bef8eeeba"
                ]
            )
            fun `nulls if invalid`(value: String) {
                assertNull(value.toUUIDStrictOrNull())
            }

        }

        @Nested
        inner class NoStrict {

            @ParameterizedTest
            @ValueSource(
                strings = [
                    "c7e4ca3236d942408e53de44bef8eeeb",
                    "c7e4ca32-36d9-4240-8e53-de44bef8eeeb"
                ]
            )
            fun `can convert if the string is valid`(value: String) {
                val uuid = UUID.fromString("c7e4ca32-36d9-4240-8e53-de44bef8eeeb")
                assertEquals(uuid, value.toUUID())
            }

            @ParameterizedTest
            @ValueSource(
                strings = [
                    "",
                    "a",
                    "c7e4ca3236d942408e53de44bef8eeeba",
                    "c7e4ca32-36d9-4240-8e53-de44bef8eeeba"
                ]
            )
            fun `throws exception if invalid`(value: String) {
                assertThrows<IllegalArgumentException> {
                    value.toUUID()
                }
            }

            @ParameterizedTest
            @ValueSource(strings = ["c7e4ca3236d942408e53de44bef8eeeb", "c7e4ca32-36d9-4240-8e53-de44bef8eeeb"])
            fun `non null value when value can be converted`(value: String) {
                val uuid = UUID.fromString("c7e4ca32-36d9-4240-8e53-de44bef8eeeb")
                assertEquals(uuid, value.toUUIDOrNull())
            }

            @ParameterizedTest
            @ValueSource(
                strings = [
                    "",
                    "a",
                    "c7e4ca3236d942408e53de44bef8eeeba",
                    "c7e4ca32-36d9-4240-8e53-de44bef8eeeba"
                ]
            )
            fun `nulls if invalid`(value: String) {
                assertNull(value.toUUIDStrictOrNull())
            }

        }

    }

    @Nested
    inner class WithColor {

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                " ",
                "red"
            ]
        )
        fun `should wrap non empty string`(value: String) {
            val string = randomString()
            string withColor value shouldBe "<$value>$string</$value>"
        }

        @ParameterizedTest
        @ValueSource(
            strings = [
                "",
                " ",
                "red"
            ]
        )
        fun `should wrap empty string`(value: String) {
            "" withColor value shouldBe "<$value></$value>"
        }

    }

    @Nested
    inner class AsComponent {

        @Test
        fun `should transform non empty string`() {
            val string = randomString()
            string.asComponent() shouldBe Component.text(string)
        }

        @Test
        fun `should transform empty string`() {
            "".asComponent() shouldBe Component.empty()
        }

        @Test
        fun `should read mini message tag`() {
            val string = "<red><bold>hello</red>"
            string.asComponent() shouldBe Component.text("hello").color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
        }

        @Test
        fun `should use tag if defined`() {
            val string = "<red><test>hello"
            string.asComponent(
                Placeholder.parsed("test", "myvalue")
            ) shouldBe Component.text("myvaluehello").color(NamedTextColor.RED)
        }

        @Test
        fun `should use custom instance of mini message`() {
            val string = "<red><test>hello"

            val miniMessage = MiniMessage.builder()
                .tags(
                    TagResolver.resolver(
                        Placeholder.parsed("test", "myvalue")
                    )
                )
                .build()

            string.asComponent(miniMessage = miniMessage) shouldBe Component.text("<red>myvaluehello")
        }

    }

    @Nested
    inner class StringBuilderDeleteLast {

        @Test
        fun `should throw if size is under 0`() {
            assertThrows<IllegalArgumentException> {
                StringBuilder().deleteLast(-1)
            }
        }

        @Test
        fun `should return the same string builder if size is 0`() {
            val builder = StringBuilder().append(randomString())
            builder.deleteLast(0) shouldBe builder
        }


        @Test
        fun `should return the same string builder if size is bigger than length`() {
            val string = randomString()
            val builder = StringBuilder().append(string)
            builder.deleteLast(string.length)
            builder.length shouldBe 0
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8, 9])
        fun `should delete last char`(size: Int) {
            val string = randomString()
            val builder = StringBuilder().append(string)
            builder.deleteLast(size)
            builder.toString() shouldBe string.dropLast(size)
        }

    }
}
