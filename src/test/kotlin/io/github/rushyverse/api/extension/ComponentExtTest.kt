package io.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertEquals

class ComponentExtTest {

    @Nested
    inner class StringToComponent {

        @Test
        fun `should return empty component if empty string`() {
            val component = "".toComponent()
            assertEquals(Component.empty(), component)
        }

        @ParameterizedTest
        @ValueSource(strings = ["Hello", "&#3b81f1Hello", "Hello https://www.youtube.com #3b81f1"])
        fun `should return component with text without legacy color`(value: String) {
            val component = value.toComponent(extractColors = false)
            assertEquals(Component.text(value), component)
        }

        @Test
        fun `should separate text and url`() {
            val component = "Hello https://www.youtube.com #3b81f1".toComponent(extractUrls = true, extractColors = false)
            val expected = Component.text("Hello ")
                .append(Component.text("https://www.youtube.com").clickEvent(ClickEvent.openUrl("https://www.youtube.com")))
                .append(Component.text(" #3b81f1"))
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
            val text = Component.text("Hello ").append(Component.text("https://www.youtube.com").clickEvent(ClickEvent.openUrl("https://www.youtube.com"))).toText()
            assertEquals("Hello https://www.youtube.com", text)
        }
    }
}