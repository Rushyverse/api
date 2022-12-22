package io.github.rushyverse.api.listener

import io.github.rushyverse.api.coroutine.MinestomSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener

/**
 * Allows to handle event in a coroutine context.
 * @param T Type of the event to handle.
 * @property coroutineScope Coroutine scope where the event will be handled.
 */
public abstract class EventListenerSuspend<T : Event>(
    private val coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope
) : EventListener<T> {

    override fun run(event: T): EventListener.Result {
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            runSuspend(event)
        }
        return EventListener.Result.SUCCESS
    }

    /**
     * Handler of the event in a coroutine scope.
     * Before the first suspension point, the code will be executed in the main thread.
     * When the first suspension point is reached, the code will be executed in a thread obtained using [coroutineScope].
     * @param event Event that was fired.
     */
    public abstract suspend fun runSuspend(event: T)
}