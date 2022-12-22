package io.github.rushyverse.api.extension

import io.github.rushyverse.api.coroutine.MinestomSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.CommandSyntax
import net.minestom.server.command.builder.arguments.Argument

/**
 * Allows to set the default executor command in a coroutine scope.
 * @see [Command.setDefaultExecutor]
 * @receiver Command where the default executor will be set.
 * @param executor Executor to process the command in a suspendable context.
 * @param coroutineScope Coroutine scope where the default executor will be executed.
 */
public inline fun Command.setDefaultExecutorSuspend(
    coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope,
    crossinline executor: suspend (sender: CommandSender, context: CommandContext) -> Unit
): Unit = setDefaultExecutor { sender, context ->
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        executor(sender, context)
    }
}

/**
 * Allows to add a syntax to a command in a coroutine scope.
 * @see [Command.addSyntax]
 * @receiver Command where the syntax will be added.
 * @param executor Executor to process the command in a suspendable context.
 * @param arguments Arguments of the syntax.
 * @param coroutineScope Coroutine scope where the syntax will be executed.
 */
public inline fun Command.addSyntaxSuspend(
    crossinline executor: suspend (sender: CommandSender, context: CommandContext) -> Unit,
    vararg arguments: Argument<*>,
    coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope
): MutableCollection<CommandSyntax> = addSyntax({ sender, context ->
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        executor(sender, context)
    }
}, *arguments)

/**
 * Allows to add a conditional syntax to a command in a coroutine scope.
 * @see [Command.addConditionalSyntax]
 * @receiver Command where the syntax will be added.
 * @param condition Condition to check before executing the syntax.
 * @param executor Executor to process the command in a suspendable context.
 * @param arguments Arguments of the syntax.
 * @param coroutineScope Coroutine scope where the syntax will be executed.
 */
public inline fun Command.addConditionalSyntaxSuspend(
    noinline condition: (sender: CommandSender, commandString: String?) -> Boolean,
    crossinline executor: suspend (sender: CommandSender, context: CommandContext) -> Unit,
    vararg arguments: Argument<*>,
    coroutineScope: CoroutineScope = Dispatchers.MinestomSync.scope
): MutableCollection<CommandSyntax> = addConditionalSyntax(condition, { sender, context ->
    coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
        executor(sender, context)
    }
}, *arguments)