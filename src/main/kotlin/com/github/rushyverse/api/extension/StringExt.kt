package com.github.rushyverse.api.extension

import kotlin.math.min

/**
 * Transform a string into a list of string by cutting it.
 * @receiver String to transform.
 * @param maxSize Max size of each string.
 * @return A list with strings with length less or equals to [maxSize].
 */
public fun String.toFormattedLore(maxSize: Int) : List<String> {
    return toFormattedLoreSequence(maxSize).toList()
}

/**
 * Transform a string into a sequence of string by cutting.
 * @receiver String to transform.
 * @param maxSize Max size of each string.
 * @return A sequence with strings with length less or equals to [maxSize].
 */
public fun String.toFormattedLoreSequence(maxSize: Int): Sequence<String> {
    if(isEmpty()) return emptySequence()
    if(lastIndex <= maxSize) return sequenceOf(this)

    var index = 0
    return sequence {
        while(index < length) {
            val minSubstringLength = min(length, index + maxSize)
            val maxStringLine = substring(index, minSubstringLength)

            val substringBeforeSpace = maxStringLine.substringBeforeLast(' ')
            val nextChar = getOrNull(index + substringBeforeSpace.length)

            index += if(nextChar == null || nextChar.isWhitespace()) {
                yield(substringBeforeSpace)
                // +1 to skip the space
                substringBeforeSpace.length + 1
            } else {
                yield(maxStringLine.dropLast(1) + "-")
                maxStringLine.lastIndex
            }
        }
    }
}