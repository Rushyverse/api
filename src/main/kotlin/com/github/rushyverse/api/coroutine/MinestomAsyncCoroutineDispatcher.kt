package com.github.rushyverse.api.coroutine

import kotlinx.coroutines.Dispatchers
import net.minestom.server.MinecraftServer
import net.minestom.server.ServerProcess
import net.minestom.server.timer.ExecutionType

/**
 * Static instance of [MinestomAsyncCoroutineDispatcher].
 */
internal val minestomAsyncDispatcher = MinestomAsyncCoroutineDispatcher(MinecraftServer.process())

/**
 * @see [MinestomAsyncCoroutineDispatcher]
 */
public val Dispatchers.MinestomAsync: MinestomCoroutineDispatcher get() = minestomAsyncDispatcher

/**
 * Dispatcher to execute task in a [async][ExecutionType.ASYNC] context of the server.
 * @property serverProcess Server's process
 */
public class MinestomAsyncCoroutineDispatcher(serverProcess: ServerProcess) :
    MinestomCoroutineDispatcher(serverProcess, ExecutionType.ASYNC)