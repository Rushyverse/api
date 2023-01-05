package com.github.rushyverse.api.extension

import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack

/**
 * Define an unique component as lore.
 * @receiver ItemStack.
 * @param lore Lore to set.
 * @return The same item.
 */
public fun ItemStack.withLore(lore: Component): ItemStack = withLore(listOf(lore))