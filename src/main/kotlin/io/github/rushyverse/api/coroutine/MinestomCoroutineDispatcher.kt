package io.github.rushyverse.api.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.minestom.server.ServerProcess
import net.minestom.server.timer.ExecutionType
import kotlin.coroutines.CoroutineContext

public open class MinestomCoroutineDispatcher(
    private val serverProcess: ServerProcess,
    public val type: ExecutionType
) : CoroutineDispatcher() {

    public val scope: CoroutineScope by lazy { CoroutineScope(this + SupervisorJob()) }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!serverProcess.isAlive) {
            return
        }

        serverProcess.scheduler().scheduleNextProcess(block, type)
    }
}