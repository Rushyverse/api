package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor

/**
 * Default max line for a lore line.
 * This value is defined by looking with the default Minecraft size application.
 */
public const val DEFAULT_LORE_LINE_LENGTH: Int = 30

/**
 * Transform a sequence of strings to a component.
 * Each string will be transformed into a component and then joined together by a new line.
 * @receiver The sequence of strings to transform.
 * @param transform The transform function to apply to each string.
 * @return A component that contains all the strings.
 */
public inline fun Sequence<String>.toLore(
    crossinline transform: TextComponent.Builder.() -> Unit = {
        color(NamedTextColor.GRAY)
    }
): List<TextComponent> {
    return map { Component.text().content(it).apply(transform).build() }.toList()
}

/**
 * Transform a collection of strings to a component.
 * Each string will be transformed into a component and then joined together by a new line.
 * @receiver The collection of strings to transform.
 * @param transform A function that will be applied to each component.
 * @return A component that contains all the strings.
 */
public inline fun Collection<String>.toLore(
    crossinline transform: TextComponent.Builder.() -> Unit = {
        color(NamedTextColor.GRAY)
    }
): List<TextComponent> {
    if (isEmpty()) return emptyList()
    return map { Component.text().content(it).apply(transform).build() }
}

/**
 * Transform a string into a list of string by cutting it.
 * If the string is too large and doesn't have any space, it will be cut each [maxSize] characters and a '-' will be added.
 * If the string contains a space, it will be cut at the space.
 * @receiver String to transform.
 * @param maxSize Max size of each string.
 * @return A list with strings with length less or equals to [maxSize].
 */
public fun String.toFormattedLore(maxSize: Int = DEFAULT_LORE_LINE_LENGTH): List<String> {
    return toFormattedLoreSequence(maxSize).toList()
}

/**
 * Transform a string into a sequence of string by cutting.
 * If the string is too large and doesn't have any space, it will be cut each [maxSize] characters and a '-' will be added.
 * If the string contains a space, it will be cut at the space.
 * @receiver String to transform.
 * @param maxSize Max size of each string.
 * @return A sequence with strings with length less or equals to [maxSize].
 */
public fun String.toFormattedLoreSequence(maxSize: Int = DEFAULT_LORE_LINE_LENGTH): Sequence<String> {
    if (isEmpty()) return emptySequence()
    if (length <= maxSize) return sequenceOf(this)

    var index = 0
    return sequence {
        while (index < length) {
            val nextIndex = index + maxSize
            if(nextIndex >= length) {
                yield(substring(index))
                break
            }

            val substringToNextIndex = substring(index, nextIndex)
            val substringBeforeLastSpace = substringToNextIndex.substringBeforeLast(' ')
            val nextChar = get(index + substringBeforeLastSpace.length)

            index += if (nextChar.isWhitespace()) {
                yield(substringBeforeLastSpace)
                // +1 to skip the space
                substringBeforeLastSpace.length + 1
            } else {
                yield(substringToNextIndex.dropLast(1) + '-')
                substringToNextIndex.lastIndex
            }
        }
    }
}