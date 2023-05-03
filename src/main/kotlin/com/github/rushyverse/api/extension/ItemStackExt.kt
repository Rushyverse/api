package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemStack

/**
 * Format the string [loreString] using [toFormattedLoreSequence] and transform it into a [TextComponent] using [toLore].
 * @receiver builder to transform.
 * @param loreString String that will be formatted and transform it to lore components.
 * @param lineLength The maximum length of each line.
 * @param transform A function that will be applied to each component.
 * @return The same builder with the lore modified.
 */
public inline fun ItemStack.formattedLore(
    loreString: String,
    lineLength: Int = DEFAULT_LORE_LINE_LENGTH,
    crossinline transform: TextComponent.Builder.() -> Unit = {
        color(NamedTextColor.GRAY)
    }
): ItemStack {
    val meta = this.itemMeta
    meta.lore(loreString.toFormattedLoreSequence(lineLength).toLore(transform))
    this.itemMeta = meta
    return this
}

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
): ItemStack {
    val meta = this.itemMeta
    meta.lore(loreString.toFormattedLoreSequence(lineLength).toLore(transform))
    this.itemMeta = meta
    return this
}

/**
 * Define an unique component as lore.
 * @receiver ItemStack.
 * @param lore Lore to set.
 * @return The same item.
 */
public fun ItemStack.withLore(lore: Component): ItemStack = apply {
    itemMeta = itemMeta.apply { lore(listOf(lore)) }
}
