package fr.rushy.api.command

import fr.rushy.api.permission.CustomPermission
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

/**
 * Command to give item to a player.
 */
public class GiveCommand : Command("give") {

    init {
        setCondition { sender, _ ->
            sender !is Player || sender.hasPermission(CustomPermission.GIVE)
        }

        setDefaultExecutor { sender, context ->
            val commandName = context.commandName
            sendSyntaxMessage(sender, commandName)
        }

        val playerArg = argumentPlayer()
        val itemArg = argumentItem()
        val amountArg = argumentAmount()

        setSyntax(playerArg, itemArg, amountArg)
    }

    /**
     * Send an error message to define the usage syntax of the command.
     * @param sender Command's sender.
     * @param commandName Name of the command used to execute it.
     */
    private fun sendSyntaxMessage(sender: CommandSender, commandName: String) {
        sender.sendMessage(
            Component.text("Usage: /$commandName <target> <item> [amount]", NamedTextColor.RED)
        )
    }

    /**
     * Create a new argument targeting players name.
     * @return New argument.
     */
    private fun argumentPlayer(): ArgumentEntity = ArgumentType.Entity("target").onlyPlayers(true)

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
    private fun setSyntax(playerArg: ArgumentEntity, itemArg: ArgumentItemStack, amountArg: Argument<Int>) {
        addSyntax({ sender, context ->
            val target = context.get(playerArg).find(sender).filterIsInstance<Player>().firstOrNull()
            if (target == null) {
                sendPlayerNotFoundMessage(sender)
                return@addSyntax
            }

            val amount = context.get(amountArg)
            val item = context.get(itemArg).withAmount(amount)

            process(sender, target, item)
        }, playerArg, itemArg, amountArg)
    }

    /**
     * Give the item and notify sender.
     * @param sender Command's sender.
     * @param target Player who will receive the item.
     * @param item Item given.
     */
    private fun process(
        sender: CommandSender,
        target: Player,
        item: ItemStack
    ) {
        target.inventory.addItemStack(item)
        sender.sendMessage(Component.text("Items [${item.displayName} x ${item.amount()}] sent"))
    }
}