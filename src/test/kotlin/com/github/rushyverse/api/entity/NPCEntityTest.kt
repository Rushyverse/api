package com.github.rushyverse.api.entity

import com.github.rushyverse.api.position.IAreaLocatable
import io.mockk.*
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class NPCEntityTest {

    @Nested
    inner class LookNearbyPlayer {

        @Test
        fun `should throw exception if area is null`() {
            val npc = NPCEntity(EntityType.PLAYER, areaTrigger = null)
            assertThrows<IllegalStateException> { npc.lookNearbyPlayer() }
        }

        @Test
        fun `should keep the position if no player is near`() {
            val area = mockk<IAreaLocatable<Player>>() {
                every { entitiesInArea } returns emptySet()
            }
            val npc = NPCEntity(EntityType.PLAYER, area)
            val expectedPos = npc.position
            println(expectedPos)

            npc.lookNearbyPlayer()
            assertEquals(expectedPos, npc.position)
        }

        @Test
        fun `should look at the player if there is one`() {
            val player = mockk<Player>()

            val area = mockk<IAreaLocatable<Player>>() {
                every { entitiesInArea } returns setOf(player)
            }
            val npc = NPCEntity(EntityType.PLAYER, area)

            val slotPlayer = slot<Player>()
            val npcSpyk = spyk(npc) {
                justRun { lookAt(capture(slotPlayer)) }
            }

            npcSpyk.lookNearbyPlayer()

            verify(exactly = 1) { npcSpyk.lookAt(any<Player>()) }
            assertEquals(player, slotPlayer.captured)
        }

    }
}