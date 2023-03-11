package com.github.rushyverse.api.command

import com.github.rushyverse.api.command.CommandMessages.sendPlayerNotFoundMessage
import com.github.rushyverse.api.extension.sync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission

/**
 * Command to kick a player.
 * @param isProtected Lambda to check if a player is protected from being kicked.
 */
public class KickCommand(
    name: String = "kick",
    vararg aliases: String,
    private val isProtected: (CommandSender, Player) -> Boolean = { _, target ->
        target.sync { hasPermission(Permissions.EXECUTE.permission) }
    }
) : Command(name, *aliases) {

    /**
     * Enum of permission to perform [command][KickCommand].
     * @property permission Permission.
     */
    public enum class Permissions(public val permission: Permission) {
        /**
         * Permission to kick another player.
         */
        EXECUTE(Permission("kick"))
    }

    init {
        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        setCondition { sender, _ ->
            sender !is Player || sender.hasPermission(Permissions.EXECUTE.permission)
        }

        val playerArg = argumentPlayer()
        val reasonArg = argumentReason()
        addSyntaxPlayer(playerArg, reasonArg)
    }

    /**
     * Create a new argument to retrieve the reason of the kick.
     * @return New string argument
     */
    private fun argumentReason(): Argument<String> =
        ArgumentType.String("reason").setDefaultValue("No reason specified")

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayer(): ArgumentEntity =
        ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true)

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(Component.text("Usage: /$commandName <target>", NamedTextColor.RED))
    }

    /**
     * Define the syntax to process the command on a player.
     * @param playerArg Argument to retrieve player selected.
     */
    private fun addSyntaxPlayer(playerArg: ArgumentEntity, reasonArg: Argument<String>) {
        addSyntax({ sender, context ->
            val player = context.get(playerArg).findFirstPlayer(sender)
            if (player == null) {
                sendPlayerNotFoundMessage(sender)
                return@addSyntax
            }

            if (!isProtected(sender, player)) {
                sender.sendMessage(Component.text("You can't kick this player", NamedTextColor.RED))
                return@addSyntax
            }

            val reasonComponent = Component.text(context.get(reasonArg))

            kickPlayer(player, reasonComponent)

            sender.sendMessage(
                Component.translatable("commands.kick.success", player.name, reasonComponent)
                    .color(NamedTextColor.YELLOW)
            )
        }, playerArg, reasonArg)
    }

    /**
     * Kick the player from the server.ad
     *
     * @param player Player to kick.
     * @param reason Component to display as the reason of the kick.
     */
    private fun kickPlayer(
        player: Player,
        reason: Component
    ) {
        val kickComponent = Component.translatable("multiplayer.disconnect.kicked")
            .appendNewline()
            .append(reason)

        player.sync { kick(kickComponent) }
    }
}