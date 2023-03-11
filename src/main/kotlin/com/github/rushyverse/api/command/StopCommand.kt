package com.github.rushyverse.api.command

import com.github.rushyverse.api.extension.async
import com.github.rushyverse.api.extension.setDefaultExecutorSuspend
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.ServerProcess
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceManager
import net.minestom.server.permission.Permission

/**
 * Command to stop the server.
 */
public class StopCommand(
    name: String,
    vararg aliases: String,
    private val serverProcess: ServerProcess = MinecraftServer.process(),
    private val instanceManager: InstanceManager? = MinecraftServer.getInstanceManager()
) : Command(name, *aliases) {

    /**
     * Enum of permission to perform [command][StopCommand].
     * @property permission Permission.
     */
    public enum class Permissions(public val permission: Permission) {
        /**
         * Permission to give item to another player.
         */
        EXECUTE(Permission("stop"))
    }

    init {
        setCondition { sender, _ ->
            sender !is Player || sender.hasPermission(Permissions.EXECUTE.permission)
        }

        setDefaultExecutorSuspend { _, _ ->
            kickPlayers()
            serverProcess.stop()
        }
    }

    /**
     * Kick all players from the server.
     */
    private suspend fun kickPlayers() {
        val instanceManager = instanceManager ?: return
        val stopComponent = Component.translatable("commands.stop.stopping")
        instanceManager.instances
            .asSequence()
            .flatMap { it.players }
            .map {
                it.async { kick(stopComponent) }
            }.toList().awaitAll()
    }
}