package com.github.rushyverse.api.entity

import com.github.rushyverse.api.position.IAreaLocatable
import com.github.rushyverse.api.utils.randomString
import io.mockk.*
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

abstract class CommonNPCEntityTest {

    @Nested
    inner class LookNearbyPlayer {

        @Test
        fun `should throw exception if area is null`() {
            val npc = createEntity(null)
            assertThrows<IllegalStateException> { npc.lookNearbyPlayer() }
        }

        @Test
        fun `should keep the position if no player is near`() {
            val area = mockk<IAreaLocatable<Player>> {
                every { entitiesInArea } returns emptySet()
            }
            val npc = createEntity(area)
            val expectedPos = npc.position

            npc.lookNearbyPlayer()
            assertEquals(expectedPos, npc.position)
        }

        @Test
        fun `should look at the player if there is one`() {
            val player = mockk<Player>()

            val area = mockk<IAreaLocatable<Player>> {
                every { entitiesInArea } returns setOf(player)
            }
            val npc = createEntity(area)

            val slotPlayer = slot<Player>()
            val npcSpy = spyk(npc) {
                justRun { lookAt(capture(slotPlayer)) }
            }

            npcSpy.lookNearbyPlayer()

            verify(exactly = 1) { npcSpy.lookAt(any<Player>()) }
            assertEquals(player, slotPlayer.captured)
        }

        @Test
        fun `should look at the near player in the list`() {
            val player1 = mockk<Player>()
            val player2 = mockk<Player>()

            val area = mockk<IAreaLocatable<Player>> {
                every { entitiesInArea } returns setOf(player1, player2)
            }
            val npc = createEntity(area)

            val slotPlayer = slot<Player>()
            val npcSpy = spyk(npc) {
                justRun { lookAt(capture(slotPlayer)) }
            }

            npcSpy.lookNearbyPlayer()

            verify(exactly = 1) { npcSpy.lookAt(any<Player>()) }
            assertEquals(player1, slotPlayer.captured)
        }

    }

    @Nested
    inner class OnEnterArea {

        @Test
        fun `should do nothing`() {
            val npc = createEntity(null)
            val player = mockk<Player>()
            npc.onEnterArea(player)
        }

    }

    @Nested
    inner class OnLeaveArea {

        @Test
        fun `should do nothing`() {
            val npc = createEntity(null)
            val player = mockk<Player>()
            npc.onLeaveArea(player)
        }

    }

    @Nested
    inner class OnInteract {

        @Test
        fun `should do nothing`() {
            val npc = createEntity(null)
            val event = mockk<PlayerEntityInteractEvent>()
            npc.onInteract(event)
        }

    }

