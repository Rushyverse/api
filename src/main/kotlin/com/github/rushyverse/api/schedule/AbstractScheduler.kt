package com.github.rushyverse.api.schedule

import kotlinx.coroutines.*

/**
 * Allows scheduling a task into a coroutine from the [coroutineScope].
 * @property coroutineScope Scope to start the job.
 * @property job Instance of the job launched.
 * @property running `true` if the task is running, `false` otherwise.
 */
public abstract class AbstractScheduler(
    protected val coroutineScope: CoroutineScope
) : Scheduler {

    protected var job: Job? = null

    override val running: Boolean
        get() = job?.isActive == true

    override fun start() {
        require(!running) { "The scheduling is already running" }
        job = coroutineScope.launch {
            run()
        }
    }

    /**
     * Main body of the scheduler.
     * Will create the loop to execute tasks.
     */
    public abstract suspend fun run()

    override fun cancel(cause: CancellationException?) {
        job?.cancel(cause)
        job = null
    }

    override suspend fun cancelAndJoin() {
        job?.cancelAndJoin()
        job = null
    }
}
