package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.extension.asComponent
import com.github.rushyverse.api.extension.asString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component

/**
 * Serializer for [Component].
 */
public object ComponentSerializer : KSerializer<Component> {

    private val stringSerializer: KSerializer<String> get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Component) {
        stringSerializer.serialize(encoder, value.asString())
    }

    override fun deserialize(decoder: Decoder): Component {
        return stringSerializer.deserialize(decoder).asComponent()
    }
}
