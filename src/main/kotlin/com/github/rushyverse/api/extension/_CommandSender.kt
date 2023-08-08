package com.github.rushyverse.api.extension

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.CommandSender

/**
 * Send a error message to a sender.
 * @receiver Sender that will receive the message.
 * @param message Message.
 */
public fun CommandSender.sendMessageError(message: String) {
    sendMessage(text {
        content(message)
        color(NamedTextColor.RED)
    })
}

/**
 * Verify if a sender has several permissions.
 * Iterate on all permissions and check the presence with the function [CommandSender.hasPermission].
 * @receiver Sender with permissions.
 * @param permissions Bukkit Permissions.
 * @return `true` if the sender has all permission, `false` otherwise.
 */
public fun CommandSender.hasPermissions(permissions: Array<String>): Boolean =
    permissions.all(this::hasPermission)

/**
 * Verify if a sender has several permissions.
 * Iterate on all permissions and check the presence with the function [CommandSender.hasPermission].
 * @receiver Sender with permissions.
 * @param permissions Bukkit Permissions.
 * @return `true` if the sender has all permission, `false` otherwise.
 */
public fun CommandSender.hasPermissions(permissions: Iterable<String>): Boolean =
    permissions.all(this::hasPermission)
