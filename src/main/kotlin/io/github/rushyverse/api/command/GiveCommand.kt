package io.github.rushyverse.api.command

import io.github.rushyverse.api.extension.sync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.permission.Permission

/**
 * Command to give item to a player.
 */
public class GiveCommand : Command("give") {

    /**
     * Enum of permission to perform [command][GiveCommand].
     * @property permission Permission.
     */
    public enum class Permissions(public val permission: Permission) {
        /**
         * Permission to give item to oneself.
         */
        EXECUTE(Permission("give")),
    }

    init {
        setCondition { sender, commandLine ->
            if (sender !is Player || Permissions.values().any { sender.hasPermission(it.permission) }) {
                return@setCondition true
            }
            if (commandLine != null) {
                CommandMessages.sendMissingPermissionMessage(sender)
            }
            false
        }

        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        val playersArg = argumentPlayers()
        val itemArg = argumentItem()
        val amountArg = argumentAmount()

        setSyntax(playersArg, itemArg, amountArg)
    }

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(Component.text("Usage: /$commandName <targets> <item> [amount]", NamedTextColor.RED))
    }

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayers(): ArgumentEntity =
        ArgumentType.Entity("targets").singleEntity(false)

    /**
     * Create a new argument targeting the amount of item.
     * @return New argument.
     */
    private fun argumentAmount() = ArgumentType.Integer("amount").setDefaultValue(1)

    /**
     * Create a new argument targeting item to give.
     * @return New argument.
     */
    private fun argumentItem(): ArgumentItemStack = ArgumentType.ItemStack("item")

    /**
     * Define the syntax to process the command.
     */
    private fun setSyntax(playersArg: ArgumentEntity, itemArg: ArgumentItemStack, amountArg: Argument<Int>) {
        addSyntax({ sender, context ->
            val targets = context.get(playersArg).find(sender).asSequence().filterIsInstance<Player>()

            val amount = context.get(amountArg)
            val item = context.get(itemArg).withAmount(amount)

            process(sender, targets, item)
        }, playersArg, itemArg, amountArg)
    }

    /**
     * Give the item and notify sender.
     * @param sender Command's sender.
     * @param targets Player who will receive the item.
     * @param item Item given.
     */
    private fun process(
        sender: CommandSender,
        targets: Sequence<Player>,
        item: ItemStack
    ) {
        val receivers = targets.map {
            it.sync {
                inventory.addItemStack(item)
                name
            }
        }.toList()

        if (receivers.size == 1) {
            sender.sendMessage(
                Component.translatable(
                    "commands.give.success.single",
                    Component.text(item.amount()),
                    Component.text(item.material().name()),
                    receivers.first()
                )
            )
        } else {
            sender.sendMessage(
                Component.translatable(
                    "commands.give.success.multiple",
                    Component.text(item.amount()),
                    Component.text(item.material().name()),
                    Component.text(receivers.size)
                )
            )
        }

    }
}