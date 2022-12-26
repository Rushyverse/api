package io.github.rushyverse.api.extension

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.thread.Acquirable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class AcquirableExtTest {

    @Nested
    inner class IterableToAcquirables {

        @Test
        fun `Empty to AcquirableCollection`() {
            val iterable = emptyList<Player>()
            val acquirableCollection = iterable.toAcquirables()
            assertTrue(acquirableCollection.unwrap().toList().isEmpty())
        }


        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10])
        fun `Not empty to AcquirableCollection`(numberOfEntities: Int) {
            val iterable = List(numberOfEntities) {
                val entity = mockk<Entity>()
                val acquirable = spyk<Acquirable<Entity>>(Acquirable.of(entity)) {
                    every { unwrap() } returns entity
                }
                every { entity.getAcquirable<Entity>() } returns acquirable
                entity
            }.asIterable()
            val acquirableCollection = iterable.toAcquirables()
            assertContentEquals(iterable, acquirableCollection.unwrap().toList())
        }
    }

    @Nested
    inner class ArrayToAcquirables {

        @Test
        fun `Empty to AcquirableCollection`() {
            val array = emptyArray<Player>()
            val acquirableCollection = array.toAcquirables()
            assertTrue(acquirableCollection.unwrap().toList().isEmpty())
        }


        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10])
        fun `Not empty to AcquirableCollection`(numberOfEntities: Int) {
            val array = Array(numberOfEntities) {
                val entity = mockk<Entity>()
                val acquirable = spyk<Acquirable<Entity>>(Acquirable.of(entity)) {
                    every { unwrap() } returns entity
                }
                every { entity.getAcquirable<Entity>() } returns acquirable
                entity
            }
            val acquirableCollection = array.toAcquirables()
            assertContentEquals(array.toList(), acquirableCollection.unwrap().toList())
        }
    }

    @Nested
    inner class SequenceToAcquirables {

        @Test
        fun `Empty to AcquirableCollection`() {
            val array = emptySequence<Player>()
            val acquirableCollection = array.toAcquirables()
            assertTrue(acquirableCollection.unwrap().toList().isEmpty())
        }


        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10])
        fun `Not empty to AcquirableCollection`(numberOfEntities: Int) {
            val sequence = Array(numberOfEntities) {
                val entity = mockk<Entity>()
                val acquirable = spyk<Acquirable<Entity>>(Acquirable.of(entity)) {
                    every { unwrap() } returns entity
                }
                every { entity.getAcquirable<Entity>() } returns acquirable
                entity
            }.asSequence()
            val acquirableCollection = sequence.toAcquirables()
            assertContentEquals(sequence.toList(), acquirableCollection.unwrap().toList())
        }
    }

}