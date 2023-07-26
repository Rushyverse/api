package com.github.rushyverse.api.extension

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

/**
 * Create new bukkit runnable with the body as the function sent in parameter.
 * @param task Function that will be processed when the bukkit runnable will be run.
 * @return New instance of bukkit runnable.
 */
public inline fun BukkitRunnable(crossinline task: BukkitRunnable.() -> Unit): BukkitRunnable {
    return object : BukkitRunnable() {
        override fun run() {
            task()
        }
    }
}

/**
 * Execute the function into the primary server thread in bukkit runnable.
 * The current coroutine will be suspended.
 * @param block Code that will be executed in the primary thread of the server.
 * @return T Instance returned after the execution of the task.
 */
public suspend inline fun <T> onPrimaryThread(
    plugin: Plugin,
    noinline block: suspend CoroutineScope.() -> T
): T = withContext(plugin.minecraftDispatcher, block)

/**
 * Execute the function asynchronously into a bukkit thread (other than primary thread).
 * The current coroutine will be suspended.
 * @param block Code that will be executed in a second thread of bukkit.
 * @return T Instance returned after the execution of the task.
 */
public suspend inline fun <T> onAsyncThread(
    plugin: Plugin,
    noinline block: suspend CoroutineScope.() -> T
): T = withContext(plugin.asyncDispatcher, block)
