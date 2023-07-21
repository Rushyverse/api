package com.github.rushyverse.api.extension.event

import org.bukkit.event.Cancellable

/**
 * Extension function allowing to cancel the current process by method calling.
 */
public fun Cancellable.cancel() {
    isCancelled = true
}

/**
 * Extension function allowing to cancel the current process by method calling with a condition.
 */
public fun <T : Cancellable> T.cancelIf(condition: T.() -> Boolean) {
    if (condition()) {
        cancel()
    }
}