    @Nested
    @EnvTest
    inner class Update {

        @Nested
        inner class OnEnterArea {

            @Test
            fun `should trigger enter area if new player is in area`(env: Env) {
                val pos = Pos(0.0, 0.0, 0.0)
                val flatInstance = env.createFlatInstance()
                val player = mockk<Player>()
                val area = mockk<IAreaLocatable<Player>> {
                    justRun { position = any() }
                    justRun { instance = any() }
                    every { updateEntitiesInArea() } returns Pair(setOf(player), emptySet())
                }
                val npc = createEntity(area)
                npc.setInstance(flatInstance, pos)
                val npcSpy = spyk(npc) {
                    justRun { onEnterArea(any()) }
                }
                npcSpy.update(0)

                verify(exactly = 1) { npcSpy.onEnterArea(player) }
            }

            @Test
            fun `should not trigger enter area if the player is always in the area`(env: Env) {
                val pos = Pos(0.0, 0.0, 0.0)
                val flatInstance = env.createFlatInstance()
                val player = mockk<Player>()
                val area = mockk<IAreaLocatable<Player>> {
                    justRun { position = any() }
                    justRun { instance = any() }
                    every { updateEntitiesInArea() } returns Pair(setOf(player), emptySet())
                }
                val npc = createEntity(area)
                npc.setInstance(flatInstance, pos)
                val npcSpy = spyk(npc) {
                    justRun { onEnterArea(any()) }
                }
                npcSpy.update(0)

                verify(exactly = 1) { npcSpy.onEnterArea(player) }

                every { area.updateEntitiesInArea() } returns Pair(emptySet(), emptySet())

                npcSpy.update(1)

                verify(exactly = 1) { npcSpy.onEnterArea(player) }
                verify(exactly = 0) { npcSpy.onLeaveArea(any()) }
            }

        }

        @Nested
        inner class OnLeaveArea {

            @Test
            fun `should trigger leave area if player is not in area anymore`(env: Env) {
                val pos = Pos(0.0, 0.0, 0.0)
                val flatInstance = env.createFlatInstance()
                val player = mockk<Player>()
                val area = mockk<IAreaLocatable<Player>> {
                    justRun { position = any() }
                    justRun { instance = any() }
                    every { updateEntitiesInArea() } returns Pair(emptySet(), setOf(player))
                }

                val npc = createEntity(area)
                npc.setInstance(flatInstance, pos)
                val npcSpy = spyk(npc) {
                    justRun { onLeaveArea(any()) }
                }
                npcSpy.update(0)

                verify(exactly = 1) { npcSpy.onLeaveArea(player) }
            }

            @Test
            fun `should not leave enter area if the player is always out of the area`(env: Env) {
                val pos = Pos(0.0, 0.0, 0.0)
                val flatInstance = env.createFlatInstance()
                val player = mockk<Player>()
                val area = mockk<IAreaLocatable<Player>> {
                    justRun { position = any() }
                    justRun { instance = any() }
                    every { updateEntitiesInArea() } returns Pair(emptySet(), setOf(player))
                }
                val npc = createEntity(area)
                npc.setInstance(flatInstance, pos)
                val npcSpy = spyk(npc) {
                    justRun { onLeaveArea(any()) }
                }
                npcSpy.update(0)

                verify(exactly = 1) { npcSpy.onLeaveArea(player) }

                every { area.updateEntitiesInArea() } returns Pair(emptySet(), emptySet())

                npcSpy.update(1)

                verify(exactly = 1) { npcSpy.onLeaveArea(player) }
                verify(exactly = 0) { npcSpy.onEnterArea(any()) }
            }

        }

        @Test
        fun `should update the area entities`(env: Env) {
            val pos = Pos(0.0, 0.0, 0.0)
            val flatInstance = env.createFlatInstance()
            val area = mockk<IAreaLocatable<Player>> {
                justRun { position = any() }
                justRun { instance = any() }
                every { updateEntitiesInArea() } returns Pair(emptySet(), emptySet())
            }
            val npc = createEntity(area)
            npc.setInstance(flatInstance, pos)
            npc.update(0)

            verify(exactly = 1) { area.instance = flatInstance }
            verify(exactly = 1) { area.position = pos }
            verify(exactly = 1) { area.updateEntitiesInArea() }
        }

        @Test
        fun `should trigger enter and leave area if players is in and out of area`(env: Env) {
            val pos = Pos(0.0, 0.0, 0.0)
            val flatInstance = env.createFlatInstance()
            val player = mockk<Player>(randomString())
            val player2 = mockk<Player>(randomString())
            val area = mockk<IAreaLocatable<Player>> {
                justRun { position = any() }
                justRun { instance = any() }
                every { updateEntitiesInArea() } returns Pair(setOf(player), setOf(player2))
            }
            val npc = createEntity(area)
            npc.setInstance(flatInstance, pos)
            val npcSpy = spyk(npc) {
                justRun { onEnterArea(any()) }
                justRun { onLeaveArea(any()) }
            }
            npcSpy.update(0)

            verify(exactly = 1) { npcSpy.onEnterArea(player) }
            verify(exactly = 1) { npcSpy.onLeaveArea(player2) }

            every { area.updateEntitiesInArea() } returns Pair(setOf(player2), setOf(player))

            npcSpy.update(1)

            verify(exactly = 1) { npcSpy.onEnterArea(player) }
            verify(exactly = 1) { npcSpy.onEnterArea(player2) }
            verify(exactly = 1) { npcSpy.onLeaveArea(player) }
            verify(exactly = 1) { npcSpy.onLeaveArea(player2) }

            every { area.updateEntitiesInArea() } returns Pair(emptySet(), emptySet())

            npcSpy.update(0)

            verify(exactly = 1) { npcSpy.onEnterArea(player) }
            verify(exactly = 1) { npcSpy.onEnterArea(player2) }
            verify(exactly = 1) { npcSpy.onLeaveArea(player) }
            verify(exactly = 1) { npcSpy.onLeaveArea(player2) }
        }
    }

    protected abstract fun createEntity(area: IAreaLocatable<Player>?): NPCEntity
}