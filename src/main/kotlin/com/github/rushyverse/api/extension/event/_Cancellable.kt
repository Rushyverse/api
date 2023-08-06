package com.github.rushyverse.api.extension.event

import org.bukkit.event.Cancellable

/**
 * Extension function allowing to cancel the current process by method calling.
 */
public fun Cancellable.cancel() {
    isCancelled = true
}
