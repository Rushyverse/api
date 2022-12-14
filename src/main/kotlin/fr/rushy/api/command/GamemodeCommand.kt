package fr.rushy.api.command

import fr.rushy.api.permission.CustomPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import java.util.*

/**
 * Command to define the game mode of a player.
 */
public class GamemodeCommand : Command("gamemode") {

    init {
        val gamemodeArg = createGamemodeArgument()
        val playerArg = argumentPlayer()

        setCondition { sender, _ ->
            sender !is Player || hasPermission(sender)
        }

        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        addSyntaxGamemode(gamemodeArg)
        addSyntaxPlayer(playerArg, gamemodeArg)
    }

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayer(): ArgumentEntity =
        ArgumentType.Entity("target").onlyPlayers(true)

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
    private fun addSyntaxPlayer(playerArg: ArgumentEntity, gamemodeArg: ArgumentEnum<GameMode>) {
        addSyntax({ sender, context ->
            val finder = context.get(playerArg)
            val players = finder.find(sender).asSequence().filterIsInstance<Player>().toSet()
            processOther(sender, players, context.get(gamemodeArg))
        }, gamemodeArg, playerArg)
    }

    /**
     * Define the syntax to process the command on the sender.
     * @param gamemodeArg Argument to retrieve game mode selected.
     */
    private fun addSyntaxGamemode(gamemodeArg: ArgumentEnum<GameMode>) {
        addSyntax({ sender, context ->
            if (sender !is Player) {
                sendNoPermissionMessage(sender)
                return@addSyntax
            }

            processSelf(sender, context.get(gamemodeArg))
        }, gamemodeArg)
    }

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(
            Component.text("Usage: /$commandName <gamemode> [targets]", NamedTextColor.RED)
        )
    }

    /**
     * Check if the player has the permission to execute the command
     * @param sender Player.
     * @return `true` if the player is authorized to execute, `false` otherwise.
     */
    private fun hasPermission(sender: Player) = sender.hasPermission(CustomPermission.GAMEMODE)

    /**
     * Change the game mode of the player and notify it.
     * @param player Player who has his game mode modified.
     * @param gamemode Game mode applied to the player.
     */
    private fun processSelf(player: Player, gamemode: GameMode) {
        player.apply {
            gameMode = gamemode
            sendMessage(
                Component.text("Your gamemode has been changed to ${gamemode.name}")
            )
        }
    }

    /**
     * Change the game m ode of the targeted players and notify them.
     * @param sender Command's sender.
     * @param players List of entities targeted by the sender.
     * @param gamemode Game mode applied to the players.
     */
    private fun processOther(sender: CommandSender, players: Collection<Player>, gamemode: GameMode) {
        if (players.isEmpty()) {
            sendPlayerNotFoundMessage(sender)
            return
        }

        players.forEach {
            if (it == sender) {
                processSelf(it, gamemode)
            } else {
                it.gameMode = gamemode
                val gamemodeComponent: Component =
                    Component.translatable("gameMode." + gamemode.name.lowercase(Locale.ROOT))
                val playerName: Component = it.displayName ?: it.name

                it.sendMessage(Component.translatable("gameMode.changed", gamemodeComponent))
                sender.sendMessage(
                    Component.translatable(
                        "commands.gamemode.success.other",
                        playerName,
                        gamemodeComponent
                    )
                )
            }
        }
    }
}