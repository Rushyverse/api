package io.github.distractic.bukkit.api.extension

import io.github.distractic.bukkit.api.schedule.SchedulerTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

/**
 * Calls the specified suspending block with a given coroutine context into the [CoroutineScope], suspends until it completes, and returns
 * the result.
 *
 * The resulting context for the [block] is derived by merging the current [coroutineContext] with the
 * specified context using `coroutineContext + context` (see [CoroutineContext.plus]).
 * This suspending function is cancellable. It immediately checks for cancellation of
 * the resulting context and throws [CancellationException] if it is not [active][CoroutineContext.isActive].
 *
 * This function uses dispatcher from the new context, shifting execution of the [block] into the
 * different thread if a new dispatcher is specified, and back to the original dispatcher
 * when it completes. Note that the result of `withContext` invocation is
 * dispatched into the original context in a cancellable way with a **prompt cancellation guarantee**,
 * which means that if the original [coroutineContext], in which `withContext` was invoked,
 * is cancelled by the time its dispatcher starts to execute the code,
 * it discards the result of `withContext` and throws [CancellationException].
 *
 * The cancellation behaviour described above is enabled if and only if the dispatcher is being changed.
 * For example, when using `withContext(NonCancellable) { ... }` there is no change in dispatcher and
 * this call will not be cancelled neither on entry to the block inside `withContext` nor on exit from it.
 */
public suspend inline fun <T> withScopeContext(
    scope: CoroutineScope,
    noinline block: suspend CoroutineScope.() -> T
): T {
    return withContext(scope.coroutineContext, block)
}

/**
 * Create a new scheduler with a task and run it.
 * The [CoroutineScope] for the scheduler is a children of the [CoroutineScope] receiver.
 * @receiver CoroutineScope to launch the task.
 * @param delay Time to wait between each execution.
 * @param body Task executed in the coroutine context each time.
 * @return The instance of the scheduler where the task is run.
 */
public fun CoroutineScope.scheduledTask(
    delay: Duration,
    body: suspend SchedulerTask.Task.() -> Unit
): SchedulerTask = scheduler(delay).apply {
    addUnsafe(body = body)
    start()
}

/**
 * Create a new scheduler without task.
 * The [CoroutineScope] for the scheduler is a children of the [CoroutineScope] receiver.
 * @receiver CoroutineScope to launch the task.
 * @param delay Time to wait between each execution.
 * @return The instance of the scheduler.
 */
public fun CoroutineScope.scheduler(delay: Duration): SchedulerTask =
    SchedulerTask(this, delay)