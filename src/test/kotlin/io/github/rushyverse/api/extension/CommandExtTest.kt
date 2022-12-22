package io.github.rushyverse.api.extension

import io.github.rushyverse.api.utils.randomString
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommandExtTest {

    @Test
    fun `should use dispatcher to process set default executor suspend`() {
        val command = Command("test")
        var executed = false
        val senderMock = mockk<CommandSender>()
        val contextMock = mockk<CommandContext>()
        val scope = CoroutineScope(Dispatchers.Default)
        command.setDefaultExecutorSuspend(scope) { sender, context ->
            checkContextFromScope(scope, coroutineContext)
            assertEquals(senderMock, sender)
            assertEquals(contextMock, context)
            executed = true
        }
        command.defaultExecutor?.apply(senderMock, contextMock)
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

        command.addSyntaxSuspend({ sender, context ->
            checkContextFromScope(scope, coroutineContext)
            assertEquals(senderMock, sender)
            assertEquals(contextMock, context)
            executed = true
        }, stringArg, intArg, coroutineScope = scope)

        command.syntaxes.first().executor.apply(senderMock, contextMock)
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

        val expectedCommandString = randomString()
        command.addConditionalSyntaxSuspend(
            { sender, commandString ->
                assertEquals(senderMock, sender)
                assertEquals(expectedCommandString, commandString)
                conditionalExecuted = true
                true
            },
            { sender, context ->
            checkContextFromScope(scope, coroutineContext)
            assertEquals(senderMock, sender)
            assertEquals(contextMock, context)
            executed = true
        }, stringArg, intArg, coroutineScope = scope)

        val syntax = command.syntaxes.first()
        syntax.commandCondition!!.canUse(senderMock, expectedCommandString)
        assertFalse(executed)
        assertTrue(conditionalExecuted)

        syntax.executor.apply(senderMock, contextMock)
        assertTrue(executed)
    }

    private fun checkContextFromScope(scope: CoroutineScope, coroutineContext: CoroutineContext) {
        assertEquals(scope.coroutineContext.job.key, coroutineContext.job.key)
    }
}