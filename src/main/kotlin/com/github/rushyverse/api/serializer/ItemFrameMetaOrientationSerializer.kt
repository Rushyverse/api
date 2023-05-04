package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.block.BlockFace

/**
 * Serializer for ItemFrame [BlockFace] orientation.
 * To deserialize the orientation, it will be case-insensitive.
 */
public object ItemFrameMetaOrientationSerializer : KSerializer<BlockFace> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("orientation", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BlockFace) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): BlockFace {
        val decodeString = decoder.decodeString()
        return BlockFace.values()
            .find { it.name.equals(decodeString, true) }
            ?: throw SerializationException("Invalid orientation")
    }
}