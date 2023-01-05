package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentExtTest {

    @Nested
    inner class ChangeDecoration {

        @Nested
        inner class Bold {

            @Test
            fun `should add bold`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.BOLD, TextDecoration.State.TRUE),
                    Component.text("Hello").withBold()
                )
            }

            @Test
            fun `should remove bold`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.BOLD, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutBold()
                )
            }

            @Test
            fun `should undefine bold`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.BOLD, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineBold()
                )
            }

        }

        @Nested
        inner class Italic {

            @Test
            fun `should add italic`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE),
                    Component.text("Hello").withItalic()
                )
            }

            @Test
            fun `should remove italic`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutItalic()
                )
            }

            @Test
            fun `should undefine italic`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineItalic()
                )
            }

        }

        @Nested
        inner class Underlined {

            @Test
            fun `should add underlined`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE),
                    Component.text("Hello").withUnderlined()
                )
            }

            @Test
            fun `should remove underlined`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutUnderlined()
                )
            }

            @Test
            fun `should undefine underlined`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.UNDERLINED, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineUnderlined()
                )
            }

        }

        @Nested
        inner class Strikethrough {

            @Test
            fun `should add strikethrough`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE),
                    Component.text("Hello").withStrikethrough()
                )
            }

            @Test
            fun `should remove strikethrough`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutStrikethrough()
                )
            }

            @Test
            fun `should undefine strikethrough`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineStrikethrough()
                )
            }

        }

        @Nested
        inner class Obfuscated {

            @Test
            fun `should add obfuscated`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE),
                    Component.text("Hello").withObfuscated()
                )
            }

            @Test
            fun `should remove obfuscated`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutObfuscated()
                )
            }

            @Test
            fun `should undefine obfuscated`() {
                assertEquals(
                    Component.text("Hello").decoration(TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineObfuscated()
                )
            }

        }

        @Nested
        inner class Decorations {

            @Test
            fun `should add all decorations`() {
                assertEquals(
                    Component.text("Hello")
                        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)
                        .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE)
                        .decoration(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE),
                    Component.text("Hello").withDecorations()
                )
            }

            @Test
            fun `should remove all decorations`() {
                assertEquals(
                    Component.text("Hello")
                        .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)
                        .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE)
                        .decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE),
                    Component.text("Hello").withoutDecorations()
                )
            }

            @Test
            fun `should undefine all decorations`() {
                assertEquals(
                    Component.text("Hello")
                        .decoration(TextDecoration.BOLD, TextDecoration.State.NOT_SET)
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.NOT_SET)
                        .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.NOT_SET)
                        .decoration(TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET),
                    Component.text("Hello").undefineDecorations()
                )
            }

        }
    }

    @Nested
    inner class StringToComponent {

        @Test
        fun `should return empty component if empty string`() {
            val component = "".toComponent()
            assertEquals(Component.empty(), component)
        }

        @ParameterizedTest
        @ValueSource(strings = ["Hello", "&#3b81f1Hello", "Hello https://www.youtube.com #3b81f1"])
        fun `should return component with text without extract color`(value: String) {
            val component = value.toComponent(extractColors = false)
            assertEquals(Component.text(value), component)
        }

        @Test
        fun `should separate text and url`() {
            val component =
                "Hello https://www.youtube.com &#3b81f1".toComponent(extractUrls = true, extractColors = false)
            val expected = Component.text("Hello ")
                .append(
                    Component.text("https://www.youtube.com").clickEvent(ClickEvent.openUrl("https://www.youtube.com"))
                )
                .append(Component.text(" &#3b81f1"))
            assertEquals(expected, component)
        }

        @Test
        fun `should return component with text with legacy color`() {
            val component = "&cHello".toComponent()
            assertEquals(Component.text("Hello").color(NamedTextColor.RED), component)
        }

        @Test
        fun `should return component with text with hex color`() {
            val component = "&#3b81f1Hello".toComponent()
            assertEquals(Component.text("Hello").color(TextColor.fromHexString("#3b81f1")), component)
        }

        @Test
        fun `should return component with text if legacy colors is disabled`() {
            val component = "&cHello".toComponent(extractColors = false)
            assertEquals(Component.text("&cHello"), component)
        }

        @Test
        fun `should return component with text using another color character`() {
            val component = "^#3b81f1Hello".toComponent(colorChar = '^')
            assertEquals(Component.text("Hello").color(TextColor.fromHexString("#3b81f1")), component)
        }
    }

    @Nested
    inner class ComponentToText {

        @Test
        fun `should return empty string if empty component`() {
            val text = Component.empty().toText()
            assertEquals("", text)
        }

        @Test
        fun `should return text without legacy color`() {
            val text = Component.text("Hello").toText()
            assertEquals("Hello", text)
        }

        @Test
        fun `should return text with legacy color`() {
            val text = Component.text("Hello").color(NamedTextColor.RED).toText()
            assertEquals("§cHello", text)
        }

        @Test
        fun `should return text with hex color`() {
            val text = Component.text("Hello").color(TextColor.fromHexString("#3b81f1")).toText()
            assertEquals("§9Hello", text)
        }

        @Test
        fun `should return text with url`() {
            val text = Component.text("Hello ").append(
                Component.text("https://www.youtube.com").clickEvent(ClickEvent.openUrl("https://www.youtube.com"))
            ).toText()
            assertEquals("Hello https://www.youtube.com", text)
        }
    }
}