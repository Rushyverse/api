package com.github.rushyverse.api.item

import net.kyori.adventure.text.Component
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
        assertTrue(ItemComparator.SIMILAR.areSame(item1, item2))

        val item3 = ItemStack.of(Material.DIAMOND).withDisplayName(Component.text("Test"))
        assertFalse(ItemComparator.SIMILAR.areSame(item1, item3))

        val item4 = ItemStack.of(Material.DIAMOND, 2).withDisplayName(Component.text("Test"))
        assertTrue(ItemComparator.SIMILAR.areSame(item3, item4))

        val item5 = ItemStack.of(Material.DIAMOND_SWORD)
        assertFalse(ItemComparator.SIMILAR.areSame(item1, item5))
    }

    @Test
    fun `equals should check the similarity with the amount`() {
        val item1 = ItemStack.of(Material.DIAMOND)
        val item2 = ItemStack.of(Material.DIAMOND)
        assertTrue(ItemComparator.EQUALS.areSame(item1, item2))

        val item3 = ItemStack.of(Material.DIAMOND).withDisplayName(Component.text("Test"))
        assertFalse(ItemComparator.EQUALS.areSame(item1, item3))

        val item4 = ItemStack.of(Material.DIAMOND, 2).withDisplayName(Component.text("Test"))
        assertFalse(ItemComparator.EQUALS.areSame(item3, item4))

        val item5 = ItemStack.of(Material.DIAMOND_SWORD)
        assertFalse(ItemComparator.EQUALS.areSame(item1, item5))
    }

    @Test
    fun `custom should respect the custom comparator`() {
        val comparator = ItemComparator { a, b -> a.material() == b.material() }

        val item1 = ItemStack.of(Material.DIAMOND)
        val item2 = ItemStack.of(Material.DIAMOND)
        assertTrue(comparator.areSame(item1, item2))

        val item3 = ItemStack.of(Material.DIAMOND, 2)
        assertTrue(comparator.areSame(item1, item3))

        val item4 = ItemStack.of(Material.DIAMOND).withDisplayName(Component.text("Test"))
        assertTrue(comparator.areSame(item1, item4))

        val item5 = ItemStack.of(Material.DIAMOND_SWORD)
        assertFalse(comparator.areSame(item1, item5))
    }
}