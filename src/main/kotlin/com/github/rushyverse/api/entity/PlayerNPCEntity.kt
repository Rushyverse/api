package com.github.rushyverse.api.entity

import net.kyori.adventure.text.Component
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.PlayerInfoPacket
import net.minestom.server.network.packet.server.play.PlayerInfoPacket.AddPlayer
import net.minestom.server.network.packet.server.play.PlayerInfoPacket.RemovePlayer

/**
 * A non-player character that looks like a player.
 * @property name Name of the NPC.
 * @property properties Properties of the NPC.
 * @property playerRemovePacket Remove player packet.
 */
public open class PlayerNPCEntity(
    public val name: String,
    public val properties: List<AddPlayer.Property> = emptyList(),
) : NPCEntity(EntityType.PLAYER) {

    private val playerRemovePacket = PlayerInfoPacket(
        PlayerInfoPacket.Action.REMOVE_PLAYER,
        listOf(RemovePlayer(uuid))
    )

    override fun updateNewViewer(player: Player) {
        val connection = player.playerConnection
        connection.sendPacket(createPlayerAddPacket())

        scheduleNextTick { connection.sendPacket(playerRemovePacket) }
        super.updateNewViewer(player)
    }

    /**
     * Create a packet to add the NPC in the player view.
     * @return A new packet.
     */
    private fun createPlayerAddPacket() = PlayerInfoPacket(
        PlayerInfoPacket.Action.ADD_PLAYER,
        listOf(
            AddPlayer(
                uuid,
                name,
                properties,
                GameMode.CREATIVE,
                0,
                customName ?: Component.text(name),
                null
            )
        )
    )
}