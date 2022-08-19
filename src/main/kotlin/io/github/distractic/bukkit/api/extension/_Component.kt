package io.github.distractic.bukkit.api.extension

import net.kyori.adventure.text.*
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("NOTHING_TO_INLINE")
public inline fun Component.toText(): String {
    return LegacyComponentSerializer.legacySection().serialize(this)
}

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