package com.github.rushyverse.api.item

import com.github.rushyverse.api.extension.item
import com.github.rushyverse.api.item.exception.CraftResultMissingException
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Width of the craft table.
 */
private const val CRAFT_TABLE_WIDTH = 3

/**
 * Height of the craft table.
 */
private const val CRAFT_TABLE_HEIGHT = 3

/**
 * Size of the craft table.
 */
private const val CRAFT_TABLE_SIZE = CRAFT_TABLE_WIDTH * CRAFT_TABLE_HEIGHT

/**
 * Slots with index corresponding to the index defined in minecraft for the craft table.
 * @property index Index in the craft table.
 */
public enum class CraftSlot(public val index: UInt) {

    /**
     * Top left position on the crafting table.
     */
    TopLeft(0u),

    /**
     * Top position on the crafting table.
     */
    Top(1u),

    /**
     * Top right position on the crafting table.
     */
    TopRight(2u),

    /**
     * Center left position on the crafting table.
     */
    CenterLeft(3u),

    /**
     * Center position on the crafting table.
     */
    Center(4u),

    /**
     * Center right position on the crafting table.
     */
    CenterRight(5u),

    /**
     * Bottom left position on the crafting table.
     */
    BottomLeft(6u),

    /**
     * Bottom position on the crafting table.
     */
    Bottom(7u),

    /**
     * Bottom right position on the crafting table.
     */
    BottomRight(8u)
}

/**
 * Builder to create a [ShapedRecipe].
 * @property craft Array to define the position for each item.
 * @property result Result item of the crafting.
 */
public class CraftBuilder {

    /**
     * Storage of the item with the position assigned for the recipe's shape.
     * The top left position is defined by 0 and bottom right 8
     */
    private val craft: Array<ItemStack?> = arrayOfNulls(CRAFT_TABLE_SIZE)

    /**
     * Result item of the craft.
     */
    public var result: ItemStack? = null

    /**
     * Define an item stack with the material as a type at a position on the craft table.
     * @param positions Positions of the item.
     * @param material Item type.
     * @return The item created from the material.
     */
    public fun set(positions: Array<CraftSlot>, material: Material): ItemStack {
        return ItemStack(material).also {
            set(positions, item = it)
        }
    }

    /**
     * Define the item stack at a position on the craft table.
     * @param positions Positions of the item.
     * @param builder Item builder.
     * @return The item built from the builder.
     */
    public inline fun set(positions: Array<CraftSlot>, builder: ItemStack.() -> Unit): ItemStack {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return item(builder).also {
            set(positions, item = it)
        }
    }

    /**
     * Define the item stack at a position on the craft table.
     * @param positions Positions of the item.
     * @param item Item.
     */
    public fun set(positions: Array<CraftSlot>, item: ItemStack) {
        positions.forEach {
            set(it, item)
        }
    }

    /**
     * Define the item stack at a position on the craft table.
     * @param position Position of the item.
     * @param item Item.
     */
    public fun set(position: CraftSlot, item: ItemStack) {
        craft[position.index.toInt()] = item
    }

    /**
     * Define the value of the [result] property.
     * An item is built from the builder and assign it as a result of the craft.
     * @param builder Item builder.
     * @return The item built from the builder.
     */
    public inline fun result(builder: ItemStack.() -> Unit): ItemStack {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return item(builder).also(this::result)
    }

    /**
     * Define the value of the [result] property.
     * @param item Item assign to the result of the craft.
     */
    @Suppress("NOTHING_TO_INLINE")
    public inline fun result(item: ItemStack) {
        result = item
    }

    /**
     * Build the [ShapedRecipe] associated to the craft schema defined by the method [set].
     * @param key The unique recipe key.
     * @return Shape of the recipe built from the data of [craft].
     */
    public fun build(key: NamespacedKey): ShapedRecipe {
        val itemResult = result ?: throw CraftResultMissingException()
        val shaped = ShapedRecipe(key, itemResult)

        val keyItems = associateKeyWithItem()
        val lines = keysToCraftLines(keyItems)

        shaped.shape(*lines)
        setNotNullIngredient(keyItems, shaped)

        return shaped
    }

    /**
     * Set the ingredients in the [shaped] only if it is not null.
     * @param keyItems Map of association about keys and items.
     * @param shaped Shape where the ingredient will be set.
     */
    private fun setNotNullIngredient(
        keyItems: List<Pair<Char, ItemStack?>>,
        shaped: ShapedRecipe
    ) {
        keyItems.asSequence()
            .distinctBy { it.first }
            .forEach { (key, item) ->
                if (item != null) {
                    shaped.setIngredient(key, item)
                }
            }
    }

    /**
     * Transform the set of keys to a list of lines for the craft format.
     * The keys are chunked by size of 3 (3 items by line).
     * @param keys Keys associated to items.
     * @return Array of 3 [String] containing each 3 [Char].
     */
    private fun keysToCraftLines(keys: List<Pair<Char, ItemStack?>>): Array<String> =
        keys
            .asSequence()
            // [a,b,c,d,e,f,g,h,i]
            .map { it.first }
            // ["abcdefghi"]
            .joinToString(separator = "")
            // ["abc", "def", "ghi"]
            .chunked(CRAFT_TABLE_WIDTH)
            .toTypedArray()

    /**
     * Associated a key for each item in [craft].
     * If two items are similar, they will have the same key.
     * @return Map with the key associate to an item.
     */
    private fun associateKeyWithItem(): List<Pair<Char, ItemStack?>> {
        val itemKeys = LinkedHashMap<ItemStack, Char>(craft.size)
        var keyIncrement = 'A'
        return craft.map {
            (if (it == null) ' ' else itemKeys.getOrPut(it) { keyIncrement++ }) to it
        }
    }
}
