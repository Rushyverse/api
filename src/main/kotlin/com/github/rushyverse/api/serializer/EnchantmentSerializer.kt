package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

/**
 * Serializer for [Enchantment].
 */
public object EnchantmentSerializer : KSerializer<Enchantment> {

    override val descriptor: SerialDescriptor get() = NamespacedSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Enchantment) {
        NamespacedSerializer.serialize(encoder, value.key)
    }

    override fun deserialize(decoder: Decoder): Enchantment {
        val key = NamespacedSerializer.deserialize(decoder) as NamespacedKey
        return Enchantment.getByKey(key)
            ?: throw SerializationException("Unable to find enchantment with namespaced key: $key")
    }
}
