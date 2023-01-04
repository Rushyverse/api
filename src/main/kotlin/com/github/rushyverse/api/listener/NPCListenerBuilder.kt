package com.github.rushyverse.api.listener

import com.github.rushyverse.api.entity.NPCEntity
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventListener
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent

public object NPCListenerBuilder {

    public fun createEventNode(): EventNode<Event> {
        return EventNode.all("npc").apply {
            addInteractListener(this)
        }
    }

    private fun addInteractListener(node: EventNode<Event>) {
        node.addListener(EventListener.builder(PlayerEntityInteractEvent::class.java)
            .filter { it.hand == Player.Hand.MAIN && it.target is NPCEntity }
            .handler {
                val npc = it.target as NPCEntity
                npc.onInteract(it)
            }
            .build()
        )
    }
}