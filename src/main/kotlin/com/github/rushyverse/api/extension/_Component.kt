package com.github.rushyverse.api.extension

import net.kyori.adventure.text.*
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * MiniMessage instance to serialize components with strict mode.
 */
private val MINI_MESSAGE_STRICT: MiniMessage = MiniMessage.builder()
    .strict(true)
    .tags(StandardTags.defaults())
    .build()

/**
 * Creates a text component using a builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun text(builder: TextComponent.Builder.() -> Unit): TextComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.text().apply(builder).build()
}

/**
 * Append a component build into the current component builder.
 * @receiver Component builder.
 * @param builder Function to build the child component with a component builder.
 * @return The current component builder.
 */
public inline fun TextComponent.Builder.appendText(builder: TextComponent.Builder.() -> Unit): TextComponent.Builder {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return append(Component.text().apply(builder).build())
}

/**
 * Creates a keybind component using a builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun keybind(builder: KeybindComponent.Builder.() -> Unit): KeybindComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.keybind().apply(builder).build()
}

/**
 * Creates a score component using a builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun score(builder: ScoreComponent.Builder.() -> Unit): ScoreComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.score().apply(builder).build()
}

/**
 * Creates a block NBT component using builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun blockNBT(builder: BlockNBTComponent.Builder.() -> Unit): BlockNBTComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.blockNBT().apply(builder).build()
}

/**
 * Creates an entity NBT component using builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun entityNBT(builder: EntityNBTComponent.Builder.() -> Unit): EntityNBTComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.entityNBT().apply(builder).build()
}

/**
 * Creates a translatable component using builder.
 * @param builder Function to build the component with the component builder.
 * @return The component built.
 */
public inline fun translatable(builder: TranslatableComponent.Builder.() -> Unit): TranslatableComponent {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return Component.translatable().apply(builder).build()
}

/**
 * Transform a component into a legacy text.
 * @receiver Component to transform.
 * @return The legacy text.
 */
public fun Component.toText(): String = LegacyComponentSerializer.legacySection().serialize(this)

/**
 * Add the [bold][TextDecoration.BOLD] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)

/**
 * Remove the [bold][TextDecoration.BOLD] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [bold][TextDecoration.BOLD] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineBold(): Component = this.decoration(TextDecoration.BOLD, TextDecoration.State.NOT_SET)

/**
 * Add the [italic][TextDecoration.ITALIC] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.TRUE)

/**
 * Remove the [italic][TextDecoration.ITALIC] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [italic][TextDecoration.ITALIC] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineItalic(): Component = this.decoration(TextDecoration.ITALIC, TextDecoration.State.NOT_SET)

/**
 * Add the [underlined][TextDecoration.UNDERLINED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withUnderlined(): Component = this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.TRUE)

/**
 * Remove the [underlined][TextDecoration.UNDERLINED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutUnderlined(): Component =
    this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [underlined][TextDecoration.UNDERLINED] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineUnderlined(): Component =
    this.decoration(TextDecoration.UNDERLINED, TextDecoration.State.NOT_SET)

/**
 * Add the [strikethrough][TextDecoration.STRIKETHROUGH] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.TRUE)

/**
 * Remove the [strikethrough][TextDecoration.STRIKETHROUGH] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [strikethrough][TextDecoration.STRIKETHROUGH] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineStrikethrough(): Component =
    this.decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.NOT_SET)

/**
 * Add the [obfuscated][TextDecoration.OBFUSCATED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withObfuscated(): Component = this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.TRUE)

/**
 * Remove the [obfuscated][TextDecoration.OBFUSCATED] decoration to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutObfuscated(): Component =
    this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE)

/**
 * Set as [not set][TextDecoration.State.NOT_SET] the [obfuscated][TextDecoration.OBFUSCATED] decoration of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineObfuscated(): Component =
    this.decoration(TextDecoration.OBFUSCATED, TextDecoration.State.NOT_SET)

/**
 * Add all decorations to the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withDecorations(): Component =
    withBold().withItalic().withUnderlined().withStrikethrough().withObfuscated()

/**
 * Remove all decorations from the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.withoutDecorations(): Component =
    withoutBold().withoutItalic().withoutUnderlined().withoutStrikethrough().withoutObfuscated()

/**
 * Set as [not set][TextDecoration.State.NOT_SET] all decorations of the component.
 * @receiver Component to transform.
 * @return The same component.
 */
public fun Component.undefineDecorations(): Component =
    undefineBold().undefineItalic().undefineUnderlined().undefineStrikethrough().undefineObfuscated()

/**
 * Converts the component to its string representation using MiniMessage.
 *
 * @receiver The component to convert.
 * @param mini The MiniMessage instance to use for serialization.
 * @return The string representation of the component.
 */
public fun Component.asString(mini: MiniMessage = MINI_MESSAGE_STRICT): String = mini.serialize(this)
