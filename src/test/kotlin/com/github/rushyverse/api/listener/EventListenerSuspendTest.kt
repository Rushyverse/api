package com.github.rushyverse.api.listener

import com.github.rushyverse.api.utils.assertCoroutineContextFromScope
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerMoveEvent
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class EventListenerSuspendTest {

    @Test
    fun `should use dispatcher to process event listener suspend`() {
        val expectedEvent = mockk<PlayerMoveEvent>()
        val currentThread = Thread.currentThread()
        val latch = CountDownLatch(1)
        var executed = false

        val scope = CoroutineScope(Dispatchers.Default)
        val handler = object : EventListenerSuspend<PlayerMoveEvent>(scope) {
            override suspend fun runSuspend(event: PlayerMoveEvent) {
                assertCoroutineContextFromScope(scope, coroutineContext)
                assertEquals(currentThread, Thread.currentThread())
                assertEquals(expectedEvent, event)

                yield()

                assertNotEquals(currentThread, Thread.currentThread())
                latch.countDown()
                executed = true
            }

            override fun eventType(): Class<PlayerMoveEvent> {
                return PlayerMoveEvent::class.java
            }
        }

        assertEquals(EventListener.Result.SUCCESS, handler.run(expectedEvent))
        latch.await()
        assertTrue(executed)
    }

    @Test
    fun `should return success despite an exception`() {
        val expectedEvent = mockk<PlayerLoginEvent>()
        val scope = CoroutineScope(Dispatchers.Default)
        val handler = object : EventListenerSuspend<PlayerLoginEvent>(scope) {
            override suspend fun runSuspend(event: PlayerLoginEvent) {
                throw RuntimeException()
            }

            override fun eventType(): Class<PlayerLoginEvent> {
                return PlayerLoginEvent::class.java
            }
        }

        assertEquals(EventListener.Result.SUCCESS, handler.run(expectedEvent))
    }
}