package com.github.rushyverse.api.item

import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ItemComparatorTest {

    @Test
    fun `similar should check the similarity without the amount`() {
        val item1 = ItemStack.of(Material.DIAMOND)
        val item2 = ItemStack.of(Material.DIAMOND)
        val item3 = ItemStack.of(Material.DIAMOND, 2)
        val item4 = ItemStack.of(Material.DIAMOND_SWORD)
        assertTrue(ItemComparator.SIMILAR.areSame(item1, item2))
        assertTrue(ItemComparator.SIMILAR.areSame(item1, item3))
        assertFalse(ItemComparator.SIMILAR.areSame(item1, item4))
    }

    @Test
    fun `equals should check the similarity with the amount`() {
        val item1 = ItemStack.of(Material.DIAMOND)
        val item2 = ItemStack.of(Material.DIAMOND)
        val item3 = ItemStack.of(Material.DIAMOND, 2)
        val item4 = ItemStack.of(Material.DIAMOND_SWORD)
        assertTrue(ItemComparator.EQUALS.areSame(item1, item2))
        assertFalse(ItemComparator.EQUALS.areSame(item1, item3))
        assertFalse(ItemComparator.EQUALS.areSame(item1, item4))
    }

    @Test
    fun `custom should respect the custom comparator`() {
        val item1 = ItemStack.of(Material.DIAMOND)
        val item2 = ItemStack.of(Material.DIAMOND)
        val item3 = ItemStack.of(Material.DIAMOND, 2)
        val item4 = ItemStack.of(Material.DIAMOND_SWORD)

        val comparator = ItemComparator { a, b -> a.material() == b.material() }
        assertTrue(comparator.areSame(item1, item2))
        assertTrue(comparator.areSame(item1, item3))
        assertFalse(comparator.areSame(item1, item4))
    }
}