package io.github.rushyverse.api.command

import io.github.rushyverse.api.extension.async
import io.github.rushyverse.api.extension.setDefaultExecutorSuspend
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission

/**
 * Command to stop the server.
 */
public class StopCommand : Command("stop") {

    /**
     * Enum of permission to perform [command][StopCommand].
     * @property permission Permission.
     */
    public enum class Permissions(public val permission: Permission) {
        /**
         * Permission to give item to another player.
         */
        EXECUTE(Permission("stop.execute"))
    }

    init {
        setCondition { sender, commandLine ->
            if (sender !is Player || sender.hasPermission(Permissions.EXECUTE.permission)) {
                return@setCondition true
            }
            if (commandLine != null) {
                CommandMessages.sendMissingPermissionMessage(sender)
            }
            false
        }

        setDefaultExecutorSuspend { _, _ ->
            val stopComponent = Component.translatable("commands.stop.stopping")
            MinecraftServer.getInstanceManager().instances
                .asSequence()
                .flatMap { it.players }
                .map {
                    it.async { kick(stopComponent) }
                }.toList().awaitAll()

            MinecraftServer.stopCleanly()
        }
    }
}