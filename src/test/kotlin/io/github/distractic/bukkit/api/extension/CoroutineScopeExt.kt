package io.github.distractic.bukkit.api.extension

import io.github.distractic.bukkit.api.schedule.SchedulerTask
import kotlinx.coroutines.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class CoroutineScopeExt {

    @Test
    fun `with scope context will use the coroutine context`() = runBlocking {
        val scope = CoroutineScope(Dispatchers.Default)
        val job = withScopeContext(scope) {
            val currentJob = this.coroutineContext.job
            assertTrue { currentJob in scope.coroutineContext.job.children }
            currentJob
        }
        assertTrue { job !in scope.coroutineContext.job.children }
    }

    @Test
    fun `create running scheduler with task`() {
        val body: suspend SchedulerTask.Task.() -> Unit = {}
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val delay = 42.seconds
        val scheduler = scope.scheduledTask(delay, body)
        assertTrue { scheduler.running }

        assertEquals(delay, scheduler.delay)
        assertEquals(1, scheduler.tasks.size)

        val task = scheduler.tasks.first()
        assertEquals(body, task.body)
        assertEquals(scheduler, task.parent)

        scope.coroutineContext.cancelChildren()
        assertFalse { scheduler.running }
    }

    @Test
    fun `create scheduler`() {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val delay = 10.seconds
        val scheduler = scope.scheduler(delay)
        assertFalse { scheduler.running }
        assertEquals(delay, scheduler.delay)
        assertEquals(0, scheduler.tasks.size)

        scheduler.start()

        scope.coroutineContext.cancelChildren()
        assertFalse { scheduler.running }
    }
}