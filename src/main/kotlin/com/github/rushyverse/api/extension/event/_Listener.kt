package com.github.rushyverse.api.extension.event

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration

/**
 * Unregister the listener.
 */
public fun Listener.unregister(): Unit = HandlerList.unregisterAll(this)

/**
 * Wait events corresponding to an event type.
 * If the function [block] doesn't valid an event, stop the listening after the duration of [timeout].
 * Register a new listener to retrieve event.
 *
 * The current coroutine is suspended. When the coroutine is cancellable or resume, unregister the listener.
 * @param plugin Java plugin to register the listener.
 * @param priority Priority to register this event at.
 * @param ignoreCancelled Whether to pass canceled events or not.
 * @param block Function to treat the received event,
 * returns `true` to valid the event and stop the listening, `false` otherwise.
 */
public suspend inline fun <reified T : Event> waitEvent(
    plugin: JavaPlugin,
    timeout: Duration,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline block: T.() -> Boolean
) {
    withTimeoutOrNull(timeout) {
        waitEvent(
            plugin = plugin,
            priority = priority,
            ignoreCancelled = ignoreCancelled,
            block = block
        )
    }
}

/**
 * Wait events corresponding to an event type.
 * Register a new listener to retrieve event.
 *
 * The current coroutine is suspended. When the coroutine is cancellable or resume, unregister the listener.
 * @param plugin Java plugin to register the listener.
 * @param priority Priority to register this event at.
 * @param ignoreCancelled Whether to pass canceled events or not.
 * @param block Function to treat the received event, returns `true` to valid the event and stop the listening, `false` otherwise.
 */
public suspend inline fun <reified T : Event> waitEvent(
    plugin: JavaPlugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline block: T.() -> Boolean
) {
    val listener = object : Listener {}

    suspendCancellableCoroutine { cont ->
        cont.invokeOnCancellation {
            listener.unregister()
        }

        plugin.server.pluginManager.registerEvent(
            T::class.java,
            listener,
            priority,
            { _, event ->
                if (event !is T) return@registerEvent
                try {
                    if (event.block()) {
                        listener.unregister()
                        cont.resume(Unit)
                    }
                } catch (ex: Throwable) {
                    listener.unregister()
                    cont.resumeWithException(ex)
                }
            },
            plugin,
            ignoreCancelled
        )
    }
}
