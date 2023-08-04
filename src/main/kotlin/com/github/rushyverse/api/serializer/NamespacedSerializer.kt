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

    private val regexUppercase = Regex("([A-Z])")

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "namespaced",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Namespaced) {
        encoder.encodeString(value.key)
    }

    override fun deserialize(decoder: Decoder): Namespaced {
        return decoder.decodeString().let { decodedString ->
            val namespacedString = decodedString.split(NamespacedKey.DEFAULT_SEPARATOR).map {
                // Replace "A-Z" to "_a-z"
                it.replace(regexUppercase) { matchResult -> "_${matchResult.value.lowercase()}" }
            }

            if (namespacedString.size == 2) {
                NamespacedKey(namespacedString[0], namespacedString[1])
            } else {
                NamespacedKey.minecraft(decodedString)
            }
        }
    }
}
