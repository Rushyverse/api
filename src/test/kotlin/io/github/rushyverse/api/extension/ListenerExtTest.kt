package io.github.rushyverse.api.extension

import io.github.rushyverse.api.utils.assertCoroutineContextFromScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerSkinInitEvent
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ListenerExtTest {

    @Test
    fun `should handle event in coroutine context by adding listener suspend`() {
        val node = spyk(EventNode.all("test"))
        val slot = slot<EventListener<PlayerSkinInitEvent>>()
        every { node.addListener(capture(slot)) } returns mockk()

        val currentThread = Thread.currentThread()
        val latch = CountDownLatch(1)
        val event = mockk<PlayerSkinInitEvent>()

        var executed = false
        val scope = CoroutineScope(Dispatchers.Default)
        node.addListenerSuspend<PlayerSkinInitEvent>(scope) {
            assertCoroutineContextFromScope(scope, coroutineContext)
            assertEquals(event, it)
            executed = true

            assertEquals(currentThread, Thread.currentThread())
            yield()
            assertNotEquals(currentThread, Thread.currentThread())
            latch.countDown()
        }

        slot.captured.run(event)
        latch.await()
        assertTrue(executed)

    }
}