package com.github.rushyverse.api.extension

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Allows editing the profile of the player.
 * The method must be called into the server thread.
 */
public inline fun Player.editProfile(editor: PlayerProfile.() -> Unit) {
    contract { callsInPlace(editor, InvocationKind.EXACTLY_ONCE) }
    playerProfile = playerProfile.apply(editor)
}

/**
 * Check if the [item] is equals to the one of the item in player's hands.
 * @receiver Player.
 * @param item Item to compare the item in hands.
 * @return `true` if present in one of hands, `false` otherwise.
 */
public fun Player.itemInHand(item: ItemStack): Boolean = itemInHand {
    it == item
}

/**
 * Use the lambda [finder] to check if a specific item is present in one of both hands.
 * @receiver Player.
 * @param finder Lambda method to compare item.
 * @return `true` if present in one of hands, `false` otherwise.
 */
public inline fun Player.itemInHand(finder: (ItemStack) -> Boolean): Boolean {
    contract { callsInPlace(finder, InvocationKind.AT_LEAST_ONCE) }
    val inventory = inventory
    return finder(inventory.itemInMainHand) || finder(inventory.itemInOffHand)
}
