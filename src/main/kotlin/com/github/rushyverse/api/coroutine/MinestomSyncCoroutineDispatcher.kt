package com.github.rushyverse.api.coroutine

import kotlinx.coroutines.Dispatchers
import net.minestom.server.MinecraftServer
import net.minestom.server.ServerProcess
import net.minestom.server.timer.ExecutionType

/**
 * Static instance of [MinestomSyncCoroutineDispatcher].
 */
internal val minestomSyncDispatcher = MinestomSyncCoroutineDispatcher(MinecraftServer.process())

/**
 * @see [MinestomSyncCoroutineDispatcher]
 */
public val Dispatchers.MinestomSync: MinestomCoroutineDispatcher get() = minestomSyncDispatcher

/**
 * Dispatcher to execute task in a [sync][ExecutionType.SYNC] context of the server.
 * @property serverProcess Server's process
 */
public class MinestomSyncCoroutineDispatcher(serverProcess: ServerProcess) :
    MinestomCoroutineDispatcher(serverProcess, ExecutionType.SYNC)