package fr.distractic.bukkit.api.schedule

import kotlinx.coroutines.CancellationException

/**
 * Schedule to start and stop a task using coroutine.
 * @property running `true` if the scheduler is running, `false` otherwise.
 */
public interface Scheduler {

    public val running: Boolean

    /**
     * Start the task.
     * Throw an exception if the task is already running.
     */
    public fun start()

    /**
     * Stop the task.
     * @param cause Reason of the cancellation.
     */
    public fun cancel(cause: CancellationException? = null)

    /**
     * Stop the task and wait the end of last execution.
     */
    public suspend fun cancelAndJoin()
}