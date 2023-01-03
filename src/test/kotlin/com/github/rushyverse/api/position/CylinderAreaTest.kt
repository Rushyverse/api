package com.github.rushyverse.api.position

import com.github.rushyverse.api.utils.randomPos
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.mockk
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CylinderAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = CylinderArea<Entity>(mockk(), randomPos(), 0.0, 0.0..0.0)
            assertTrue { area.entitiesInArea.isEmpty() }
        }

        @Test
        fun `should throw an exception if the radius is negative`() {
            assertThrows<IllegalArgumentException> {
                CylinderArea<Entity>(mockk(), randomPos(), -1.0, 0.0..0.0)
            }
        }

        @Test
        fun `should throw an exception if the radius is set`() {
            val area = CylinderArea<Entity>(mockk(), randomPos(), 0.0, 0.0..0.0)
            assertThrows<IllegalArgumentException> {
                area.radius = -1.0
            }
        }

        @Test
        fun `should set the radius without exception if value is zero or positive`() {
            val area = CylinderArea<Entity>(mockk(), randomPos(), 0.0, 0.0..0.0)

            area.radius = 0.0
            assertEquals(0.0, area.radius)

            area.radius = 1.0
            assertEquals(1.0, area.radius)
        }
    }

    @Nested
    inner class UpdateWithYChange {

        @Test
        fun `should use negative y limit`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -5.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -8.1, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -11.0, 0.0)
            }
            val player4 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -4.9, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3, player4)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 1.0, -10.0..-5.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2), area.entitiesInArea)
        }

        @Test
        fun `should use positive y limit`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 5.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 7.3, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 4.3, 0.0)
            }
            val player4 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 10.1, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3, player4)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 0.0, 5.0..10.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2), area.entitiesInArea)
        }

        @Test
        fun `should use negative and positive y limit`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -3.0, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 8.0, 0.0)
            }
            val player4 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 10.1, 0.0)
            }
            val player5 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -5.1, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3, player4, player5)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 0.0, -5.0..10.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2, player3), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2, player3), area.entitiesInArea)
        }

        @Test
        fun `should use zero y limit`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, -0.1, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.1, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 0.0, 0.0..0.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player), area.entitiesInArea)
        }

    }

    @Nested
    inner class UpdateWithRadiusChange {

        @Test
        fun `should use zero for radius`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.1, 0.0, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.1)
            }
            val player4 = mockk<Player>(randomString()) {
                every { position } returns Pos(-0.1, 0.0, 0.0)
            }
            val player5 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, -0.1)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3, player4, player5)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 0.0, 0.0..0.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player), area.entitiesInArea)
        }

        @Test
        fun `should use positive for radius`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(1.0, 0.0, 0.0)
            }
            val player3 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 1.0)
            }
            val player4 = mockk<Player>(randomString()) {
                every { position } returns Pos(-1.0, 0.0, 0.0)
            }
            val player5 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, -1.0)
            }
            val player6 = mockk<Player>(randomString()) {
                every { position } returns Pos(1.0, 0.0, 0.1)
            }
            val player7 = mockk<Player>(randomString()) {
                every { position } returns Pos(-1.0, 0.0, -0.1)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, player3, player4, player5, player6, player7)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 1.0, 0.0..0.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2, player3, player4, player5), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2, player3, player4, player5), area.entitiesInArea)
        }
    }

    @Nested
    inner class UpdateEntitiesInArea {

        @Test
        fun `should have filtered entities on type`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val player2 = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, player2, entity)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Player>(instance, min, 1.0, 0.0..0.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2), area.entitiesInArea)
        }

        @Test
        fun `should have entities in the area`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(1.0, 2.0, 4.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(2.0, 2.0, 4.0)
            }
            val entity2 = mockk<Entity>(randomString()) {
                every { position } returns Pos(5.0, 5.0, 5.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, entity, entity2)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Entity>(instance, min, 5.0, 0.0..2.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)
        }

        @Test
        fun `should remove entities in the area`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(1.0, 2.0, 4.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(2.0, 2.0, 4.0)
            }
            val entity2 = mockk<Entity>(randomString()) {
                every { position } returns Pos(5.0, 5.0, 5.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, entity, entity2)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Entity>(instance, min, 5.0, 0.0..3.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)

            every { player.position } returns Pos(10.0, 0.0, 0.0)
            every { entity2.position } returns Pos(0.0, 1.0, 0.0)

            val (added2, removed2) = area.updateEntitiesInArea()
            assertContentEquals(listOf(entity2), added2)
            assertContentEquals(listOf(player), removed2)
            assertContentEquals(listOf(entity, entity2), area.entitiesInArea)
        }

        @Test
        fun `should not change entities in area if entities is always in area`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val entity2 = mockk<Entity>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, entity, entity2)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = CylinderArea<Entity>(instance, min, 5.0, 0.0..3.0)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, entity, entity2), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity, entity2), area.entitiesInArea)

            val (added2, removed2) = area.updateEntitiesInArea()
            assertContentEquals(emptyList(), added2)
            assertContentEquals(emptyList(), removed2)
            assertContentEquals(listOf(player, entity, entity2), area.entitiesInArea)
        }

    }

}