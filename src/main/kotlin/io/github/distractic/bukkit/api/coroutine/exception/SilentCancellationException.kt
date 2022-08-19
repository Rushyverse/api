package io.github.distractic.bukkit.api.coroutine.exception

import kotlin.coroutines.cancellation.CancellationException

/**
 * Exception who doesn't have a stack trace.
 */
public class SilentCancellationException(message: String, cause: Throwable? = null) : CancellationException(message) {

    init {
        if (cause != null) {
            initCause(cause)
        }
    }

    override fun fillInStackTrace(): Throwable {
        stackTrace = emptyArray()
        return this
    }
}