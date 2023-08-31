package com.github.rushyverse.api.serializer

import com.destroystokyo.paper.Namespaced
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey

/**
 * Serializer for [Namespaced].
 */
public object NamespacedSerializer : KSerializer<Namespaced> {

    /**
     * The default separator used to separate the namespace and the key.
     */
    private const val DEFAULT_SEPARATOR = ":"

    /**
     * Regex used to replace uppercase letters with "_[a-z]".
     */
    private val regexUppercase = Regex("([A-Z])")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "namespaced",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Namespaced) {
        encoder.encodeString(value.namespace + DEFAULT_SEPARATOR + value.key)
    }

    override fun deserialize(decoder: Decoder): Namespaced {
        return decoder.decodeString().let { decodedString ->
            val namespacedString = decodedString.split(DEFAULT_SEPARATOR).map {
                // Replace " " to "_". Example: blue wool -> blue_wool
                it.replace(' ', '_')
                    // Replace "A-Z" to "_a-z". Example: blueWool -> blue_wool
                    .replace(regexUppercase) { matchResult -> "_${matchResult.value.lowercase()}" }

            }

            if (namespacedString.size == 2) {
                NamespacedKey(namespacedString[0], namespacedString[1])
            } else {
                NamespacedKey.minecraft(namespacedString[0])
            }
        }
    }
}
