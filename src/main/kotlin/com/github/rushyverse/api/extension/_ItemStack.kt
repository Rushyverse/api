package com.github.rushyverse.api.extension

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.ByteArrayOutputStream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Type of this item.
 */
public var ItemStack.material: Material
    get() = type
    set(value) {
        type = value
    }

/**
 * Utility method to build [ItemStack].
 * @param builder Builder function.
 * @return Instance of the item who is created.
 */
public inline fun item(builder: ItemStack.() -> Unit): ItemStack {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return ItemStack(builder)
}

/**
 * Utility method to build [ItemStack].
 * @param builder Builder function.
 * @return Instance of the item who is created.
 */
public inline fun ItemStack(builder: ItemStack.() -> Unit): ItemStack {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return ItemStack(Material.AIR, builder)
}

/**
 * Utility method to build [ItemStack].
 * @param material Item material.
 * @param builder Builder function.
 * @return Instance of the item who is created.
 */
public inline fun ItemStack(material: Material, builder: ItemStack.() -> Unit): ItemStack {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return ItemStack(material).apply(builder)
}

/**
 * Filter the array to retrieve items with only a type different of [Material.AIR].
 * @receiver Array of items.
 * @return List of item without type [Material.AIR].
 */
public fun <T : ItemStack> Array<out T>.filterNotAir(): List<T> = filter { it.type != Material.AIR }

/**
 * Filter the iterable to retrieve items with only a type different of [Material.AIR].
 * @receiver Iterable of items.
 * @return List of item without type [Material.AIR].
 */
public fun <T : ItemStack> Iterable<T>.filterNotAir(): List<T> = filter { it.type != Material.AIR }

/**
 * Filter the sequence to retrieve items with only a type different of [Material.AIR].
 * @receiver Sequence of items.
 * @return Sequence of item without type [Material.AIR].
 */
public fun <T : ItemStack> Sequence<T>.filterNotAir(): Sequence<T> = filter { it.type != Material.AIR }

/**
 * Get the content of the array to transform it into a map with the index and the item.
 * Only the item not null and with a type different of [Material.AIR] is insert into the result.
 * @receiver Array of item.
 * @return Map of item with index.
 */
public fun <T : ItemStack> Array<out T?>.itemsIndexed(): Map<Int, T> {
    return asIterable().itemsIndexed()
}

/**
 * Get the content of the sequence to transform it into a map with the index and the item.
 * Only the item not null and with a type different of [Material.AIR] is insert into the result.
 * @receiver Sequence of item.
 * @return Map of item with index.
 */
public fun <T : ItemStack> Sequence<T?>.itemsIndexed(): Map<Int, T> {
    return asIterable().itemsIndexed()
}

/**
 * Get the content of the iterable instance to transform it into a map with the index and the item.
 * Only the item not null and with a type different of [Material.AIR] is insert into the result.
 * @receiver Iterable of item.
 * @return Map of item with index.
 */
public fun <T : ItemStack> Iterable<T?>.itemsIndexed(): Map<Int, T> {
    val result = mutableMapOf<Int, T>()
    forEachIndexed { index, item ->
        if (item != null && item.type != Material.AIR) {
            result[index] = item
        }
    }
    return result
}

/**
 * Serialize a map of item with index to an encoded String with Base64.
 * @receiver Items with indexes.
 * @return The String encoded with items and indexes serialized.
 */
public fun Map<Int, ItemStack>.serializeToBase64(): String {
    return serializeToBytes().encodeBase64ToString()
}

/**
 * Serialize a map of item with index to a byte array.
 * @receiver Items with indexes.
 * @return Byte array of items and indexes serialized.
 */
public fun Map<Int, ItemStack>.serializeToBytes(): ByteArray {
    return ByteArrayOutputStream().use { os ->
        os.buffered().use {
            it.write(size)

            forEach { (index, item) ->
                it.write(index)
                val bytes = item.serializeAsBytes()
                it.write(bytes.size)
                it.write(bytes)
            }

            it.flush()
            os.toByteArray()
        }
    }
}

