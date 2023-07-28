package com.github.rushyverse.api.world

import com.github.rushyverse.api.extension.centerRelative
import com.github.rushyverse.api.extension.minMaxOf
import com.github.rushyverse.api.serializer.LocationSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import org.bukkit.Location

/**
 * Serializer class for [CubeArea] objects.
 */
public object CubeAreaSerializer: KSerializer<CubeArea> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("cubeArea") {
        val locationDescriptor = LocationSerializer.descriptor
        element("loc1", locationDescriptor)
        element("loc2", locationDescriptor)
    }

    override fun deserialize(decoder: Decoder): CubeArea {
        val locationSerializer = LocationSerializer

        return decoder.decodeStructure(descriptor) {
            var loc1: Location? = null
            var loc2: Location? = null

            if (decodeSequentially()) {
                loc1 = decodeSerializableElement(descriptor, 0, locationSerializer)
                loc2 = decodeSerializableElement(descriptor, 1, locationSerializer)
            } else {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> loc1 = decodeSerializableElement(descriptor, index, locationSerializer)
                        1 -> loc2 = decodeSerializableElement(descriptor, index, locationSerializer)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
            }

            CubeArea(
                loc1 ?: throw SerializationException("The field loc1 is missing"),
                loc2 ?: throw SerializationException("The field loc2 is missing"),
            )
        }
    }

    override fun serialize(encoder: Encoder, value: CubeArea) {
        val locationSerializer = LocationSerializer

        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, locationSerializer, value.min)
            encodeSerializableElement(descriptor, 1, locationSerializer, value.max)
        }
    }

}

/**
 * A cuboid area defined by two positions.
 * @property min Minimum position.
 * @property max Maximum position.
 * @property center Center position of the cube.
 */
@Serializable(with = CubeAreaSerializer::class)
public class CubeArea(loc1: Location, loc2: Location) {

    public var center: Location
        get() = max.centerRelative(min)
        set(value) {
            // The new position becomes the center of the cube.
            val halfSize = max.centerRelative(min)
            min = value.subtract(halfSize)
            max = value.add(halfSize)
        }

    public var min: Location
        private set

    public var max: Location
        private set


    init {
        val world1 = loc1.world
        val world2 = loc2.world
        require(world1 === world2) { "Locations must be in the same world" }

        val (x1, x2) = minMaxOf(loc1.x, loc2.x)
        val (y1, y2) = minMaxOf(loc1.y, loc2.y)
        val (z1, z2) = minMaxOf(loc1.z, loc2.z)
        this.min = Location(world1, x1, y1, z1)
        this.max = Location(world2, x2, y2, z2)
    }

    /**
     * Determines if a given location is within the specified area.
     *
     * @param location The location to check.
     * @return True if the location is within the area, false otherwise.
     */
    public fun isInArea(location: Location): Boolean {
        return min.world === location.world &&
                location.x in min.x..max.x &&
                location.y in min.y..max.y &&
                location.z in min.z..max.z
    }

    override fun toString(): String {
        return "CubeArea(min=$min, max=$max)"
    }

}
