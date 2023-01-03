package com.github.rushyverse.api.position

import com.github.rushyverse.api.utils.randomString
import io.mockk.mockk
import net.minestom.server.entity.Entity
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AbstractAreaTest {

    @Nested
    inner class Instantiation {

        @Test
        fun `should have no entities at the creation`() {
            val area = object : AbstractArea<Entity>() {
                override fun updateEntitiesInArea(): Pair<Collection<Entity>, Collection<Entity>> {
                    error("Not implemented")
                }
            }
            assertTrue { area.entitiesInArea.isEmpty() }
        }
    }

    @Nested
    inner class Update {

        @Test
        fun `should add all entities if list is empty`() {
            val entities = setOf(
                mockk(randomString()),
                mockk(randomString()),
                mockk<Entity>(randomString())
            )
            val area = object : AbstractArea<Entity>() {
                override fun updateEntitiesInArea(): Pair<Collection<Entity>, Collection<Entity>> {
                    return update(entities)
                }
            }

            val (added, removed) = area.updateEntitiesInArea()
            assertEquals(entities, added)
            assertTrue { removed.isEmpty() }
            assertEquals(entities, area.entitiesInArea)
        }

        @Test
        fun `should add and remove all entities if list not in entities list of area`() {
            val entity1 = mockk<Entity>(randomString())
            val entity2 = mockk<Entity>(randomString())
            val entity3 = mockk<Entity>(randomString())

            var entities = setOf(entity1, entity2, entity3)

            val area = object : AbstractArea<Entity>() {
                override fun updateEntitiesInArea(): Pair<Collection<Entity>, Collection<Entity>> {
                    return update(entities)
                }
            }
            area.updateEntitiesInArea()

            area.updateEntitiesInArea()
            entities = setOf(entity1, entity2)
            val (added, removed) = area.updateEntitiesInArea()
            assertTrue { added.isEmpty() }
            assertEquals(setOf(entity3), removed)
            assertEquals(entities, area.entitiesInArea)

            val entity4 = mockk<Entity>(randomString())
            entities = setOf(entity1, entity4)
            val (added2, removed2) = area.updateEntitiesInArea()
            assertEquals(setOf(entity4), added2)
            assertEquals(setOf(entity2), removed2)
            assertEquals(entities, area.entitiesInArea)
        }

        @Test
        fun `should not change entities in area if entities is always in area`() {
            val entity1 = mockk<Entity>(randomString())
            val entity2 = mockk<Entity>(randomString())
            val entity3 = mockk<Entity>(randomString())

            val entities = setOf(entity1, entity2, entity3)

            val area = object : AbstractArea<Entity>() {
                override fun updateEntitiesInArea(): Pair<Collection<Entity>, Collection<Entity>> {
                    return update(entities)
                }
            }
            val (added, removed) = area.updateEntitiesInArea()
            assertEquals(entities, added)
            assertTrue { removed.isEmpty() }
            assertEquals(entities, area.entitiesInArea)

            val (added2, removed2) = area.updateEntitiesInArea()
            assertTrue { added2.isEmpty() }
            assertTrue { removed2.isEmpty() }
            assertEquals(entities, area.entitiesInArea)
        }

    }
}