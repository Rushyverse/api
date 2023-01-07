package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.item.ItemStack

/**
 * Format the string [loreString] using [toFormattedLoreSequence] and transform it into a [TextComponent] using [toLore].
 * @receiver builder to transform.
 * @param loreString String that will be formatted and transform it to lore components.
 * @param lineLength The maximum length of each line.
 * @param transform A function that will be applied to each component.
 * @return The same builder with the lore modified.
 */
public inline fun ItemStack.Builder.formattedLore(
    loreString: String,
    lineLength: Int = DEFAULT_LORE_LINE_LENGTH,
    crossinline transform: TextComponent.Builder.() -> Unit = {
        color(NamedTextColor.GRAY)
    }
): ItemStack.Builder = lore(loreString.toFormattedLoreSequence(lineLength).toLore(transform))

/**
 * Format the string [loreString] using [toFormattedLoreSequence] and transform it into a [TextComponent] using [toLore].
 * @receiver ItemStack to transform.
 * @param loreString String that will be formatted and transform it to lore components.
 * @param lineLength The maximum length of each line.
 * @param transform A function that will be applied to each component.
 * @return The same ItemStack with the lore modified.
 */
public inline fun ItemStack.withFormattedLore(
    loreString: String,
    lineLength: Int = DEFAULT_LORE_LINE_LENGTH,
    crossinline transform: TextComponent.Builder.() -> Unit = {
        color(NamedTextColor.GRAY)
    }
): ItemStack = withLore(loreString.toFormattedLoreSequence(lineLength).toLore(transform))

/**
 * Define an unique component as lore.
 * @receiver ItemStack.
 * @param lore Lore to set.
 * @return The same item.
 */
public fun ItemStack.withLore(lore: Component): ItemStack = withLore(listOf(lore))