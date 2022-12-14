package fr.rushy.api

import fr.rushy.api.command.GamemodeCommand
import fr.rushy.api.command.GiveCommand
import fr.rushy.api.command.KickCommand
import fr.rushy.api.command.StopCommand
import fr.rushy.api.event.handle.handlePlayerLoginEvent
import fr.rushy.api.world.StoneGenerator
import net.minestom.server.MinecraftServer

public class Main {

    public companion object {

        @JvmStatic
        public fun main(args: Array<String>) {
            val minecraftServer = MinecraftServer.init()
            val instanceManager = MinecraftServer.getInstanceManager()
            val instanceContainer = instanceManager.createInstanceContainer()
            instanceContainer.setGenerator(StoneGenerator())

            registerCommands()

            val globalEventHandler = MinecraftServer.getGlobalEventHandler()
            handlePlayerLoginEvent(globalEventHandler, instanceContainer)

            val port = args.getOrElse(0) { "25565" }.toInt()
            minecraftServer.start("0.0.0.0", port)
        }

        /**
         * Register all commands.
         */
        private fun registerCommands() {
            val commandManager = MinecraftServer.getCommandManager()
            commandManager.register(StopCommand())
            commandManager.register(KickCommand())
            commandManager.register(GiveCommand())
            commandManager.register(GamemodeCommand())
        }
    }
}