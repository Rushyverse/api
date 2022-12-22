package io.github.rushyverse.api.coroutine

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.minestom.server.ServerProcess
import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.SchedulerManager
import kotlin.test.Test

class MinestomAsyncCoroutineDispatcherTest {

    @Test
    fun `Should not perform if process is not alive`() {
        val process = mockk<ServerProcess>()
        every { process.isAlive } returns false
        val schedulerManager = mockk<SchedulerManager>()
        every { process.scheduler() } returns schedulerManager

        val dispatcher = MinestomAsyncCoroutineDispatcher(process)
        dispatcher.dispatch(mockk()) {}

        verify(exactly = 0) { schedulerManager.scheduleNextProcess(any(), any()) }
        verify(exactly = 0) { schedulerManager.scheduleNextTick(any()) }
    }

    @Test
    fun `Should perform task in async context`() {
        val process = mockk<ServerProcess>()
        every { process.isAlive } returns true
        val schedulerManager = mockk<SchedulerManager>()
        every { process.scheduler() } returns schedulerManager
        every { schedulerManager.scheduleNextProcess(any(), any()) } returns mockk()

        val dispatcher = MinestomAsyncCoroutineDispatcher(process)

        val runnable = mockk<Runnable>()
        dispatcher.dispatch(mockk(), runnable)

        verify(exactly = 1) { schedulerManager.scheduleNextProcess(runnable, ExecutionType.ASYNC) }
        verify(exactly = 0) { schedulerManager.scheduleNextTick(any()) }
    }

}