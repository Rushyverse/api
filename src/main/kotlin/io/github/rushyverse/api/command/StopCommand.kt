package io.github.rushyverse.api.command

import io.github.rushyverse.api.permission.CustomPermission
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import kotlin.time.Duration.Companion.seconds

/**
 * Command to stop the server.
 */
public class StopCommand : Command("stop") {

    init {
        setCondition { sender, _ ->
            sender !is Player || sender.hasPermission(CustomPermission.STOP_SERVER)
        }

        setDefaultExecutor { _, _ ->
            GlobalScope.launch {
                MinecraftServer.getInstanceManager().instances.asSequence().flatMap {
                    it.players
                }.forEach {
                    it.kick("Server closed")
                }

                delay(1.seconds)

                MinecraftServer.stopCleanly()
            }

        }

    }
}