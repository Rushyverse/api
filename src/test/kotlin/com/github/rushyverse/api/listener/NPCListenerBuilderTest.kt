package com.github.rushyverse.api.listener

import com.github.rushyverse.api.entity.NPCEntity
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import org.junit.jupiter.api.Nested
import kotlin.test.BeforeTest
import kotlin.test.Test

class NPCListenerBuilderTest {

    lateinit var node: EventNode<Event>

    @BeforeTest
    fun onBefore() {
        node = NPCListenerBuilder.createEventNode()
    }

    @Nested
    inner class CreateEventNode {

        @Test
        fun `create event node with npc name`() {
            assert(node.name == "npc")
        }

    }

    @Nested
    inner class AddInteractListener {

        @Test
        fun `node event has a register listener for interact event`() {
            node.hasListener(PlayerEntityInteractEvent::class.java)
        }

        @Test
        fun `doesn't trigger when target is not an npc`() {
            val event = mockk<PlayerEntityInteractEvent>()
            every { event.target } returns mockk()
            node.call(event)
            verify(exactly = 1) { event.target }
        }

        @Test
        fun `doesn't trigger when hand is not main hand`() {
            val event = mockk<PlayerEntityInteractEvent>()
            every { event.target } returns mockk<NPCEntity>()
            every { event.hand } returns Player.Hand.OFF
            node.call(event)
            verify(exactly = 1) { event.target }
            verify(exactly = 1) { event.hand }
        }

        @Test
        fun `trigger interaction method of npc`() {
            val event = mockk<PlayerEntityInteractEvent>()
            val target = mockk<NPCEntity>()
            justRun { target.onInteract(event) }

            every { event.target } returns target
            every { event.hand } returns Player.Hand.MAIN

            node.call(event)
            verify(exactly = 2) { event.target }
            verify(exactly = 1) { event.hand }
            verify(exactly = 1) { target.onInteract(event) }
        }

    }
}