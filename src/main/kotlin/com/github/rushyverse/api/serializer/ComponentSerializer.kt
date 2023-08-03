package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.extension.toComponent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

/**
 * Serializer for [Component].
 */
public object ComponentSerializer : KSerializer<Component> {

    private val miniMessage: MiniMessage = MiniMessage.builder()
        .strict(true)
        .tags(StandardTags.defaults())
        .build()

    private val stringSerializer get() = String.serializer()

    override val descriptor: SerialDescriptor get() = stringSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Component) {
        stringSerializer.serialize(encoder, miniMessage.serialize(value))
    }

    override fun deserialize(decoder: Decoder): Component {
        val value = stringSerializer.deserialize(decoder)
        return value.toComponent(miniMessage = miniMessage)
    }
}
