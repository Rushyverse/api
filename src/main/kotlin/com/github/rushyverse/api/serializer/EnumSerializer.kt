package com.github.rushyverse.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.yaml.snakeyaml.serializer.SerializerException
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
        val name = decoder.decodeString().uppercase().replace(" ", "_")
        return findEnumValue(name)
    }

    /**
     * Finds the matching enum value for a given decoded string.
     * Will transform all spaces to underscore and uppercase all letters.
     * So for example, "foo bar" will be transformed to "FOO_BAR".
     * If no matching enum value is found, an [SerializerException] will be thrown.
     *
     * @param decoded The decoded string used to search for the matching enum value.
     * @return The matching enum value.
     * @throws IllegalArgumentException if no matching enum value is found.
     */
    public fun findEnumValue(decoded: String): T {
        val name = decoded.uppercase().replace(" ", "_")
        return values.firstOrNull { it.name == name } ?: run {
            throw SerializationException("Invalid value: $name. Valid values are: ${values.joinToString { it.name }}")
        }
    }
}
