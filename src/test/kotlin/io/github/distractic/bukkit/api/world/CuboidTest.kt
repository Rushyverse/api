package io.github.distractic.bukkit.api.world

import io.github.distractic.bukkit.api.extension.minMax
import io.github.distractic.bukkit.api.utils.assertEqualsLocation
import io.github.distractic.bukkit.api.world.exception.WorldDifferentException
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.random.Random
import kotlin.test.*

class CuboidTest {

    @Nested
    @DisplayName("Create instance")
    inner class CreateInstance {

        @Nested
        @DisplayName("From 2 locations")
        inner class FromLocations {

            @Test
            fun `with different world`() {
                val world1 = mockk<World>()
                val world2 = mockk<World>()
                val location1 = Location(world1, .0, .0, .0)
                val location2 = Location(world2, .0, .0, .0)
                val exception = assertThrows<WorldDifferentException> {
                    Cuboid(location1, location2)
                }
                assertEquals(world1, exception.world1)
                assertEquals(world2, exception.world2)
            }

            @Test
            fun `with minimal and maximal coordinates already separated`() {
                val world = createMockWorld()
                val randomCoords = createRandomPositionsOrdered()
                val location1 = Location(world, randomCoords[0], randomCoords[1], randomCoords[2])
                val location2 = Location(world, randomCoords[3], randomCoords[4], randomCoords[5])

                val cuboid = Cuboid(location1, location2)
                assertEqualsLocation(location1, cuboid.startLocation)
                assertEqualsLocation(location2, cuboid.endLocation)
            }

            @Test
            fun `minimal and maximal coordinates mixed`() {
                val world = createMockWorld()
                val randomCoords = createRandomPositionsOrdered()
                val location1 = Location(world, randomCoords[0], randomCoords[4], randomCoords[2])
                val location2 = Location(world, randomCoords[3], randomCoords[1], randomCoords[5])

                val cuboid = Cuboid(location1, location2)
                val startLocation = cuboid.startLocation
                val endLocation = cuboid.endLocation

                assertEquals(location1.x, startLocation.x)
                assertEquals(location2.y, startLocation.y)
                assertEquals(location1.z, startLocation.z)

                assertEquals(location2.x, endLocation.x)
                assertEquals(location1.y, endLocation.y)
                assertEquals(location2.z, endLocation.z)
            }
        }
    }

    @Nested
    @DisplayName("Get world")
    inner class GetWorld {

        @Test
        fun `is linked to the location's world instance from constructor`() {
            val world = mockk<World>()
            val location1 = Location(world, .0, .0, .0)
            val location2 = Location(world, .0, .0, .0)
            val cuboid = Cuboid(location1, location2)
            assertEquals(world, cuboid.world)
        }

        @Test
        fun `is null if location's world instance is null`() {
            val location1 = Location(null, .0, .0, .0)
            val location2 = Location(null, .0, .0, .0)
            val cuboid = Cuboid(location1, location2)
            assertNull(cuboid.world)
        }

        @Test
        fun `is not linked to the location's world instance if changed`() {
            val world = mockk<World>()
            val location1 = Location(world, .0, .0, .0)
            val location2 = Location(world, .0, .0, .0)
            val cuboid = Cuboid(location1, location2)
            assertEquals(world, cuboid.world)

            location1.world = null
            assertEquals(world, cuboid.world)

            location2.world = null
            assertEquals(world, cuboid.world)
        }
    }

    @Nested
    @DisplayName("Set world")
    inner class SetWorld {

        @Test
        fun `change the world of start and end location`() {
            val location1 = Location(null, .0, .0, .0)
            val location2 = Location(null, .0, .0, .0)
            val cuboid = Cuboid(location1, location2)
            assertNull(cuboid.world)

            val world = mockk<World>()
            cuboid.world = world
            assertEquals(world, cuboid.world)
            assertEquals(world, cuboid.startLocation.world)
            assertEquals(world, cuboid.endLocation.world)
        }
    }

