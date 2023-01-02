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

class SphereAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = SphereArea<Entity>(mockk(), randomPos(), 0.0)
            assertTrue { area.entitiesInArea.isEmpty() }
        }

        @Test
        fun `should throw an exception if the radius is set`() {
            val area = SphereArea<Entity>(mockk(), randomPos(), 0.0)
            assertThrows<IllegalArgumentException> {
                area.radius = -1.0
            }
        }

        @Test
        fun `should set the radius without exception if value is zero or positive`() {
            val area = SphereArea<Entity>(mockk(), randomPos(), 0.0)

            area.radius = 0.0
            assertEquals(0.0, area.radius)

            area.radius = 1.0
            assertEquals(1.0, area.radius)
        }
    }

    @Nested
    inner class Update {

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
                every { getNearbyEntities(any(), any()) } answers {
                    val pos = arg<Pos>(0)
                    val range = arg<Double>(1)
                    entities.filter { it.position.distance(pos) <= range }
                }
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = SphereArea<Player>(instance, min, 1.0)
            val (added, removed) = area.update()

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
                every { getNearbyEntities(any(), any()) } answers {
                    val pos = arg<Pos>(0)
                    val range = arg<Double>(1)
                    entities.filter { it.position.distance(pos) <= range }
                }
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = SphereArea<Entity>(instance, min, 5.0)
            val (added, removed) = area.update()

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
                every { getNearbyEntities(any(), any()) } answers {
                    val pos = arg<Pos>(0)
                    val range = arg<Double>(1)
                    entities.filter { it.position.distance(pos) <= range }
                }
            }

            val min = Pos(0.0, 0.0, 0.0)
            val area = SphereArea<Entity>(instance, min, 5.0)
            val (added, removed) = area.update()

            assertContentEquals(listOf(player, entity), added)
            assertContentEquals(emptyList(), removed)
            assertContentEquals(listOf(player, entity), area.entitiesInArea)

            every { player.position } returns Pos(10.0, 0.0, 0.0)
            every { entity2.position } returns Pos(0.0, 5.0, 0.0)

            val (added2, removed2) = area.update()
            assertContentEquals(listOf(entity2), added2)
            assertContentEquals(listOf(player), removed2)
            assertContentEquals(listOf(entity, entity2), area.entitiesInArea)
        }

    }

}