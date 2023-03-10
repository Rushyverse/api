package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minestom.server.entity.metadata.other.ItemFrameMeta

/**
 * Serializer for [ItemFrameMeta.Orientation].
 * To deserialize the orientation, it will be case-insensitive.
 */
public object ItemFrameMetaOrientationSerializer : KSerializer<ItemFrameMeta.Orientation> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("orientation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ItemFrameMeta.Orientation) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): ItemFrameMeta.Orientation {
        val decodeString = decoder.decodeString()
        return ItemFrameMeta.Orientation.values()
            .find { it.name.equals(decodeString, true) }
            ?: throw SerializationException("Invalid orientation")
    }
}