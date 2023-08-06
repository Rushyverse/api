package com.github.rushyverse.api.world

import com.github.rushyverse.api.serializer.LocationSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.Location
import kotlin.math.pow

/**
 * Serializer class for [SphereArea] objects.
 */
public object SphereAreaSerializer : KSerializer<SphereArea> {

    /**
     * Serializer for [Location].
     */
    private val locationSerializer get() = LocationSerializer

    /**
     * Serializer for [Double].
     */
    private val doubleSerializer get() = Double.serializer()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("sphere") {
        element("location", locationSerializer.descriptor)
        element("radius", doubleSerializer.descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): SphereArea {
        return decoder.decodeStructure(descriptor) {
            var location: Location? = null
            var radius: Double? = null

            if (decodeSequentially()) {
                location = decodeSerializableElement(descriptor, 0, locationSerializer)
                radius = decodeSerializableElement(descriptor, 1, doubleSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> location = decodeSerializableElement(descriptor, index, locationSerializer)
                        1 -> radius = decodeSerializableElement(descriptor, index, doubleSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            SphereArea(
                location ?: throw SerializationException("The field location is missing"),
                radius ?: throw SerializationException("The field radius is missing"),
            )
        }
    }

    override fun serialize(encoder: Encoder, value: SphereArea) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, locationSerializer, value.location)
            encodeSerializableElement(descriptor, 1, doubleSerializer, value.radius)
        }
    }

}

/**
 * Represents a spherical area with a certain radius.
 *
 * @property location The center location of the sphere.
 * @property radius The radius of the sphere.
 */
@Serializable(with = SphereAreaSerializer::class)
public class SphereArea(
    override var location: Location,
    radius: Double,
) : Area {

    public var radius: Double = radius
        set(value) {
            assertRadiusValue(value)
            field = value
        }

    init {
        assertRadiusValue(radius)
    }

    /**
     * Verifies that the new radius value is greater than or equal to 0.0.
     * @param value New radius value.
     */
    private fun assertRadiusValue(value: Double) {
        require(value >= 0.0) { "Radius must be greater than or equal to 0.0" }
    }

    override fun isInArea(location: Location): Boolean {
        val areaLocation = this.location
        return location.world === areaLocation.world // Same world
                && location.distanceSquared(areaLocation) <= radius.pow(2) // Distance is less than or equal to radius
    }

    override fun toString(): String {
        return "SphereArea(location=$location, radius=$radius)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SphereArea

        if (location != other.location) return false
        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }


}
