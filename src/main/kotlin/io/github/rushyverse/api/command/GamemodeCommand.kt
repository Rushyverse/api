package io.github.rushyverse.api.command

import io.github.rushyverse.api.command.CommandMessages.sendMissingPermissionMessage
import io.github.rushyverse.api.command.CommandMessages.sendPlayerNotFoundMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.permission.Permission
import java.util.*

/**
 * Command to define the game mode of a player.
 */
public class GamemodeCommand : Command("gamemode") {

    /**
     * Enum of permission to perform [command][GamemodeCommand].
     * @property permission Permission.
     */
    public enum class Permissions(public val permission: Permission) {
        /**
         * Permission to change game mode of oneself.
         */
        SELF(Permission("gamemode.self")),

        /**
         * Permission to change game mode of another player.
         */
        OTHER(Permission("gamemode.other"))
    }

    init {
        val gamemodeArg = createGamemodeArgument()
        val playerArg = argumentPlayer()

        setCondition { sender, commandLine ->
            if(sender !is Player || Permissions.values().any { sender.hasPermission(it.permission) }) {
                return@setCondition true
            }
            if(commandLine != null) {
                sendMissingPermissionMessage(sender)
            }
            return@setCondition false
        }

        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        addSelfSyntax(gamemodeArg)
        addOtherSyntax(playerArg, gamemodeArg)
    }

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(Component.text("Usage: /$commandName <gamemode> [target]", NamedTextColor.RED))
    }

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayer(): ArgumentEntity = ArgumentType.Entity("target").onlyPlayers(true).singleEntity(true)

    /**
     * Create a new argument targeting the gamemode choice.
     * @return New argument.
     */
    private fun createGamemodeArgument(): ArgumentEnum<GameMode> {
        return ArgumentType.Enum("gamemode", GameMode::class.java).setFormat(ArgumentEnum.Format.LOWER_CASED)
            .apply {
                setCallback { sender, exception ->
                    sender.sendMessage(
                        Component.text("Invalid gamemode ", NamedTextColor.RED)
                            .append(Component.text(exception.input, NamedTextColor.WHITE))
                            .append(Component.text("!"))
                    )
                }
            }
    }

    /**
     * Define the syntax to process the command on another player.
     * @param playerArg Argument to retrieve player(s) selected.
     * @param gamemodeArg Argument to retrieve game mode selected.
     */
    private fun addOtherSyntax(playerArg: ArgumentEntity, gamemodeArg: ArgumentEnum<GameMode>) {
        addSyntax({ sender, context ->
            if (!sender.hasPermission(Permissions.OTHER.permission)) {
                sendMissingPermissionMessage(sender)
                return@addSyntax
            }

            val finder = context.get(playerArg)
            val player = finder.findFirstPlayer(sender)
            if (player == null) {
                sendPlayerNotFoundMessage(sender)
                return@addSyntax
            }

            player.getAcquirable<Player>().sync {
                val gamemode = context.get(gamemodeArg)
                if (player == sender) {
                    processSelf(player, gamemode)
                } else {
                    processOther(sender, player, gamemode)
                }
            }
        }, gamemodeArg, playerArg)
    }

    /**
     * Define the syntax to process the command on the sender.
     * @param gamemodeArg Argument to retrieve game mode selected.
     */
    private fun addSelfSyntax(gamemodeArg: ArgumentEnum<GameMode>) {
        addSyntax({ sender, context ->
            if (sender !is Player || !sender.hasPermission(Permissions.SELF.permission)) {
                sendMissingPermissionMessage(sender)
                return@addSyntax
            }

            sender.getAcquirable<Player>().sync {
                val gamemode = context.get(gamemodeArg)
                processSelf(sender, gamemode)
            }
        }, gamemodeArg)
    }

    /**
     * Change the game mode of the player and notify it.
     * @param player Player who has his game mode modified.
     * @param gamemode Game mode applied to the player.
     */
    private fun processSelf(player: Player, gamemode: GameMode) {
        player.apply {
            gameMode = gamemode
            val gamemodeComponent = createTranslatableGameModeComponent(gamemode)
            sendMessage(Component.translatable("commands.gamemode.success.self", gamemodeComponent))
        }
    }

    /**
     * Change the game m ode of the targeted players and notify them.
     * @param sender Command's sender.
     * @param player List of entities targeted by the sender.
     * @param gamemode Game mode applied to the players.
     */
    private fun processOther(sender: CommandSender, player: Player, gamemode: GameMode) {
        player.gameMode = gamemode
        val gamemodeComponent = createTranslatableGameModeComponent(gamemode)
        val playerName: Component = player.displayName ?: player.name

        player.sendMessage(Component.translatable("gameMode.changed", gamemodeComponent))
        sender.sendMessage(Component.translatable("commands.gamemode.success.other", playerName, gamemodeComponent))
    }

    /**
     * Create a component representing the game mode.
     * @param gamemode Game mode.
     * @return Component representing the game mode.
     */
    private fun createTranslatableGameModeComponent(gamemode: GameMode): Component {
        return Component.translatable("gameMode." + gamemode.name.lowercase(Locale.ROOT))
    }
}