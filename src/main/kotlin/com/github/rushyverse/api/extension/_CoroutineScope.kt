package com.github.rushyverse.api.extension

import com.github.rushyverse.api.schedule.SchedulerTask
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

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
 * Create a new scheduler without a task.
 * The [CoroutineScope] for the scheduler is a children of the [CoroutineScope] receiver.
 * @receiver CoroutineScope to launch the task.
 * @param delay Time to wait between each execution.
 * @return The instance of the scheduler.
 */
public fun CoroutineScope.scheduler(delay: Duration): SchedulerTask =
    SchedulerTask(this, delay)
