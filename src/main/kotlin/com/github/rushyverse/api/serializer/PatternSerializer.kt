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
        element("color", dyeColorSerializer.descriptor)
        element("type", typeSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Pattern) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, dyeColorSerializer, value.color)
            encodeSerializableElement(descriptor, 1, typeSerializer, value.pattern)
        }
    }

    override fun deserialize(decoder: Decoder): Pattern {
        return decoder.decodeStructure(descriptor) {
            var color: DyeColor? = null
            var type: PatternType? = null

            if (decodeSequentially()) {
                color = decodeSerializableElement(descriptor, 0, dyeColorSerializer)
                type = decodeSerializableElement(descriptor, 1, typeSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> color = decodeSerializableElement(descriptor, index, dyeColorSerializer)
                        1 -> type = decodeSerializableElement(descriptor, index, typeSerializer)
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
