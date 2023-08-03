package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.DyeColor

/**
 * Serializer for [DyeColor].
 */
public object DyeColorSerializer : KSerializer<DyeColor> {

    private val stringSerializer: KSerializer<String> get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: DyeColor) {
        stringSerializer.serialize(encoder, value.name)
    }

    override fun deserialize(decoder: Decoder): DyeColor {
        val name = stringSerializer.deserialize(decoder).uppercase().replace(" ", "_")
        return DyeColor.valueOf(name)
    }
}
