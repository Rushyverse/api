package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.assertCoroutineContextFromScope
import com.github.rushyverse.api.utils.randomString
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.coroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CommandExtTest {

    @Test
    fun `should use dispatcher to process set default executor suspend`() {
        val command = Command("test")
        var executed = false

        val senderMock = mockk<CommandSender>()
        val contextMock = mockk<CommandContext>()

        val scope = CoroutineScope(Dispatchers.Default)

        val currentThread = Thread.currentThread()
        val latch = CountDownLatch(1)

        command.setDefaultExecutorSuspend(scope) { sender, context ->
            assertCoroutineContextFromScope(scope, coroutineContext)
            assertEquals(senderMock, sender)
            assertEquals(contextMock, context)
            executed = true

            assertEquals(currentThread, Thread.currentThread())
            yield()
            assertNotEquals(currentThread, Thread.currentThread())
            latch.countDown()
        }
        command.defaultExecutor?.apply(senderMock, contextMock)
        latch.await()
        assertTrue(executed)
    }

    @Test
    fun `should use dispatcher to process add syntax suspend`() {
        val command = Command("test")
        val stringArg = ArgumentType.String("string")
        val intArg = ArgumentType.Integer("int")
        var executed = false

        val senderMock = mockk<CommandSender>()
        val contextMock = mockk<CommandContext>()
        val scope = CoroutineScope(Dispatchers.Default)

        val currentThread = Thread.currentThread()
        val latch = CountDownLatch(1)

        command.addSyntaxSuspend({ sender, context ->
            assertCoroutineContextFromScope(scope, coroutineContext)
            assertEquals(senderMock, sender)
            assertEquals(contextMock, context)
            executed = true

            assertEquals(currentThread, Thread.currentThread())
            yield()
            assertNotEquals(currentThread, Thread.currentThread())
            latch.countDown()
        }, stringArg, intArg, coroutineScope = scope)

        command.syntaxes.first().executor.apply(senderMock, contextMock)
        latch.await()
        assertTrue(executed)
    }

    @Test
    fun `should use dispatcher to process add conditional syntax suspend`() {
        val command = Command("test")
        val stringArg = ArgumentType.String("string")
        val intArg = ArgumentType.Integer("int")
        var executed = false
        var conditionalExecuted = false

        val senderMock = mockk<CommandSender>()
        val contextMock = mockk<CommandContext>()
        val scope = CoroutineScope(Dispatchers.Default)

        val currentThread = Thread.currentThread()
        val latch = CountDownLatch(1)

        val expectedCommandString = randomString()
        command.addConditionalSyntaxSuspend(
            { sender, commandString ->
                assertEquals(senderMock, sender)
                assertEquals(expectedCommandString, commandString)
                conditionalExecuted = true
                true
            },
            { sender, context ->
                assertCoroutineContextFromScope(scope, coroutineContext)
                assertEquals(senderMock, sender)
                assertEquals(contextMock, context)
                executed = true

                assertEquals(currentThread, Thread.currentThread())
                yield()
                assertNotEquals(currentThread, Thread.currentThread())
                latch.countDown()

            }, stringArg, intArg, coroutineScope = scope
        )

        val syntax = command.syntaxes.first()
        syntax.commandCondition!!.canUse(senderMock, expectedCommandString)
        assertFalse(executed)
        assertTrue(conditionalExecuted)

        syntax.executor.apply(senderMock, contextMock)
        latch.await()
        assertTrue(executed)
    }
}