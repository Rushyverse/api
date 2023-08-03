package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

/**
 * Serializer for [Material].
 */
public object MaterialSerializer : KSerializer<Material> {

    private val stringSerializer: KSerializer<String> get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Material) {
        stringSerializer.serialize(encoder, value.name)
    }

    override fun deserialize(decoder: Decoder): Material {
        val value = stringSerializer.deserialize(decoder).uppercase().replace(" ", "_")
        return Material.getMaterial(value) ?: throw SerializationException("Unknown material: $value")
    }
}
