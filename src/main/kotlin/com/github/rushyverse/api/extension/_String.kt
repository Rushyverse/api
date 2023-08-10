@file:JvmName("StringUtils")
@file:JvmMultifileClass

package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import java.math.BigInteger
import java.util.*

/**
 * MiniMessage instance to deserialize components without strict mode.
 */
public val MINI_MESSAGE_NON_STRICT: MiniMessage = MiniMessage.builder()
    .strict(false)
    .tags(StandardTags.defaults())
    .build()

/**
 * Length of a UUID.
 */
public const val UUID_SIZE: Int = 36

/**
 * Number of dashes in a UUID.
 */
private const val NUMBER_DASHES_UUID = 4

/**
 * Radix for hexadecimal numbers.
 */
private const val HEXADECIMAL_RADIX = 16

/**
 * The constant value representing the number of bits in a UUID high and low bits.
 */
private const val UUID_HIGH_LOW_BITS: Int = 64

/**
 * Default max line for a lore line.
 * This value is defined by looking with the default Minecraft size application.
 */
public const val DEFAULT_LORE_LINE_LENGTH: Int = 30

/**
 * Converts the receiver String to Int if possible, otherwise returns the same String.
 *
 * @receiver The input String.
 * @return The converted Int value if successful or the original String itself if not.
 */
public fun String.toIntOrString(): Any = toIntOrNull() ?: this

/**
 * Wraps a given string with a color tag.
 * Example: "Hello".wrapColorWith("red") will return `<red>Hello</red>`.
 *
 * @receiver The string to be wrapped.
 * @param color The color to use for wrapping.
 * @return The original string wrapped with the color tag.
 */
public infix fun String.withColor(color: String): String = "<$color>$this</$color>"

/***
 * Encodes the specified byte array into a String using the [Base64] encoding scheme.
 * @receiver String to encode.
 * @return A String containing the resulting Base64 encoded characters.
 */
public fun String.encodeBase64ToString(): String = Base64.getEncoder().encodeToString(this.toByteArray(Charsets.UTF_8))

/***
 * Encodes the specified byte array into a String using the [Base64] encoding scheme.
 * @receiver Array of byte to encode.
 * @return A byte array containing the resulting Base64 encoded characters.
 */
public fun ByteArray.encodeBase64ToString(): String = Base64.getEncoder().encodeToString(this)

/**
 * Decodes a Base64 encoded String into a newly-allocated byte array using the [Base64] encoding scheme.
 * @receiver String to decode.
 * @return A new String containing the decoded bytes.
 */
public fun String.decodeBase64ToString(): String = Base64.getDecoder().decode(this).decodeToString()

/**
 * Decodes a Base64 encoded String into a newly-allocated byte array using the [Base64] encoding scheme.
 * @receiver Array of byte< to decode.
 * @return A byte array containing the decoded bytes.
 */
public fun String.decodeBase64Bytes(): ByteArray = Base64.getDecoder().decode(this)

/**
 * Creates a [UUID] from the string standard.
 * The string must have the strict format of UUID (with dashes).
 * If the string cannot be converted to UUID, returns null.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value or null if the format is not valid.
 */
public fun String.toUUIDStrictOrNull(): UUID? = try {
    toUUIDStrict()
} catch (_: Exception) {
    null
}

/**
 * Creates a [UUID] from the string standard.
 * The string must have the strict format of UUID (with dashes).
 * If the string cannot be converted to UUID, throw an exception.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value.
 * @throws IllegalArgumentException Exception if the value is not a valid uuid.
 */
@Throws(IllegalArgumentException::class)
public fun String.toUUIDStrict(): UUID = UUID.fromString(this)

/**
 * Creates a [UUID] from a string.
 * The string can have dashes or not.
 * If the string cannot be converted to UUID, returns null.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value or null if the format is not valid.
 */
public fun String.toUUIDOrNull(): UUID? = try {
    toUUID()
} catch (_: Exception) {
    null
}

/**
 * Creates a [UUID] from a string.
 * The string can have dashes or not.
 * If the string cannot be converted to UUID, throw an exception.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value.
 * @throws IllegalArgumentException Exception if the value is not a valid uuid.
 */
@Throws(IllegalArgumentException::class)
public fun String.toUUID(): UUID {
    val length = this.length
    if (length == UUID_SIZE) {
        return toUUIDStrict()
    } else {
        if (length == UUID_SIZE - NUMBER_DASHES_UUID) { // -4 because of dashes
            val idHex = BigInteger(this, HEXADECIMAL_RADIX)
            return UUID(idHex.shiftRight(UUID_HIGH_LOW_BITS).toLong(), idHex.toLong())
        }
    }
    throw IllegalArgumentException("Invalid UUID format: $this")
}

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
 * If the string is too large and doesn't have any space,
 * it will be cut each [lineLength] characters and a '-' will be added.
 * If the string contains a space, it will be cut at the space.
 * @receiver String to transform.
 * @param lineLength Max size of each string.
 * @return A list with strings with length less or equals to [lineLength].
 */
public fun String.toFormattedLore(lineLength: Int = DEFAULT_LORE_LINE_LENGTH): List<String> {
    return toFormattedLoreSequence(lineLength).toList()
}

/**
 * Transform a string into a sequence of string by cutting.
 * If the string is too large and doesn't have any space,
 * it will be cut each [lineLength] characters and a '-' will be added.
 * If the string contains a space, it will be cut at the space.
 * @receiver String to transform.
 * @param lineLength Max size of each string.
 * @return A sequence with strings with length less or equals to [lineLength].
 */
public fun String.toFormattedLoreSequence(lineLength: Int = DEFAULT_LORE_LINE_LENGTH): Sequence<String> {
    if (isEmpty()) return emptySequence()
    if (length <= lineLength) return sequenceOf(this)

    var index = 0
    return sequence {
        while (index < length) {
            val nextIndex = index + lineLength
            if (nextIndex >= length) {
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

/**
 * Transforms a string into a component using MiniMessage.
 * Will set the color according to the tag in the string.
 * The [tagResolver] will be used to resolve the custom tags and replace values.
 * @receiver The string used to create the component.
 * @param tagResolver The tag resolver used to resolve the custom tags.
 * @param miniMessage The mini message instance used to parse the string.
 * @return The component created from the string.
 */
public fun String.asComponent(
    vararg tagResolver: TagResolver,
    miniMessage: MiniMessage = MINI_MESSAGE_NON_STRICT,
): Component = miniMessage.deserialize(this, *tagResolver)
