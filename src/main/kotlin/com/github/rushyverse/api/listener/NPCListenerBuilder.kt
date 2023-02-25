package com.github.rushyverse.api.listener

import com.github.rushyverse.api.entity.NPCEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent

/**
 * Builder to create an event node to listen to NPC events.
 */
public object NPCListenerBuilder {

    /**
     * Create an event node to listen to NPC events.
     * @return A new event node.
     */
    public fun createEventNode(): EventNode<Event> {
        return EventNode.all("npc").apply {
            addInteractListener(this)
        }
    }

    /**
     * Add a listener to the node to listen when a player interacts with an NPC.
     * @param node Event node to add the listener.
     */
    private fun addInteractListener(node: EventNode<Event>) {
        node.addListener(
            EventListener.builder(PlayerEntityInteractEvent::class.java)
                .filter { it.target is NPCEntity && it.hand == Player.Hand.MAIN }
                .handler {
                    val npc = it.target as NPCEntity
                    npc.onInteract(it)
                }
                .build()
        )
    }
}