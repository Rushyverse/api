package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.block.banner.PatternType

/**
 * Serializer for [PatternType].
 */
public object PatternTypeSerializer : KSerializer<PatternType> {

    private val enumSerializer = EnumSerializer("patternTypeEnum", PatternType.entries)

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "patternType",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: PatternType) {
        encoder.encodeString(value.identifier)
    }

    override fun deserialize(decoder: Decoder): PatternType {
        val key = decoder.decodeString()
        return PatternType.getByIdentifier(key.lowercase()) ?: enumSerializer.findEnumValue(key)
    }
}
