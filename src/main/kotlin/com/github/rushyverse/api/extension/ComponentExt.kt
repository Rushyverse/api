package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Transform a component into a legacy text.
 * @receiver Component to transform.
 * @return The legacy text.
 */
public fun Component.toText(): String = LegacyComponentSerializer.legacySection().serialize(this)

/**
 * Add the [bold][TextDecoration.BOLD] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)

/**
 * Remove the [bold][TextDecoration.BOLD] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [bold][TextDecoration.BOLD] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.NOT_SET)

/**
 * Add the [italic][TextDecoration.ITALIC] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)

/**
 * Remove the [italic][TextDecoration.ITALIC] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [italic][TextDecoration.ITALIC] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)

/**
 * Add the [underlined][TextDecoration.UNDERLINED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withUnderlined(): Component = this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)

/**
 * Remove the [underlined][TextDecoration.UNDERLINED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutUnderlined(): Component =
    this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [underlined][TextDecoration.UNDERLINED] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineUnderlined(): Component =
    this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.NOT_SET)

/**
 * Add the [strikethrough][TextDecoration.STRIKETHROUGH] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE)

/**
 * Remove the [strikethrough][TextDecoration.STRIKETHROUGH] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [strikethrough][TextDecoration.STRIKETHROUGH] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.NOT_SET)

/**
 * Add the [obfuscated][TextDecoration.OBFUSCATED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withObfuscated(): Component = this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE)

/**
 * Remove the [obfuscated][TextDecoration.OBFUSCATED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutObfuscated(): Component =
    this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [obfuscated][TextDecoration.OBFUSCATED] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineObfuscated(): Component =
    this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET)

/**
 * Add all decorations to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withDecorations(): Component =
    withBold().withItalic().withUnderlined().withStrikethrough().withObfuscated()

/**
 * Remove all decorations from the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutDecorations(): Component =
    withoutBold().withoutItalic().withoutUnderlined().withoutStrikethrough().withoutObfuscated()

/**
 * Set as [not set][TextDecoration.State.NOT_SET] all decorations of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineDecorations(): Component =
    undefineBold().undefineItalic().undefineUnderlined().undefineStrikethrough().undefineObfuscated()

/**
 * Create a new component using the string content.
 * @receiver String to transform.
 * @param extractUrls If true, will extract urls from the string and apply a clickable effect on them.
 * @param extractColors If true, will extract colors from the string and apply them to the component.
 * @param colorChar The character used to define a color.
 * @return A new text component.
 */
public fun String.toComponent(
    extractUrls: Boolean = false,
    extractColors: Boolean = true,
    colorChar: Char = LegacyComponentSerializer.AMPERSAND_CHAR,
): TextComponent = LegacyComponentSerializer.builder().apply {
    if (extractUrls) extractUrls()
    if (extractColors) character(colorChar)
}
    .build()
    .deserialize(this)