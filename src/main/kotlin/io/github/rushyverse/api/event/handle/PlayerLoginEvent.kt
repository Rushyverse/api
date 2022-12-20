package io.github.rushyverse.api.event.handle

import io.github.rushyverse.api.permission.CustomPermission
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.permission.Permission

/**
 * Handle the [PlayerLoginEvent].
 * @param globalEventHandler Event handler.
 * @param instanceContainer Instance container of the server.
 */
public fun handlePlayerLoginEvent(
    globalEventHandler: GlobalEventHandler,
    instanceContainer: InstanceContainer
) {
    globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
        val player = event.player
        event.setSpawningInstance(instanceContainer)
        player.respawnPoint = Pos(0.0, 42.0, 0.0)
        sequenceOf(
            CustomPermission.GIVE,
            CustomPermission.STOP_SERVER,
            CustomPermission.GAMEMODE,
            CustomPermission.KICK
        ).map { Permission(it) }.forEach(player::addPermission)
    }
}