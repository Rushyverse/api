package com.github.rushyverse.api.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Serializer for [Location].
 */
public object LocationSerializer : KSerializer<Location> {

    /**
     * Serializer for the coordinates x, y or z.
     */
    private val coordinateSerializer: KSerializer<Double> get() = Double.serializer()

    /**
     * Serializer for the rotations yaw or pitch.
     */
    private val rotationSerializer: KSerializer<Float?> = Float.serializer().nullable

    /**
     * Serializer for the world.
     */
    private val worldSerializer: KSerializer<String?> = String.serializer().nullable

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("location") {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        element("x", coordinateSerializer.descriptor)
        element("y", coordinateSerializer.descriptor)
        element("z", coordinateSerializer.descriptor)
        element("yaw", rotationSerializer.descriptor)
        element("pitch", rotationSerializer.descriptor)
        element("world", worldSerializer.descriptor)
    }

    override fun serialize(encoder: Encoder, value: Location) {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, coordinateSerializer, value.x)
            encodeSerializableElement(descriptor, 1, coordinateSerializer, value.y)
            encodeSerializableElement(descriptor, 2, coordinateSerializer, value.z)
            encodeSerializableElement(descriptor, 3, rotationSerializer, value.yaw)
            encodeSerializableElement(descriptor, 4, rotationSerializer, value.pitch)
            encodeSerializableElement(descriptor, 5, worldSerializer, value.world?.name)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Location {
        val coordinateSerializer = coordinateSerializer
        val rotationSerializer = rotationSerializer

        return decoder.decodeStructure(descriptor) {
            var x: Double? = null
            var y: Double? = null
            var z: Double? = null
            var yaw: Float? = null
            var pitch: Float? = null
            var world: String? = null

            if (decodeSequentially()) {
                x = decodeSerializableElement(descriptor, 0, coordinateSerializer)
                y = decodeSerializableElement(descriptor, 1, coordinateSerializer)
                z = decodeSerializableElement(descriptor, 2, coordinateSerializer)
                yaw = decodeSerializableElement(descriptor, 3, rotationSerializer)
                pitch = decodeSerializableElement(descriptor, 4, rotationSerializer)
                world = decodeSerializableElement(descriptor, 5, worldSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> x = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        1 -> y = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        2 -> z = decodeSerializableElement(descriptor, index, coordinateSerializer)
                        3 -> yaw = decodeSerializableElement(descriptor, index, rotationSerializer)
                        4 -> pitch = decodeSerializableElement(descriptor, index, rotationSerializer)
                        5 -> world = decodeSerializableElement(descriptor, index, worldSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            Location(
                world?.let { Bukkit.getWorld(it) },
                x ?: throw SerializationException("The field x is missing"),
                y ?: throw SerializationException("The field y is missing"),
                z ?: throw SerializationException("The field z is missing"),
                yaw ?: 0f,
                pitch ?: 0f
            )
        }
    }
}
