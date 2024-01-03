package com.github.rushyverse.api.extension.event

import com.github.shynixn.mccoroutine.bukkit.callSuspendingEvent
import kotlinx.coroutines.joinAll
import org.bukkit.Bukkit
import org.bukkit.block.BlockFace
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

/**
 * Future life of the damaged entity.
 * @return If the entity is not damageable returns null, otherwise return the future health.
 */
public fun EntityDamageEvent.finalDamagedHealth(): Double? {
    val damaged = entity
    return if (damaged is Damageable) {
        damaged.health - finalDamage
    } else {
        null
    }
}

/**
 * Create a [PlayerInteractEvent] to simulate a right click with an item for a player and call it.
 * @param plugin Plugin to call the event.
 * @param player Player who clicked.
 * @param item Item that was clicked.
 */
public suspend fun callRightClickOnItemEvent(plugin: Plugin, player: Player, item: ItemStack) {
    val rightClickWithItemEvent = createRightClickEventWithItem(player, item)
    Bukkit.getPluginManager().callSuspendingEvent(rightClickWithItemEvent, plugin).joinAll()
}

/**
 * Create a PlayerInteractEvent to simulate a right click with an item.
 * @param player Player who clicked.
 * @param item Item that was clicked.
 * @return The new event.
 */
private fun createRightClickEventWithItem(
    player: Player,
    item: ItemStack
) = PlayerInteractEvent(
    player,
    Action.RIGHT_CLICK_AIR,
    item,
    null,
    BlockFace.NORTH // random value not null
)