    @Nested
    @DisplayName("Contains location")
    inner class ContainsLocation {

        private lateinit var world: World
        private lateinit var location1: Location
        private lateinit var location2: Location
        private lateinit var cuboid: Cuboid

        @BeforeTest
        fun onBefore() {
            world = createMockWorld()
            location1 = Location(world, -100.0, -100.0, -100.0)
            location2 = Location(world, 100.0, 100.0, 100.0)
            cuboid = Cuboid(location1, location2)
        }

        @Test
        fun `location is in bound of minimal coordinate`() {
            assertTrue { cuboid.contains(location1) }
        }

        @Test
        fun `location is in bound of maximal coordinate`() {
            assertTrue { cuboid.contains(location2) }
        }

        @Test
        fun `location is in bound but world is different`() {
            val location = Location(mockk(), location1.x, location1.y, location1.z)
            assertFalse { cuboid.contains(location) }
        }

        @Test
        fun `location between bounds`() {
            fun getMiddle(min: Double, max: Double) = min + (max - min)
            assertTrue {
                cuboid.contains(
                    Location(
                        world,
                        getMiddle(location1.x, location2.x),
                        getMiddle(location1.y, location2.y),
                        getMiddle(location1.z, location2.z)
                    )
                )
            }
        }

        @Nested
        @DisplayName("X out of bounds")
        inner class XOut {

            @Test
            fun `inferior than minimal`() {
                assertFalse {
                    cuboid.contains(
                        location1.add(-1.0, 0.0, 0.0)
                    )
                }
            }

            @Test
            fun `superior than maximal`() {
                assertFalse {
                    cuboid.contains(
                        location2.add(1.0, 0.0, 0.0)
                    )
                }
            }
        }

        @Nested
        @DisplayName("Y out of bounds")
        inner class YOut {

            @Test
            fun `location has y is inferior than minimal`() {
                assertFalse {
                    cuboid.contains(
                        location1.add(0.0, -1.0, 0.0)
                    )
                }
            }

            @Test
            fun `location has y is superior than maximal`() {
                assertFalse {
                    cuboid.contains(
                        location2.add(0.0, 1.0, 0.0)
                    )
                }
            }
        }

        @Nested
        @DisplayName("Z out of bounds")
        inner class ZOut {

            @Test
            fun `location has z is inferior than minimal`() {
                assertFalse {
                    cuboid.contains(
                        location1.add(0.0, 0.0, -1.0)
                    )
                }
            }

            @Test
            fun `location has z is superior than maximal`() {
                assertFalse {
                    cuboid.contains(
                        location2.add(0.0, 0.0, 1.0)
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("Sequence generate")
    inner class Sequence {

        @Nested
        @DisplayName("Location")
        inner class LocationSequence {

            @Test
            fun `give only one location if min and max coordinate are equals`() {
                val world = createMockWorld()

                val blockPosition = Random.nextInt(10000)
                val position = blockPosition.toDouble()
                val location1 = Location(world, position, position, position)
                val location2 = Location(world, position, position, position)
                val cuboid = Cuboid(location1, location2)

                val locations = cuboid.locationSequence().toList()
                assertEquals(1, locations.size)

                val location = locations.first()
                assertEquals(location.world, world)
                assertEquals(location.blockX, blockPosition)
                assertEquals(location.blockY, blockPosition)
                assertEquals(location.blockZ, blockPosition)
            }

            @Test
            fun `give all locations if min and max coordinate are not equals`() {
                val world = createMockWorld()

                val bound = 10.0

                val location1 =
                    Location(world, Random.nextDouble(bound), Random.nextDouble(bound), Random.nextDouble(bound))
                val location2 =
                    Location(world, Random.nextDouble(bound), Random.nextDouble(bound), Random.nextDouble(bound))

                val cuboid = Cuboid(location1, location2)

                val mocks = mutableListOf<Location>()
                for (x in cuboid.startLocation.blockX..cuboid.endLocation.blockX) {
                    for (y in cuboid.startLocation.blockY..cuboid.endLocation.blockY) {
                        for (z in cuboid.startLocation.blockZ..cuboid.endLocation.blockZ) {
                            mocks += Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                        }
                    }
                }

                val locations = cuboid.locationSequence().toList()
                assertEquals(mocks, locations)
            }
        }

        @Nested
        @DisplayName("Block")
        inner class BlockSequence {

            @Test
            fun `give only one block if min and max coordinate are equals`() {
                val world = createMockWorld()
                val blockMock = mockk<Block>()

                val blockPosition = Random.nextInt(10000)
                val position = blockPosition.toDouble()

                every {
                    world.getBlockAt(
                        Location(
                            world,
                            position,
                            position,
                            position
                        )
                    )
                } returns blockMock

                val location1 = Location(world, position, position, position)
                val location2 = Location(world, position, position, position)

                val cuboid = Cuboid(location1, location2)

                val blocks = cuboid.blockSequence().toList()
                assertEquals(1, blocks.size)

                val block = blocks.first()
                assertEquals(blockMock, block)
            }

            @Test
            fun `give all locations if min and max coordinate are not equals`() {
                val world = createMockWorld()

                val bound = 10.0

                val location1 =
                    Location(world, Random.nextDouble(bound), Random.nextDouble(bound), Random.nextDouble(bound))
                val location2 =
                    Location(world, Random.nextDouble(bound), Random.nextDouble(bound), Random.nextDouble(bound))

                val cuboid = Cuboid(location1, location2)

                val mocks = mutableListOf<Block>()
                for (x in cuboid.startLocation.blockX..cuboid.endLocation.blockX) {
                    for (y in cuboid.startLocation.blockY..cuboid.endLocation.blockY) {
                        for (z in cuboid.startLocation.blockZ..cuboid.endLocation.blockZ) {
                            val blockMock = mockk<Block>()
                            val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
                            every { world.getBlockAt(location) } returns blockMock
                            mocks += blockMock
                        }
                    }
                }

                val blocks = cuboid.blockSequence().toList()
                assertEquals(mocks, blocks)
            }
        }
    }

    private fun createMockWorld(): World {
        val world = mockk<World>()
        every { world.uid } returns UUID.randomUUID()
        return world
    }

    private fun createRandomPositionsOrdered(): DoubleArray {
        val bound = 1000.0
        val (minX, maxX) = minMax(
            Random.nextDouble(-bound, bound),
            Random.nextDouble(-bound, bound)
        )
        val (minY, maxY) = minMax(
            Random.nextDouble(-bound, bound),
            Random.nextDouble(-bound, bound)
        )
        val (minZ, maxZ) = minMax(
            Random.nextDouble(-bound, bound),
            Random.nextDouble(-bound, bound)
        )
        return doubleArrayOf(minX, minY, minZ, maxX, maxY, maxZ)
    }
}