/**
 * Deserialize an encoded String with Base64 to a map of items and indexes.
 * @receiver Items with indexes serialized.
 * @return The map built from the String deserialized.
 */
public fun String.deserializeBase64ItemsIndexed(): Map<Int, ItemStack> {
    return decodeBase64Bytes().deserializeItemsIndexed()
}

/**
 * Deserialize a byte array to a map of items and indexes.
 * @receiver Items with indexes serialized.
 * @return The map built from the byte array.
 */
public fun ByteArray.deserializeItemsIndexed(): Map<Int, ItemStack> {
    return inputStream().buffered().use {
        val size = it.read()
        val itemsIndexed = HashMap<Int, ItemStack>(size)

        for (index in 0 until size) {
            itemsIndexed[it.read()] = ItemStack.deserializeBytes(it.readNBytes(it.read()))
        }

        itemsIndexed
    }
}

/**
 * Serialize an array of item to an encoded String with Base64.
 *
 * @receiver Items.
 * @return The String encoded with items serialized.
 */
public fun Array<out ItemStack>.serializeToBase64(): String {
    return serializeToBytes().encodeBase64ToString()
}

/**
 * Serialize an array of item to a byte array.
 * @receiver Items.
 * @return Byte array of items serialized.
 */
public fun Array<out ItemStack>.serializeToBytes(): ByteArray {
    return serializeItemsToBytes(iterator()) { size }
}

/**
 * Serialize a collection of item to an encoded String with Base64.
 * @receiver Items.
 * @return The String encoded with items serialized.
 */
public fun Collection<ItemStack>.serializeToBase64(): String {
    return serializeToBytes().encodeBase64ToString()
}

/**
 * Serialize a collection of item to a byte array.
 * @receiver Items.
 * @return Byte array of items serialized.
 */
public fun Collection<ItemStack>.serializeToBytes(): ByteArray {
    return serializeItemsToBytes(iterator()) { size }
}

/**
 * Serialize a set of item to a byte array.
 * @param items Iterator of items.
 * @param size Get the size of the iterator.
 * @return Byte array of items serialized.
 */
public inline fun serializeItemsToBytes(items: Iterator<ItemStack>, size: () -> Int): ByteArray {
    return ByteArrayOutputStream().use { os ->
        os.buffered().use {
            val itSize = size()
            it.write(itSize)

            var counter = 0
            while (items.hasNext() && counter < itSize) {
                // With some part of the bukkit api, the item array
                // is noted as @NotNull, but the content can be null.
                val item = items.next()
                val bytes = item.serializeAsBytes()
                it.write(bytes.size)
                it.write(bytes)
                counter++
            }

            it.flush()
            os.toByteArray()
        }
    }
}

/**
 * Deserialize an encoded String with Base64 to a list of items.
 * @receiver Items serialized.
 * @return The list built from the String deserialized.
 */
public fun String.deserializeBase64ItemsToList(): List<ItemStack> {
    return decodeBase64Bytes().deserializeItemsToList()
}

/**
 * Deserialize a byte array to a list of items.
 * @receiver Items serialized.
 * @return The list built from the byte array.
 */
public fun ByteArray.deserializeItemsToList(): List<ItemStack> {
    return inputStream().buffered().use {
        List(it.read()) { _ -> ItemStack.deserializeBytes(it.readNBytes(it.read())) }
    }
}

/**
 * Deserialize an encoded String with Base64 to an array of items.
 * @receiver Items serialized.
 * @return The array built from the String deserialized.
 */
public fun String.deserializeBase64ItemsToArray(): Array<ItemStack> {
    return decodeBase64Bytes().deserializeItemsToArray()
}

/**
 * Deserialize a byte array to an array of items.
 * @receiver Items serialized.
 * @return The array built from the byte array.
 */
public fun ByteArray.deserializeItemsToArray(): Array<ItemStack> {
    return inputStream().buffered().use {
        Array(it.read()) { _ -> ItemStack.deserializeBytes(it.readNBytes(it.read())) }
    }
}