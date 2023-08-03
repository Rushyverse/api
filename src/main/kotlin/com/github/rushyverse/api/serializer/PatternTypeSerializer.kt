package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.block.banner.PatternType

/**
 * Serializer for [PatternType].
 */
public object PatternTypeSerializer : KSerializer<PatternType> {

    private val stringSerializer: KSerializer<String> get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: PatternType) {
        stringSerializer.serialize(encoder, value.identifier)
    }

    override fun deserialize(decoder: Decoder): PatternType {
        val key = stringSerializer.deserialize(decoder)
        return PatternType.getByIdentifier(key) ?: throw SerializationException("The pattern type $key does not exist.")
    }
}
