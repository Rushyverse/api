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

class CuboidAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = CuboidArea<Entity>(mockk(), randomPos(), randomPos())
            assertTrue { area.entitiesInArea.isEmpty() }
        }

        @Test
        fun `should have the correct min and max positions`() {
            val min = Pos(0.0, 10.0, -10.0)
            val max = Pos(-20.0, 11.0, -16.0)
            val area = CuboidArea<Entity>(mockk(), min, max)
            assertEquals(Pos(-20.0, 10.0, -16.0), area.min)
            assertEquals(Pos(0.0, 11.0, -10.0), area.max)
        }

        @Test
        fun `should have the correct min and max positions if min and max are already ordered`() {
            val min = Pos(-1.0, -2.0, -3.0)
            val max = Pos(0.0, 1.0, 2.0)
            val area = CuboidArea<Entity>(mockk(), min, max)
            assertEquals(min, area.min)
            assertEquals(max, area.max)
        }

    }

    @Nested
    inner class EntitiesInArea {

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
            val area = CuboidArea<Entity>(instance, min, max)
            val (added, removed) = area.update()

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
            val area = CuboidArea<Entity>(instance, min, max)
            val (added, removed) = area.update()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)

            every { player.position } returns Pos(11.0, 5.0, 5.0)
            every { entity2.position } returns Pos(0.0, 6.0, 0.0)

            val (added2, removed2) = area.update()
            assertContentEquals(listOf(entity2), added2)
            assertContentEquals(listOf(player), removed2)
            assertContentEquals(listOf(entity, entity2), area.entitiesInArea)
        }

    }

}