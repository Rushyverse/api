package io.github.rushyverse.api.extension

import io.github.rushyverse.api.utils.assertCoroutineContextFromScope
import io.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import net.minestom.server.entity.Player
import net.minestom.server.thread.Acquirable
import net.minestom.server.thread.Acquired
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EntityExtTest {

    @Nested
    inner class Sync {

        @Test
        fun `should lock entity`() = runTest {
            val player = mockk<Player>()
            val acquired = mockk<Acquired<Player>>() {
                every { get() } returns player
                justRun { unlock() }
            }
            val acquirable = mockk<Acquirable<Player>>() {
                every { lock() } returns acquired
            }

            every { player.getAcquirable<Player>() } returns acquirable

            var executed = false
            val expectedValue = randomString()
            val returnedValue = player.sync {
                verify(exactly = 1) { player.getAcquirable<Player>() }
                verify(exactly = 1) { acquired.get() }
                verify(exactly = 1) { acquirable.lock() }
                verify(exactly = 0) { acquired.unlock() }
                executed = true
                expectedValue
            }

            assertTrue(executed)
            assertEquals(expectedValue, returnedValue)

            verify(exactly = 1) { player.getAcquirable<Player>() }
            verify(exactly = 1) { acquired.get() }
            verify(exactly = 1) { acquirable.lock() }
            verify(exactly = 1) { acquired.unlock() }
        }

        @Test
        fun `should unlock entity despite exception`() = runTest {
            val player = mockk<Player>()
            val acquired = mockk<Acquired<Player>>() {
                every { get() } returns player
                justRun { unlock() }
            }
            val acquirable = mockk<Acquirable<Player>>() {
                every { lock() } returns acquired
            }

            every { player.getAcquirable<Player>() } returns acquirable

            val ex = assertThrows<Exception> {
                player.sync {
                    throw Exception("Test")
                    Unit
                }
            }

            assertTrue(ex.message == "Test")

            verify(exactly = 1) { player.getAcquirable<Player>() }
            verify(exactly = 1) { acquired.get() }
            verify(exactly = 1) { acquirable.lock() }
            verify(exactly = 1) { acquired.unlock() }
        }
    }

    @Nested
    inner class Async {

        @Test
        fun `should lock entity and execute in coroutine`() = runTest {
            val player = mockk<Player>()
            val acquired = mockk<Acquired<Player>>() {
                every { get() } returns player
                justRun { unlock() }
            }
            val acquirable = mockk<Acquirable<Player>>() {
                every { lock() } returns acquired
            }

            every { player.getAcquirable<Player>() } returns acquirable
            val scope = CoroutineScope(Dispatchers.Default)

            val latch = CountDownLatch(1)
            var executed = false
            val expectedValue = randomString()
            val deferred = player.async(scope) {
                assertCoroutineContextFromScope(scope, coroutineContext)
                verify(exactly = 1) { player.getAcquirable<Player>() }
                verify(exactly = 1) { acquired.get() }
                verify(exactly = 1) { acquirable.lock() }
                verify(exactly = 0) { acquired.unlock() }

                latch.await()
                executed = true
                expectedValue
            }

            verify(exactly = 1) { player.getAcquirable<Player>() }
            verify(exactly = 1) { acquired.get() }
            verify(exactly = 1) { acquirable.lock() }
            verify(exactly = 0) { acquired.unlock() }

            assertFalse(executed)
            latch.countDown()
            assertEquals(expectedValue, deferred.await())
            assertTrue(executed)

            verify(exactly = 1) { player.getAcquirable<Player>() }
            verify(exactly = 1) { acquired.get() }
            verify(exactly = 1) { acquirable.lock() }
            verify(exactly = 1) { acquired.unlock() }
        }

        @Test
        fun `should unlock entity despite exception`() = runTest {
            val player = mockk<Player>()
            val acquired = mockk<Acquired<Player>>() {
                every { get() } returns player
                justRun { unlock() }
            }
            val acquirable = mockk<Acquirable<Player>>() {
                every { lock() } returns acquired
            }

            every { player.getAcquirable<Player>() } returns acquirable
            val scope = CoroutineScope(Dispatchers.Default)

            val deferred = player.async(scope) {
                throw Exception("Test")
                Unit
            }

            val ex = try {
                deferred.await()
            } catch (ex: Exception) {
                ex
            }

            assertTrue(ex is Exception)
            assertTrue(ex.message == "Test")

            verify(exactly = 1) { player.getAcquirable<Player>() }
            verify(exactly = 1) { acquired.get() }
            verify(exactly = 1) { acquirable.lock() }
            verify(exactly = 1) { acquired.unlock() }
        }
    }

}