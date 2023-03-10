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
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CubeAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = CubeArea<Entity>(mockk(), randomPos(), randomPos())
            assertTrue { area.entitiesInArea.isEmpty() }
        }

        @Test
        fun `should have the correct min and max positions`() {
            val min = Pos(0.0, 10.0, -10.0)
            val max = Pos(-20.0, 11.0, -16.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(-20.0, 10.0, -16.0), area.min)
            assertEquals(Pos(0.0, 11.0, -10.0), area.max)
        }

        @Test
        fun `should have the correct min and max positions if min and max are already ordered`() {
            val min = Pos(-1.0, -2.0, -3.0)
            val max = Pos(0.0, 1.0, 2.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(min, area.min)
            assertEquals(max, area.max)
        }

        @Test
        fun `position should be the center of the area`() {
            val min = Pos(0.0, 10.0, -10.0)
            val max = Pos(-20.0, 11.0, -16.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(-10.0, 10.5, -13.0), area.position)
        }

    }

    @Nested
    inner class SetPosition {

        @Test
        fun `should keep the same position if the new value is the same`() {
            val area = CubeArea<Entity>(mockk(), Pos(0.0, 0.0, 0.0), Pos(10.5, 10.5, 10.5))
            val oldMin = area.min
            val oldMax = area.max
            area.position = area.position
            assertEquals(oldMin, area.min)
            assertEquals(oldMax, area.max)
        }

        @Test
        fun `should change the position if the new positive value is different`() {
            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            val newPosition = Pos(20.0, 20.0, 20.0)
            area.position = newPosition
            assertEquals(newPosition, area.position)
            assertEquals(Pos(15.0, 15.0, 15.0), area.min)
            assertEquals(Pos(25.0, 25.0, 25.0), area.max)
        }

        @Test
        fun `should change the position if the new negative value is different`() {
            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            val newPosition = Pos(-20.0, -20.0, -20.0)
            area.position = newPosition
            assertEquals(newPosition, area.position)
            assertEquals(Pos(-25.0, -25.0, -25.0), area.min)
            assertEquals(Pos(-15.0, -15.0, -15.0), area.max)
        }

        @Test
        fun `should change the position if the new mixed value is different`() {
            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            val newPosition = Pos(20.0, -20.0, -20.0)
            area.position = newPosition
            assertEquals(newPosition, area.position)
            assertEquals(Pos(15.0, -25.0, -25.0), area.min)
            assertEquals(Pos(25.0, -15.0, -15.0), area.max)
        }

    }

    @Nested
    inner class GetPosition {

        @Test
        fun `should return the center of the area with positive values`() {
            val min = Pos(10.0, 10.0, 10.0)
            val max = Pos(20.0, 20.0, 20.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(15.0, 15.0, 15.0), area.position)
        }

        @Test
        fun `should return the center of the area with negative values`() {
            val min = Pos(-20.0, -20.0, -20.0)
            val max = Pos(-10.0, -10.0, -10.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(-15.0, -15.0, -15.0), area.position)
        }

        @Test
        fun `should return the center of the area with mixed values`() {
            val min = Pos(-20.0, 10.0, -20.0)
            val max = Pos(-10.0, 20.0, -10.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(-15.0, 15.0, -15.0), area.position)
        }

        @Test
        fun `should return the center of the area with decimal value`() {
            val min = Pos(10.6, 10.8, 10.4)
            val max = Pos(10.0, 10.0, 20.0)
            val area = CubeArea<Entity>(mockk(), min, max)
            assertEquals(Pos(10.3, 10.4, 15.2), area.position)
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
            val area = CubeArea<Player>(instance, min, min)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, player2), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, player2), area.entitiesInArea)
        }

        @Test
        fun `should have entities in the area`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(10.0, 10.0, 10.0)
            }
            val entity2 = mockk<Entity>(randomString()) {
                every { position } returns Pos(11.0, 5.0, 5.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, entity, entity2)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(instance, min, max)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)
        }

        @Test
        fun `should remove entities in the area`() {
            val player = mockk<Player>(randomString()) {
                every { position } returns Pos(0.0, 0.0, 0.0)
            }
            val entity = mockk<Entity>(randomString()) {
                every { position } returns Pos(10.0, 10.0, 10.0)
            }
            val entity2 = mockk<Entity>(randomString()) {
                every { position } returns Pos(11.0, 5.0, 5.0)
            }
            val instance = mockk<Instance>(randomString()) {
                every { entities } returns setOf(player, entity, entity2)
            }

            val min = Pos(0.0, 0.0, 0.0)
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(instance, min, max)
            val (added, removed) = area.updateEntitiesInArea()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)

            every { player.position } returns Pos(11.0, 5.0, 5.0)
            every { entity2.position } returns Pos(0.0, 6.0, 0.0)

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
            val max = Pos(10.0, 10.0, 10.0)
            val area = CubeArea<Entity>(instance, min, max)
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