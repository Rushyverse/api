package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

/**
 * Serializer for [ClosedRange] with [Double] value.
 */
public object RangeDoubleSerializer : KSerializer<ClosedRange<Double>> {

    /**
     * Serializer for [Double].
     */
    private val doubleSerializer get() = Double.serializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("range") {
        val doubleSerializer = doubleSerializer

        element("start", doubleSerializer.descriptor)
        element("end", doubleSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: ClosedRange<Double>) {
        val doubleSerializer = doubleSerializer

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, doubleSerializer, value.start)
            encodeSerializableElement(descriptor, 1, doubleSerializer, value.endInclusive)
        }
    }

    override fun deserialize(decoder: Decoder): ClosedRange<Double> {
        val doubleSerializer = doubleSerializer

        return decoder.decodeStructure(descriptor) {
            var start: Double? = null
            var end: Double? = null

            if (decodeSequentially()) {
                start = decodeSerializableElement(descriptor, 0, doubleSerializer)
                end = decodeSerializableElement(descriptor, 1, doubleSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> start = decodeSerializableElement(descriptor, index, doubleSerializer)
                        1 -> end = decodeSerializableElement(descriptor, index, doubleSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            start ?: throw SerializationException("The field start is missing")
            end ?: throw SerializationException("The field end is missing")

            start..end
        }
    }
}
