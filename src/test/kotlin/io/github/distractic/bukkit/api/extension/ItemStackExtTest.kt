package io.github.distractic.bukkit.api.extension

import io.github.distractic.bukkit.api.utils.getRandomString
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemStackExtTest {

    @Test
    fun `get material property returns the value of type property`() {
        val item = item {}
        assertEquals(item.type, item.material)
        item.type = Material.IRON_SHOVEL
        assertEquals(item.type, item.material)
    }

    @Test
    fun `set material property define the value of type property`() {
        val item = item {}
        assertEquals(item.type, item.material)
        item.material = Material.IRON_SHOVEL
        assertEquals(item.type, item.material)
    }

    @Test
    fun `item function builder create init item stack with air material`() {
        val item = item {}
        assertEquals(Material.AIR, item.type)
    }

    @Test
    fun `item function builder set the properties of item stack`() {
        val expectedType = Material.STICK
        val expectedAmount = 42

        val item = item {
            type = expectedType
            amount = expectedAmount
        }

        assertEquals(expectedType, item.type)
        assertEquals(expectedAmount, item.amount)
    }

    @Test
    fun `ItemStack function builder create init item stack with air material`() {
        val item = ItemStack {}
        assertEquals(Material.AIR, item.type)
    }

    @Test
    fun `ItemStack function builder set the properties of item stack`() {
        val expectedType = Material.STICK
        val expectedAmount = 42

        val item = ItemStack {
            type = expectedType
            amount = expectedAmount
        }

        assertEquals(expectedType, item.type)
        assertEquals(expectedAmount, item.amount)
    }

    @Test
    fun `ItemStack function builder set the material by the parameter`() {
        val expectedType = Material.STICK
        val item = ItemStack(expectedType) { }
        assertEquals(expectedType, item.type)
    }

    @Test
    fun `ItemStack function builder set the properties by lambda`() {
        val expectedAmount = 42
        val item = ItemStack(Material.AIR) {
            amount = expectedAmount
        }
        assertEquals(expectedAmount, item.amount)
    }

    @Nested
    inner class FilterNotAir {

        @Test
        fun `array of items`() {
            val item1 = mockItem(Material.DIAMOND)
            val item2 = mockItem(Material.AMETHYST_BLOCK)
            assertEquals(
                listOf(item1, item2), arrayOf(
                    item1,
                    mockItem(Material.AIR),
                    item2,
                    mockItem(Material.AIR)
                ).filterNotAir()
            )
        }

        @Test
        fun `list of items`() {
            val item1 = mockItem(Material.DIAMOND_HELMET)
            val item2 = mockItem(Material.ATTACHED_PUMPKIN_STEM)
            assertEquals(
                listOf(item1, item2), listOf(
                    item1,
                    mockItem(Material.AIR),
                    item2,
                    mockItem(Material.AIR)
                ).filterNotAir()
            )
        }

        @Test
        fun `sequence of items`() {
            val item1 = mockItem(Material.BEDROCK)
            val item2 = mockItem(Material.ACACIA_LEAVES)
            assertEquals(
                listOf(item1, item2), listOf(
                    item1,
                    mockItem(Material.AIR),
                    item2,
                    mockItem(Material.AIR)
                ).filterNotAir()
            )
        }
    }

    @Nested
    inner class ItemIndexed {

        @Nested
        inner class ArrayNotNullItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptyArray<ItemStack>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.ACACIA_DOOR)
                val item2 = mockItem(Material.ACTIVATOR_RAIL)
                val array = arrayOf(
                    mockItem(Material.AIR),
                    item1,
                    mockItem(Material.AIR),
                    mockItem(Material.AIR),
                    item2
                )
                val map = array.itemsIndexed()
                val expectedMap = mapOf(
                    1 to item1,
                    4 to item2
                )

                assertEquals(expectedMap, map)
            }
        }

        @Nested
        inner class ArrayNullableItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptyArray<ItemStack?>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.ACACIA_DOOR)
                val item2 = mockItem(Material.ACTIVATOR_RAIL)
                val array = arrayOfNulls<ItemStack>(4)
                array[0] = null
                array[1] = item1
                array[2] = item2
                array[3] = mockItem(Material.AIR)

                val map = array.itemsIndexed()
                val expectedMap = mapOf(
                    1 to item1,
                    2 to item2
                )

                assertEquals(expectedMap, map)
            }
        }

        @Nested
        inner class SequenceNotNullItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptySequence<ItemStack>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.STICK)
                val item2 = mockItem(Material.AMETHYST_BLOCK)

                val sequence = sequenceOf(
                    item1,
                    mockItem(Material.AIR),
                    mockItem(Material.AIR),
                    item2,
                    mockItem(Material.AIR)
                )

                val map = sequence.itemsIndexed()
                val expectedMap = mapOf(
                    0 to item1,
                    3 to item2
                )

                assertEquals(expectedMap, map)
            }
        }

        @Nested
        inner class SequenceNullableItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptySequence<ItemStack?>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.STICK)
                val item2 = mockItem(Material.AMETHYST_BLOCK)

                val sequence = sequenceOf(
                    item1,
                    null,
                    mockItem(Material.AIR),
                    item2,
                    null
                )

                val map = sequence.itemsIndexed()
                val expectedMap = mapOf(
                    0 to item1,
                    3 to item2
                )

                assertEquals(expectedMap, map)
            }
        }

        @Nested
        inner class IterableNotNullItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptyList<ItemStack>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.BONE)
                val item2 = mockItem(Material.IRON_SWORD)

                val list = listOf(
                    item1,
                    mockItem(Material.AIR),
                    mockItem(Material.AIR),
                    item1,
                    item2,
                    mockItem(Material.AIR)
                )

                val map = list.itemsIndexed()
                val expectedMap = mapOf(
                    0 to item1,
                    3 to item1,
                    4 to item2
                )

                assertEquals(expectedMap, map)
            }
        }

        @Nested
        inner class IterableNullableItems {

            @Test
            fun `empty array returns empty map`() {
                assertEquals(emptyMap(), emptyList<ItemStack?>().itemsIndexed())
            }

            @Test
            fun `items not air is linked to the index`() {
                val item1 = mockItem(Material.BONE)
                val item2 = mockItem(Material.IRON_SWORD)

                val list = listOf(
                    item1,
                    null,
                    mockItem(Material.AIR),
                    item1,
                    item2,
                    mockItem(Material.AIR)
                )

                val map = list.itemsIndexed()
                val expectedMap = mapOf(
                    0 to item1,
                    3 to item1,
                    4 to item2
                )

                assertEquals(expectedMap, map)
            }
        }
    }

    private fun mockItem(material: Material): ItemStack {
        return mockk<ItemStack>(getRandomString()).apply {
            every { type } returns material
        }
    }
}