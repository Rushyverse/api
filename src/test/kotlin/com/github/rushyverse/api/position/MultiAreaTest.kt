package com.github.rushyverse.api.position

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.minestom.server.entity.Entity
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MultiAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = MultiArea<Entity>()
            assertTrue { area.areas.isEmpty() }
            assertTrue { area.entitiesInArea.isEmpty() }
        }

        @Test
        fun `should have the areas passed in the constructor`() {
            val area1 = mockk<IArea<Entity>>()
            val area2 = mockk<IArea<Entity>>()
            val area = MultiArea(mutableSetOf(area1, area2))
            assertEquals(setOf(area1, area2), area.areas)
        }

    }

    @Nested
    inner class AddArea {

        @Test
        fun `should add an area`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>()
            assertTrue { area.addArea(area2) }
            assertEquals(1, area.areas.size)
        }

        @Test
        fun `should not add an area twice`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>()
            assertTrue { area.addArea(area2) }
            assertFalse { area.addArea(area2) }
            assertEquals(1, area.areas.size)
        }

    }

    @Nested
    inner class RemoveArea {

        @Test
        fun `should remove an area`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>()
            assertTrue { area.addArea(area2) }
            assertTrue { area.removeArea(area2) }
            assertEquals(0, area.areas.size)
        }

        @Test
        fun `should not remove an area if it is not in the list`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>()
            assertFalse { area.removeArea(area2) }
            assertEquals(0, area.areas.size)
        }

    }

    @Nested
    inner class RemoveAllAreas {

        @Test
        fun `should do nothing if there are no areas`() {
            val area = MultiArea<Entity>()
            area.removeAllAreas()
            assertEquals(0, area.areas.size)
        }

        @Test
        fun `should remove all areas`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>()
            val area3 = mockk<IArea<Entity>>()
            assertTrue { area.addArea(area2) }
            assertTrue { area.addArea(area3) }
            area.removeAllAreas()
            assertEquals(0, area.areas.size)
        }

    }

    @Nested
    inner class UpdateEntitiesInArea {

        @Test
        fun `should call updateEntitiesInArea on all areas`() {
            val area = MultiArea<Entity>()
            val area2 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(emptyList(), emptyList())
                every { entitiesInArea } returns emptySet()
            }
            val area3 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(emptyList(), emptyList())
                every { entitiesInArea } returns emptySet()
            }

            area.addArea(area2)
            area.addArea(area3)
            area.updateEntitiesInArea()

            verify(exactly = 1) { area2.updateEntitiesInArea() }
            verify(exactly = 1) { area2.entitiesInArea }
            verify(exactly = 1) { area3.updateEntitiesInArea() }
            verify(exactly = 1) { area3.entitiesInArea }
        }

        @Test
        fun `should register only one times an entity if in several areas`() {
            val area = MultiArea<Entity>()
            val entity1 = mockk<Entity>()
            val entity2 = mockk<Entity>()

            val area2 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1, entity2), emptyList())
                every { entitiesInArea } returns setOf(entity1, entity2)
            }
            val area3 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1), emptyList())
                every { entitiesInArea } returns setOf(entity1)
            }

            area.addArea(area2)
            area.addArea(area3)
            val (added, removed) = area.updateEntitiesInArea()

            assertTrue { removed.isEmpty() }

            val expectedRegisteredEntities = setOf(entity1, entity2)
            assertEquals(expectedRegisteredEntities, added)
            assertEquals(expectedRegisteredEntities, area.entitiesInArea)
        }

        @Test
        fun `should remove an entity if it is not in any area`() {
            val area = MultiArea<Entity>()
            val entity1 = mockk<Entity>()
            val entity2 = mockk<Entity>()

            val area2 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1, entity2), emptyList())
                every { entitiesInArea } returns setOf(entity1, entity2)
            }
            val area3 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1), emptyList())
                every { entitiesInArea } returns setOf(entity1)
            }

            area.addArea(area2)
            area.addArea(area3)
            area.updateEntitiesInArea()

            every { area2.updateEntitiesInArea() } returns Pair(emptyList(), listOf(entity1))
            every { area2.entitiesInArea } returns setOf(entity2)
            every { area3.updateEntitiesInArea() } returns Pair(emptyList(), listOf(entity1))
            every { area3.entitiesInArea } returns setOf()

            val (added, removed) = area.updateEntitiesInArea()

            assertEquals(setOf(entity1), removed)
            assertTrue { added.isEmpty() }
            assertEquals(setOf(entity2), area.entitiesInArea)
        }

        @Test
        fun `should not remove an entity if he's at least in an area`() {
            val area = MultiArea<Entity>()
            val entity1 = mockk<Entity>()
            val entity2 = mockk<Entity>()

            val area2 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1, entity2), emptyList())
                every { entitiesInArea } returns setOf(entity1, entity2)
            }
            val area3 = mockk<IArea<Entity>>() {
                every { updateEntitiesInArea() } returns Pair(listOf(entity1), emptyList())
                every { entitiesInArea } returns setOf(entity1)
            }

            area.addArea(area2)
            area.addArea(area3)
            area.updateEntitiesInArea()

            every { area2.updateEntitiesInArea() } returns Pair(emptyList(), listOf(entity1))
            every { area2.entitiesInArea } returns setOf(entity2)
            every { area3.updateEntitiesInArea() } returns Pair(emptyList(), emptyList())
            every { area3.entitiesInArea } returns setOf(entity1)

            val (added, removed) = area.updateEntitiesInArea()

            assertTrue { removed.isEmpty() }
            assertTrue { added.isEmpty() }
            assertEquals(setOf(entity1, entity2), area.entitiesInArea)
        }

    }
}