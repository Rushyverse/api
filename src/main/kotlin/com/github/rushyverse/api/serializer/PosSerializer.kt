package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.Location
import org.bukkit.World

/**
 * Serializer for [Pos].
 */
public object PosSerializer : KSerializer<Pos> {

    /**
     * Serializer for the coordinates x, y or z.
     */
    private val coordinateSerializer get() = Double.serializer()

    /**
     * Serializer for the rotations yaw or pitch.
     */
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

/**
 * Represents a position in a 3D space with additional yaw and pitch rotations.
 * Can be converted to a Bukkit [Location] with the provided [toLocation] method.
 *
 * @property x The x-coordinate of the position.
 * @property y The y-coordinate of the position.
 * @property z The z-coordinate of the position.
 * @property yaw The yaw rotation (horizontal rotation) in degrees.
 * @property pitch The pitch rotation (vertical rotation) in degrees.
 */
public data class Pos(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    /**
     * Converts this [Pos] object to a Bukkit [Location] object.
     *
     * @param world The [World] in which the position is located.
     * @return A new [Location] object representing the same position and rotation as this [Pos] object.
     */
    public fun toLocation(world: World): Location = Location(world, x, y, z, yaw, pitch)
}
