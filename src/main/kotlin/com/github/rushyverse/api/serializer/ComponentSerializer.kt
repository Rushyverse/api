package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.extension.asComponent
import com.github.rushyverse.api.extension.asString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

/**
 * Serializer for [Component].
 */
public object ComponentSerializer : KSerializer<Component> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "component",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: Component) {
        encoder.encodeString(value.asString())
    }

    override fun deserialize(decoder: Decoder): Component {
        return decoder.decodeString().asComponent()
    }
}
