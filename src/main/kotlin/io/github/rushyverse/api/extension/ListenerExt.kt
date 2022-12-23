package io.github.rushyverse.api.extension

import io.github.rushyverse.api.coroutine.MinestomSync
import io.github.rushyverse.api.listener.EventListenerSuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * Allows to handle event in a coroutine context.
 * @receiver Event node to register the event listener to.
 * @param coroutineScope Coroutine scope where the event will be handled.
 * @param handle Handler of the event.
 */
public inline fun <reified E : Event> EventNode<Event>.addListenerSuspend(
    coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope,
    crossinline handle: suspend (E) -> Unit
) {
    addListener(object : EventListenerSuspend<E>(coroutineScope) {
        override suspend fun runSuspend(event: E) {
            handle(event)
        }

        override fun eventType(): Class<E> {
            return E::class.java
        }
    })
}