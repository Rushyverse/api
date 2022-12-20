package io.github.rushyverse.api.command

import io.github.rushyverse.api.permission.CustomPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player

/**
 * Command to kick a player.
 */
public class KickCommand : Command("kick") {

    init {
        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        setCondition { sender, _ ->
            sender !is Player || sender.hasPermission(CustomPermission.KICK)
        }

        val playerArg = argumentPlayer()
        addSyntaxPlayer(playerArg)
    }

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayer(): ArgumentEntity =
        ArgumentType.Entity("target").onlyPlayers(true)

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(
            Component.text("Usage: /$commandName <target>", NamedTextColor.RED)
        )
    }

    /**
     * Define the syntax to process the command on a player.
     * @param playerArg Argument to retrieve player selected.
     */
    private fun addSyntaxPlayer(playerArg: ArgumentEntity) {
        addSyntax({ sender, context ->
            val player = context.get(playerArg).find(sender).filterIsInstance<Player>().firstOrNull()
            if (player == null) {
                sendPlayerNotFoundMessage(sender)
                return@addSyntax
            }

            process(sender, player)
        }, playerArg)
    }

    /**
     * Process the kick on [target] and notify [sender].
     * @param sender Command's sender.
     * @param target Player who will be kicked.
     */
    private fun process(sender: CommandSender, target: Player) {
        target.kick(Component.text("++").color(NamedTextColor.YELLOW))
        sender.sendMessage(Component.text("The player [${target.name}] is kicked").color(NamedTextColor.YELLOW))
    }
}