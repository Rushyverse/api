package com.github.rushyverse.api.serializer

import com.destroystokyo.paper.Namespaced
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey

/**
 * Serializer for [Namespaced].
 */
public object NamespacedSerializer : KSerializer<Namespaced> {

    private val stringSerializer get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Namespaced) {
        stringSerializer.serialize(encoder, value.key)
    }

    override fun deserialize(decoder: Decoder): Namespaced {
        return stringSerializer.deserialize(decoder).let {
            val value = it.split(NamespacedKey.DEFAULT_SEPARATOR)
            if (value.size == 2) {
                NamespacedKey(value[0], value[1])
            } else {
                NamespacedKey.minecraft(it)
            }
        }
    }
}
