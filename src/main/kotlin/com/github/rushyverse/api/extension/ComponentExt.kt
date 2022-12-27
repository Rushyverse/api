package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Transform a component into a legacy text.
 * @receiver Component to transform.
 * @return The legacy text.
 */
public fun Component.toText(): String = LegacyComponentSerializer.legacySection().serialize(this)

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