package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.serializer.PosSerializer.coordinateSerializer
import com.github.rushyverse.api.serializer.PosSerializer.rotationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.minestom.server.coordinate.Pos

/**
 * Serializer for [Pos].
 * @property coordinateSerializer Serializer for the coordinates x, y or z.
 * @property rotationSerializer Serializer for the rotations yaw or pitch.
 */
public object PosSerializer : KSerializer<Pos> {

    private val coordinateSerializer get() = Double.serializer()
    private val rotationSerializer get() = Float.serializer().nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("pos") {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        element("x", coordinateSerializer.descriptor)
        element("y", coordinateSerializer.descriptor)
        element("z", coordinateSerializer.descriptor)
        element("yaw", rotationSerializer.descriptor)
        element("pitch", rotationSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Pos) {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, coordinateSerializer, value.x)
            encodeSerializableElement(descriptor, 1, coordinateSerializer, value.y)
            encodeSerializableElement(descriptor, 2, coordinateSerializer, value.z)
            encodeSerializableElement(descriptor, 3, rotationSerializer, value.yaw)
            encodeSerializableElement(descriptor, 4, rotationSerializer, value.pitch)
        }
    }

    override fun deserialize(decoder: Decoder): Pos {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        return decoder.decodeStructure(descriptor) {
            var x: Double? = null
            var y: Double? = null
            var z: Double? = null
            var yaw: Float? = null
            var pitch: Float? = null

            if (decodeSequentially()) {
                x = decodeSerializableElement(descriptor, 0, coordinateSerializer)
                y = decodeSerializableElement(descriptor, 1, coordinateSerializer)
                z = decodeSerializableElement(descriptor, 2, coordinateSerializer)
                yaw = decodeSerializableElement(descriptor, 3, rotationSerializer)
                pitch = decodeSerializableElement(descriptor, 4, rotationSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> x = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        1 -> y = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        2 -> z = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        3 -> yaw = decodeSerializableElement(descriptor, index, rotationSerializer)
                        4 -> pitch = decodeSerializableElement(descriptor, index, rotationSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            Pos(
                x ?: throw SerializationException("The field x is missing"),
                y ?: throw SerializationException("The field y is missing"),
                z ?: throw SerializationException("The field z is missing"),
                yaw ?: 0f,
                pitch ?: 0f
            )
        }
    }
}