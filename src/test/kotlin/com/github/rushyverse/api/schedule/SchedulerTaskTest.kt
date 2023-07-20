package com.github.rushyverse.api.schedule

import io.github.distractic.bukkit.api.utils.getRandomString
import kotlinx.coroutines.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class SchedulerTaskTest {

    @Test
    fun `no task when created`() {
        assertTrue { SchedulerTask(scope(), 1.minutes).tasks.isEmpty() }
    }

    @Nested
    inner class Add {

        @Nested
        inner class Index {

            @Test
            fun `out of the list`() {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.addAtUnsafe(-1) {}
                }

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.addAtUnsafe(1) {}
                }

                runBlocking {
                    scheduler.add {}
                }

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.addAtUnsafe(2) {}
                }
            }

            @Test
            fun `during running`() = runBlocking {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )
                scheduler.start()

                val body1: suspend SchedulerTask.Task.() -> Unit = { }
                val id1 = getRandomString()
                val task1 = scheduler.addAt(0, id1, body1)
                assertEquals(id1, task1.id)
                assertEquals(scheduler, task1.parent)
                assertEquals(body1, task1.body)
                assertEquals(listOf(task1), scheduler.tasks)

                val body2: suspend SchedulerTask.Task.() -> Unit = { }
                val id2 = getRandomString()
                val task2 = scheduler.addAt(1, id2, body2)
                assertEquals(id2, task2.id)
                assertEquals(scheduler, task2.parent)
                assertEquals(body2, task2.body)
                assertEquals(listOf(task1, task2), scheduler.tasks)

                val body3: suspend SchedulerTask.Task.() -> Unit = { }
                val id3 = getRandomString()
                val task3 = scheduler.addAt(1, id3, body3)
                assertEquals(id3, task3.id)
                assertEquals(scheduler, task3.parent)
                assertEquals(body3, task3.body)
                assertEquals(listOf(task1, task3, task2), scheduler.tasks)
            }

            @Test
            fun `during idle`() = runBlocking {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )

                val body1: suspend SchedulerTask.Task.() -> Unit = { }
                val id1 = getRandomString()
                val task1 = scheduler.addAtUnsafe(0, id1, body1)
                assertEquals(id1, task1.id)
                assertEquals(scheduler, task1.parent)
                assertEquals(body1, task1.body)
                assertEquals(listOf(task1), scheduler.tasks)

                val body2: suspend SchedulerTask.Task.() -> Unit = { }
                val id2 = getRandomString()
                val task2 = scheduler.addAtUnsafe(1, id2, body2)
                assertEquals(id2, task2.id)
                assertEquals(scheduler, task2.parent)
                assertEquals(body2, task2.body)
                assertEquals(listOf(task1, task2), scheduler.tasks)

                val body3: suspend SchedulerTask.Task.() -> Unit = { }
                val id3 = getRandomString()
                val task3 = scheduler.addAtUnsafe(0, id3, body3)
                assertEquals(id3, task3.id)
                assertEquals(scheduler, task3.parent)
                assertEquals(body3, task3.body)
                assertEquals(listOf(task3, task1, task2), scheduler.tasks)
            }
        }

        @Test
        fun `during running`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )
            scheduler.start()

            val body1: suspend SchedulerTask.Task.() -> Unit = { }
            val id1 = getRandomString()
            val task1 = scheduler.add(id1, body1)
            assertEquals(id1, task1.id)
            assertEquals(scheduler, task1.parent)
            assertEquals(body1, task1.body)
            assertEquals(listOf(task1), scheduler.tasks)

            val body2: suspend SchedulerTask.Task.() -> Unit = { }
            val id2 = getRandomString()
            val task2 = scheduler.add(id2, body2)
            assertEquals(id2, task2.id)
            assertEquals(scheduler, task2.parent)
            assertEquals(body2, task2.body)
            assertEquals(listOf(task1, task2), scheduler.tasks)
        }

        @Test
        fun `during idle`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )

            val body1: suspend SchedulerTask.Task.() -> Unit = { }
            val id1 = getRandomString()
            val task1 = scheduler.addUnsafe(id1, body1)
            assertEquals(id1, task1.id)
            assertEquals(scheduler, task1.parent)
            assertEquals(body1, task1.body)
            assertEquals(listOf(task1), scheduler.tasks)

            val body2: suspend SchedulerTask.Task.() -> Unit = { }
            val id2 = getRandomString()
            val task2 = scheduler.addUnsafe(id2, body2)
            assertEquals(id2, task2.id)
            assertEquals(scheduler, task2.parent)
            assertEquals(body2, task2.body)
            assertEquals(listOf(task1, task2), scheduler.tasks)
        }
    }

    @Nested
    inner class Remove {

        @Nested
        inner class Index {

            @Test
            fun `out of the list`() {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.removeAtUnsafe(-1)
                }

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.removeAtUnsafe(0)
                }

                runBlocking {
                    scheduler.add {}
                }

                assertThrows<IndexOutOfBoundsException> {
                    scheduler.removeAtUnsafe(1)
                }
            }

            @Test
            fun `during running`() = runBlocking {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )

                val task1 = scheduler.add { }
                scheduler.add { }
                val task3 = scheduler.add { }
                scheduler.start()

                scheduler.removeAt(1)
                assertEquals(listOf(task1, task3), scheduler.tasks)
                scheduler.removeAt(1)
                assertEquals(listOf(task1), scheduler.tasks)
                scheduler.removeAt(0)
                assertEquals(listOf(), scheduler.tasks)
            }

            @Test
            fun `during idle`() = runBlocking {
                val scheduler = SchedulerTask(
                    scope(),
                    10.milliseconds
                )

                val task1 = scheduler.add { }
                val task2 = scheduler.add { }
                scheduler.add { }
                scheduler.start()

                scheduler.removeAt(2)
                assertEquals(listOf(task1, task2), scheduler.tasks)
                scheduler.removeAt(0)
                assertEquals(listOf(task2), scheduler.tasks)
                scheduler.removeAt(0)
                assertEquals(listOf(), scheduler.tasks)
            }
        }

        @Test
        fun `during running`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )
            scheduler.start()

            val task1 = scheduler.add {}
            val task2 = scheduler.add {}

            assertTrue { scheduler.remove(task1.id) }
            assertEquals(listOf(task2), scheduler.tasks)
            assertTrue { scheduler.remove(task2.id) }
            assertEquals(listOf(), scheduler.tasks)
        }

        @Test
        fun `during idle`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )

            val task1 = scheduler.add {}
            val task2 = scheduler.add {}

            assertTrue { scheduler.removeUnsafe(task1.id) }
            assertEquals(listOf(task2), scheduler.tasks)
            assertTrue { scheduler.removeUnsafe(task2.id) }
            assertEquals(listOf(), scheduler.tasks)
        }
    }

    @Nested
    @DisplayName("Delay before first execution")
    inner class DelayBefore {

        @Test
        fun `enable option`() = runBlocking {
            var isExecuted = false
            val scheduler = SchedulerTask(
                scope(),
                1.minutes,
                delayBefore = true
            )

            scheduler.addUnsafe {
                isExecuted = true
            }

            scheduler.start()
            delay(100)
            assertFalse { isExecuted }
        }

        @Test
        fun `disable option`() = runBlocking {
            var isExecuted = false
            val scheduler = SchedulerTask(
                scope(),
                1.minutes,
                delayBefore = false
            )

            scheduler.addUnsafe {
                isExecuted = true
            }

            scheduler.start()
            delay(100)
            assertTrue { isExecuted }
        }
    }

    @Nested
    @DisplayName("Safe in case of error")
    inner class Error {

        @Test
        fun `continue schedule if error into the body`() = runBlocking {
            var counter = 0
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )

            scheduler.addUnsafe {
                counter++
                error("Error in body")
            }

            scheduler.start()
            val countDownLatch = CountDownLatch(10)
            while(counter <= 1) {
                assertFalse { countDownLatch.await(10, TimeUnit.MILLISECONDS) }
                countDownLatch.countDown()
            }

            assertTrue { counter >= 2 }

        }
    }

    @Nested
    inner class Run {

        @Test
        fun `body is executed several times`() = runBlocking {
            var counter = 0
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )
            scheduler.addUnsafe { counter++ }

            scheduler.start()
            delay(100)
            assertTrue { counter in 2..10 }
        }

        @Test
        fun `is running enabled when the scheduler is executed`() {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )

            assertFalse { scheduler.running }
            scheduler.start()
            assertTrue { scheduler.running }
        }

        @Test
        fun `can be stop and re-start`() = runBlocking {
            var counter = 0
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )
            scheduler.addUnsafe { counter++ }

            scheduler.start()
            scheduler.cancel()
            assertFalse { scheduler.running }

            counter = 0
            scheduler.start()
            assertTrue { scheduler.running }
            delay(50)
            assertTrue { counter > 1 }
        }

        @Test
        fun `coroutine to run scheduler is the children of the coroutine scope`() {
            val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            val scheduler = SchedulerTask(
                scope,
                10.milliseconds
            )

            scheduler.start()
            scope.cancel("Cancel parent")
            assertFalse { scheduler.running }
        }

    }

    @Nested
    inner class Cancel {

        @Test
        fun `cancel will change the state of the scheduler`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds
            )

            scheduler.start()
            assertTrue { scheduler.running }
            scheduler.cancel()
            assertFalse { scheduler.running }
        }

        @Test
        fun `cancel when no task`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds,
                stopWhenNoTask = true
            )
            val task = scheduler.add {  }
            scheduler.start()
            assertTrue { scheduler.running }
            scheduler.remove(task.id)
            assertFalse { scheduler.running }
        }

        @Test
        fun `keep alive despite missing task`() = runBlocking {
            val scheduler = SchedulerTask(
                scope(),
                10.milliseconds,
                stopWhenNoTask = false
            )
            val task = scheduler.add {  }
            scheduler.start()
            assertTrue { scheduler.running }
            scheduler.remove(task.id)
            assertTrue { scheduler.running }
        }

    }

    private fun scope() = CoroutineScope(Dispatchers.IO + SupervisorJob())
}