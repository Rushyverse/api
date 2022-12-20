package io.github.rushyverse.api.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player

/**
 * Send a message to the sender because no player was found in a previous process.
 * @param sender Sender who will receive the message.
 */
public fun sendPlayerNotFoundMessage(sender: CommandSender) {
    if (sender is Player) {
        sender.sendMessage(Component.translatable("argument.entity.notfound.player", NamedTextColor.RED))
    } else {
        sender.sendMessage(Component.text("No player was found", NamedTextColor.RED))
    }
}

/**
 * Send a message to the sender because he doesn't have the permission to execute the command.
 * @param sender Sender who will receive the message.
 */
public fun sendNoPermissionMessage(sender: CommandSender) {
    sender.sendMessage(
        Component.translatable(
            "commands.help.failed",
            NamedTextColor.RED
        )
    )
}