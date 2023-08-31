package com.github.rushyverse.api.schedule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.CancellationException
import kotlin.time.Duration

private val log = KotlinLogging.logger { }

/**
 * Allows executing code while the task is not canceled.
 * The [tasks] contains several body functions that one will be executed after each [delay].
 * @property delay Time to wait between each execution.
 * @property _tasks Task executed in the coroutine context each time.
 * @property tasks Immutable list of a task executed in the coroutine context each time.
 * @property mutex Mutex to synchronized data of the scheduler.
 * @property nextTaskIndex Next index of the task that will be executed.
 */
public class SchedulerTask(
    coroutineScope: CoroutineScope,
    public var delay: Duration,
    private var delayBefore: Boolean = false,
    private var stopWhenNoTask: Boolean = true
) : AbstractScheduler(coroutineScope) {

    /**
     * Task of the scheduler.
     * @property id Id of the task.
     * @property parent Scheduler task where is registered the task.
     * @property body Lambda function.
     */
    public inner class Task(
        public val id: String = UUID.randomUUID().toString(),
        public val parent: SchedulerTask,
        public val body: suspend Task.() -> Unit
    ) {

        /**
         * Run the [body] function.
         */
        public suspend fun run() {
            body()
        }

        /**
         * Remove the task from the parent.
         */
        public suspend fun remove(): Boolean {
            return parent.remove(id)
        }
    }

    private var nextTaskIndex = 0

    private val _tasks = ArrayList<Task>()
    public val tasks: List<Task> = _tasks

    private val mutex = Mutex()

    override suspend fun run() {
        if (delayBefore) {
            delay(delay)
        }

        coroutineScope {
            while (isActive) {
                val task = getNextTaskAndShiftCursor()
                if (task != null) {
                    runTask(task)
                }
                delay(delay)
            }
        }
    }

    /**
     * Lock the mutex to search the next task that will be executed.
     * After have found the task, move the cursor [nextTaskIndex].
     * @return `null` if no task found into the list, the task otherwise.
     */
    private suspend fun getNextTaskAndShiftCursor(): Task? = mutex.withLock {
        var task = _tasks.getOrNull(nextTaskIndex)

        if (task == null) {
            task = _tasks.firstOrNull()
            nextTaskIndex = 1
        } else {
            nextTaskIndex++
        }
        task
    }

    /**
     * Run the task in try catch to protect the scheduler.
     * @param task Task that will be run.
     */
    private suspend fun runTask(task: Task) {
        try {
            task.run()
        } catch (t: Throwable) {
            log.error("Error during process of the task $task", t)
        }
    }

    /**
     * Add a new body should be executed in the scheduler.
     * @param id Id of the task created.
     * @param body Lambda function.
     * @return Task created.
     */
    public suspend fun add(id: String = UUID.randomUUID().toString(), body: suspend Task.() -> Unit): Task =
        mutex.withLock {
            addUnsafe(id, body)
        }

    /**
     * Add a new body should be executed in the scheduler.
     * The operation is made with possible asynchronous effect if the scheduler is running.
     * It's recommended to use this function when you are sure that the scheduler is not running.
     * @param id Id of the task created.
     * @param body Lambda function.
     * @return Task created.
     */
    public fun addUnsafe(id: String = UUID.randomUUID().toString(), body: suspend Task.() -> Unit): Task {
        return createTask(id, body).also {
            _tasks += it
        }
    }

    /**
     * Add a new body should be executed in the scheduler.
     * @param index Index at which the specified element is to be inserted.
     * @param id Id of the task created.
     * @param body Lambda function.
     * @return Task created.
     */
    public suspend fun addAt(
        index: Int,
        id: String = UUID.randomUUID().toString(),
        body: suspend Task.() -> Unit
    ): Task =
        mutex.withLock { addAtUnsafe(index, id, body) }

    /**
     * Add a new body should be executed in the scheduler.
     * The operation is made with possible asynchronous effect if the scheduler is running.
     * It's recommended to use this function when you are sure that the scheduler is not running.
     * @param index Index at which the specified element is to be inserted.
     * @param id Id of the task created.
     * @param body Lambda function.
     * @return Task created.
     */
    public fun addAtUnsafe(index: Int, id: String = UUID.randomUUID().toString(), body: suspend Task.() -> Unit): Task =
        createTask(id, body).also {
            _tasks.add(index, it)
        }

    /**
     * Create a children task.
     * @param id Id of the task created.
     * @param body Lambda function linked to the task.
     * @return Task created.
     */
    private fun createTask(
        id: String,
        body: suspend Task.() -> Unit
    ) = Task(id, this, body)

    /**
     * Remove a task from the scheduler.
     * @param id Id of the task registered into the scheduler.
     * @return `true` if a task has been removed, `false` otherwise.
     */
    public suspend fun remove(id: String): Boolean = mutex.withLock {
        removeUnsafe(id)
    }

    /**
     * Remove a task from the scheduler.
     * The operation is made with possible asynchronous effect if the scheduler is running.
     * It's recommended to use this function when you are sure that the scheduler is not running.
     * @param id Id of the task registered into the scheduler.
     * @return `true` if a task has been removed, `false` otherwise.
     */
    public fun removeUnsafe(id: String): Boolean {
        val indexOfTask = _tasks.indexOfFirst { it.id == id }
        if (indexOfTask == -1) return false
        removeAtUnsafe(indexOfTask)
        return true
    }

    /**
     * Remove a task from the scheduler.
     * Throws exception if no task is present at the index.
     * @param index Index of the task that will be removed.
     */
    public suspend fun removeAt(index: Int) {
        mutex.withLock {
            removeAtUnsafe(index)
        }
    }

    /**
     * Remove the task at the index.
     * Throws exception if no task is present at the index.
     * The operation is made with possible asynchronous effect if the scheduler is running.
     * It's recommended to use this function when you are sure that the scheduler is not running.
     * If necessary, shift the next task to not skip one task.
     * @param index Index of the task.
     */
    public fun removeAtUnsafe(index: Int) {
        _tasks.removeAt(index)
        if (tasks.isEmpty()) {
            if (stopWhenNoTask) {
                cancel(CancellationException("No tasks left. The scheduler is disabled"))
            }
            nextTaskIndex = 0
        } else if (index < nextTaskIndex) {
            nextTaskIndex--
        }
    }
}
