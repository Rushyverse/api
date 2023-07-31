package com.github.rushyverse.api.world.cube

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.extension.copy
import com.github.rushyverse.api.utils.assertEqualsLocation
import com.github.rushyverse.api.utils.randomFloat
import com.github.rushyverse.api.utils.randomLocation
import com.github.rushyverse.api.utils.randomString
import com.github.rushyverse.api.world.CubeArea
import com.github.rushyverse.api.world.isIn
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.bukkit.Location
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class CubeAreaTest {

    private lateinit var serverMock: ServerMock
    private lateinit var worldMock: WorldMock

    @BeforeTest
    fun onBefore() {
        serverMock = MockBukkit.mock()
        worldMock = serverMock.addSimpleWorld("world")
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Instantiation {

        @Test
        fun `should throw if locations are not in the same world`() {
            assertThrows<IllegalArgumentException> {
                CubeArea(randomLocation(mockk()), randomLocation(mockk()))
            }
        }

        @Test
        fun `should set min and max with null world`() {
            val loc1 = Location(null, 0.0, 0.0, 0.0)
            val loc2 = Location(null, 1.0, 1.0, 1.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1, min)
                assertEqualsLocation(loc2, max)
            }
        }

        @Test
        fun `should set min and max ignoring direction`() {
            val loc1 = Location(worldMock, 0.0, 0.0, 0.0, randomFloat(), randomFloat())
            val loc2 = Location(worldMock, 1.0, 1.0, 1.0, randomFloat(), randomFloat())
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1.copy(yaw = 0f, pitch = 0f), min)
                assertEqualsLocation(loc2.copy(yaw = 0f, pitch = 0f), max)
            }
        }

        @Test
        fun `should set min and max with same order if coordinate is correctly ordered`() {
            val loc1 = Location(worldMock, 0.0, 0.0, 0.0)
            val loc2 = Location(worldMock, 1.0, 1.0, 1.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1, min)
                assertEqualsLocation(loc2, max)
            }
        }

        @Test
        fun `should set min and max with change x order`() {
            val loc1 = Location(worldMock, 4.0, 0.0, 0.0)
            val loc2 = Location(worldMock, -5.0, 1.0, 1.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1.copy(x = loc2.x), min)
                assertEqualsLocation(loc2.copy(x = loc1.x), max)
            }
        }

        @Test
        fun `should set min and max with change y order`() {
            val loc1 = Location(worldMock, 0.0, 3.0, 0.0)
            val loc2 = Location(worldMock, 1.0, -10.0, 1.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1.copy(y = loc2.y), min)
                assertEqualsLocation(loc2.copy(y = loc1.y), max)
            }
        }

        @Test
        fun `should set min and max with change z order`() {
            val loc1 = Location(worldMock, 0.0, 3.0, 17.0)
            val loc2 = Location(worldMock, 1.0, 6.0, 2.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc1.copy(z = loc2.z), min)
                assertEqualsLocation(loc2.copy(z = loc1.z), max)
            }
        }

        @Test
        fun `should set min and max with change all coordinates order`() {
            val loc1 = Location(worldMock, -1.0, 17.0, 30.0)
            val loc2 = Location(worldMock, -9.0, 10.7, 0.0)
            CubeArea(loc1, loc2).apply {
                assertEqualsLocation(loc2, min)
                assertEqualsLocation(loc1, max)
            }
        }

    }

    @Nested
    inner class InAreaWithWorld {

        @Test
        fun `should return true if world is null for both`() {
            val min = Location(null, 0.0, 0.0, 0.0)
            val max = Location(null, 1.0, 1.0, 1.0)
            val area = CubeArea(min, max)

            min isIn area shouldBe true
        }

        @Test
        fun `should return false if world is different`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val max = Location(worldMock, 1.0, 1.0, 1.0)
            val area = CubeArea(min, max)

            min.copy(world = serverMock.addSimpleWorld(randomString())) isIn area shouldBe false
        }

        @Test
        fun `should return true if world is same and in area`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val max = Location(worldMock, 1.0, 1.0, 1.0)
            val area = CubeArea(min, max)

            min isIn area shouldBe true
        }
    }

    @Nested
    inner class InArea {

        @Test
        fun `should detect if location is in area`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val max = Location(worldMock, 10.0, 10.0, 10.0)
            val area = CubeArea(min, max)

            for (x in 0..10) {
                for (y in 0..10) {
                    for (z in 0..10) {
                        val loc = Location(worldMock, x.toDouble(), y.toDouble(), z.toDouble())
                        loc isIn area shouldBe true
                    }
                }
            }

            min.copy(x = 10.1) isIn area shouldBe false
            min.copy(y = 10.1) isIn area shouldBe false
            min.copy(z = 10.1) isIn area shouldBe false
            max.copy(x = -0.1) isIn area shouldBe false
            max.copy(y = -0.1) isIn area shouldBe false
            max.copy(z = -0.1) isIn area shouldBe false
        }

    }
}
