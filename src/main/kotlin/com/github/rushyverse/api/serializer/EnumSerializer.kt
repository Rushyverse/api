package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

/**
 * EnumSerializer is a serializer used to serialize and deserialize enum values.
 *
 * @param T The type of the enum class.
 * @property values The list of enum values to be serialized or deserialized.
 * @property descriptor The serial descriptor for the enum serializer.
 * @constructor Creates an instance of EnumSerializer with the given serial name and enum values.
 */
public open class EnumSerializer<T : Enum<T>>(
    serialName: String,
    private val values: EnumEntries<T>
) : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): T {
        return findEnumValue(decoder.decodeString())
    }

    /**
     * Finds the matching enum value for a given decoded string.
     * Will transform all spaces to underscore and uppercase all letters.
     * So for example, "foo bar" will be transformed to "FOO_BAR".
     *
     * @param decoded The decoded string used to search for the matching enum value.
     * @return The matching enum value.
     * @throws SerializationException if no matching enum value is found.
     */
    public fun findEnumValue(decoded: String): T {
        val name = decoded.uppercase().replace(" ", "_")
        return values.firstOrNull { it.name.uppercase() == name }
            ?: throw SerializationException("Invalid enum value: $decoded. Valid values are: ${
                values.joinToString { it.name }
            }")
    }
}
