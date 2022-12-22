package io.github.rushyverse.api.listener

import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * Allows to handle event in a coroutine context.
 * @receiver Event node to register the event listener to.
 * @param handle Handler of the event.
 */
public inline fun <reified E : Event> EventNode<Event>.addListenerSuspend(crossinline handle: suspend (E) -> Unit) {
    addListener(object : EventListenerSuspend<E>() {
        override suspend fun runSuspend(event: E) {
            handle(event)
        }

        override fun eventType(): Class<E> {
            return E::class.java
        }
    })
}