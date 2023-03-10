package com.github.rushyverse.api.extension

import com.github.rushyverse.api.coroutine.MinestomAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.minestom.server.entity.Entity
import net.minestom.server.event.EventListener
import net.minestom.server.event.trait.EntityEvent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Async version of [sync].
 * @receiver Entity to lock.
 * @param block The callback to execute once the element has been safely acquired.
 * @return The result of the callback in a [Deferred] object.
 */
public inline fun <reified E : Entity, reified T> E.async(
    coroutineScope: CoroutineScope = Dispatchers.MinestomAsync.scope,
    crossinline block: suspend E.() -> T
): Deferred<T> = coroutineScope.async {
    val acquirable = acquirable.lock()
    try {
        block(acquirable.get())
    } finally {
        acquirable.unlock()
    }
}

/**
 * Locks the acquirable element, execute {@code consumer} synchronously and unlock the thread.
 * Free if the element is already present in the current thread, blocking otherwise.
 * @receiver Entity to lock.
 * @param block The callback to execute once the element has been safely acquired.
 * @return The result of the callback.
 */
public inline fun <reified E : Entity, reified T> E.sync(block: E.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val acquirable = acquirable.lock()
    try {
        return block(acquirable.get())
    } finally {
        acquirable.unlock()
    }
}

/**
 * Registers an event listener for this entity.
 * @param block Handler of event.
 * @return The registered event listener.
 */
public inline fun <reified T : EntityEvent> Entity.onEvent(
    crossinline block: Entity.(T) -> EventListener.Result
): EventListener<T> {
    val listener = object : EventListener<T> {

        override fun eventType(): Class<T> {
            return T::class.java
        }

        override fun run(event: T): EventListener.Result {
            return block(event)
        }
    }
    eventNode().addListener(listener)
    return listener
}