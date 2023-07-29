package com.github.rushyverse.api.world

import com.github.rushyverse.api.serializer.LocationSerializer
import com.github.rushyverse.api.serializer.RangeDoubleSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.Location
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Serializer class for [CylinderArea] objects.
 */
public object CylinderAreaSerializer : KSerializer<CylinderArea> {

    /**
     * Serializer for [Location].
     */
    private val locationSerializer get() = LocationSerializer

    /**
     * Serializer for [Double].
     */
    private val doubleSerializer get() = Double.serializer()

    /**
     * Serializer for [ClosedRange] with [Double] value.
     */
    private val rangeDoubleSerializer get() = RangeDoubleSerializer

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("cylinder") {
        element("location", locationSerializer.descriptor)
        element("radius", doubleSerializer.descriptor)
        element("height", rangeDoubleSerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): CylinderArea {
        return decoder.decodeStructure(descriptor) {
            var location: Location? = null
            var radius: Double? = null
            var height: ClosedRange<Double>? = null

            if (decodeSequentially()) {
                location = decodeSerializableElement(descriptor, 0, locationSerializer)
                radius = decodeSerializableElement(descriptor, 1, doubleSerializer)
                height = decodeSerializableElement(descriptor, 2, rangeDoubleSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> location = decodeSerializableElement(descriptor, index, locationSerializer)
                        1 -> radius = decodeSerializableElement(descriptor, index, doubleSerializer)
                        2 -> height = decodeSerializableElement(descriptor, index, rangeDoubleSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            CylinderArea(
                location ?: throw SerializationException("The field location is missing"),
                radius ?: throw SerializationException("The field radius is missing"),
                height ?: throw SerializationException("The field height is missing"),
            )
        }
    }

    override fun serialize(encoder: Encoder, value: CylinderArea) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, locationSerializer, value.location)
            encodeSerializableElement(descriptor, 1, doubleSerializer, value.radius)
            encodeSerializableElement(descriptor, 2, rangeDoubleSerializer, value.height)
        }
    }

}

/**
 * Represents a cylindrical area in a three-dimensional space.
 *
 * @property location The location of the center of the cylinder.
 * @property radius The radius of the cylinder. Must be greater than or equal to 0.0.
 * @property height The range of valid heights for the cylinder.
 * @constructor Creates a CylinderArea object with the specified location, radius, and height range.
 */
@Serializable(with = CylinderAreaSerializer::class)
public class CylinderArea(
    override var location: Location,
    radius: Double,
    public val height: ClosedRange<Double>,
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
                && sqrt((location.x - areaLocation.x).pow(2.0) + (location.z - areaLocation.z).pow(2.0)) <= radius // Within radius
                && location.y in height // Within height
    }
}
