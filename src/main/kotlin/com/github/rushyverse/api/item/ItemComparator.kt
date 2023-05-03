package com.github.rushyverse.api.item

import org.bukkit.inventory.ItemStack

/**
 * Allows to identify if an item is equivalent to another.
 */
public fun interface ItemComparator {

    public companion object {
        /**
         * Use the method [ItemStack.isSimilar] to identify if an item is equivalent to another.
         */
        public val SIMILAR: ItemComparator = ItemComparator(ItemStack::isSimilar)

        /**
         * Use the method [ItemStack.equals] to identify if an item is equivalent to another.
         */
        public val EQUALS: ItemComparator = ItemComparator(ItemStack::equals)
    }

    /**
     * Check if both items are equivalent.
     * @param item1 First item.
     * @param item2 Second item.
     * @return `true` if both items are equivalent, `false` otherwise.
     */
    public fun areSame(item1: ItemStack, item2: ItemStack): Boolean
}
