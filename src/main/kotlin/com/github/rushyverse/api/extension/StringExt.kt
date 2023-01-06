package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import kotlin.math.min

/**
 * Transform a sequence of strings to a component.
 * Each string will be transformed into a component and then joined together by a new line.
 * @receiver The sequence of strings to transform.
 * @param transform The transform function to apply to each string.
 * @return A component that contains all the strings.
 */
public inline fun Sequence<String>.toLore(crossinline transform: TextComponent.Builder.() -> Unit = {}): Component {
    val components = map { Component.text().content(it).apply(transform) }.toList()
    if(components.isEmpty()) return Component.empty()
    return Component.join(JoinConfiguration.separator(Component.newline()), components)
}

/**
 * Transform a collection of strings to a component.
 * Each string will be transformed into a component and then joined together by a new line.
 * @receiver The collection of strings to transform.
 * @param transform A function that will be applied to each component.
 * @return A component that contains all the strings.
 */
public inline fun Collection<String>.toLore(crossinline transform: TextComponent.Builder.() -> Unit = {}): Component {
    if (isEmpty()) return Component.empty()
    val components = map { Component.text().content(it).apply(transform) }
    return Component.join(JoinConfiguration.separator(Component.newline()), components)
}

/**
 * Transform a string into a list of string by cutting it.
 * If the string is too large and doesn't have any space, it will be cut each [maxSize] characters and a '-' will be added.
 * If the string contains a space, it will be cut at the space.
 * @receiver String to transform.
 * @param maxSize Max size of each string.
 * @return A list with strings with length less or equals to [maxSize].
 */
public fun String.toFormattedLore(maxSize: Int): List<String> {
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
public fun String.toFormattedLoreSequence(maxSize: Int): Sequence<String> {
    if (isEmpty()) return emptySequence()
    if (lastIndex <= maxSize) return sequenceOf(this)

    var index = 0
    return sequence {
        while (index < length) {
            val minSubstringLength = min(length, index + maxSize)
            val maxStringLine = substring(index, minSubstringLength)

            val substringBeforeSpace = maxStringLine.substringBeforeLast(' ')
            val nextChar = getOrNull(index + substringBeforeSpace.length)

            index += if (nextChar == null || nextChar.isWhitespace()) {
                yield(substringBeforeSpace)
                // +1 to skip the space
                substringBeforeSpace.length + 1
            } else {
                yield(maxStringLine.dropLast(1) + '-')
                maxStringLine.lastIndex
            }
        }
    }
}