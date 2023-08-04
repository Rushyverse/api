package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType

/**
 * Serializer for [Pattern].
 */
public object PatternSerializer : KSerializer<Pattern> {

    private val typeSerializer: PatternTypeSerializer get() = PatternTypeSerializer

    private val dyeColorSerializer: DyeColorSerializer get() = DyeColorSerializer

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("pattern") {
        element("type", typeSerializer.descriptor)
        element("color", dyeColorSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Pattern) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, typeSerializer, value.pattern)
            encodeSerializableElement(descriptor, 1, dyeColorSerializer, value.color)
        }
    }

    override fun deserialize(decoder: Decoder): Pattern {
        return decoder.decodeStructure(descriptor) {
            var type: PatternType? = null
            var color: DyeColor? = null

            if (decodeSequentially()) {
                type = decodeSerializableElement(descriptor, 0, typeSerializer)
                color = decodeSerializableElement(descriptor, 1, dyeColorSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> type = decodeSerializableElement(descriptor, index, typeSerializer)
                        1 -> color = decodeSerializableElement(descriptor, index, dyeColorSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            Pattern(
                color ?: throw SerializationException("The field color is missing"),
                type ?: throw SerializationException("The field type is missing")
            )
        }
    }
}
