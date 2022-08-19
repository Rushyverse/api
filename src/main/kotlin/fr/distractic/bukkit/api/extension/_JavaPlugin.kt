package fr.distractic.bukkit.api.extension

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import fr.distractic.bukkit.api.item.CraftBuilder
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Register a new recipe craft in the server.
 * @receiver Java plugin associated to the recipe.
 * @param builder Builder to create recipe.
 */
public inline fun JavaPlugin.registerCraft(
    key: String = UUID.randomUUID().toString().lowercase(),
    builder: CraftBuilder.() -> Unit
): Boolean {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val craftBuilder = CraftBuilder().apply(builder)
    val namespace = NamespacedKey(this, key)
    val recipe = craftBuilder.build(namespace)
    return server.addRecipe(recipe)
}

/**
 * Register a new suspendable listener for the plugin.
 * Support listener with non-suspend and suspend methods.
 * @receiver Instance of the plugin that will receive the listener.
 * @param listenerBuilder Builder to create a listener.
 */
public inline fun JavaPlugin.registerListener(listenerBuilder: () -> Listener) {
    contract { callsInPlace(listenerBuilder, InvocationKind.EXACTLY_ONCE) }
    server.pluginManager.registerSuspendingEvents(listenerBuilder(), this)
}