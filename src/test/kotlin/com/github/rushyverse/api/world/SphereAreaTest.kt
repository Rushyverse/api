package com.github.rushyverse.api.world

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.extension.copy
import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Location
import org.bukkit.entity.Player
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class SphereAreaTest {

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
        fun `should throw an exception if the radius is negative`() {
            shouldThrow<IllegalArgumentException> {
                SphereArea(mockk(), -1.0)
            }
        }

        @Test
        fun `should throw an exception if the radius is set`() {
            val area = SphereArea(mockk(), 0.0)
            assertThrows<IllegalArgumentException> {
                area.radius = -1.0
            }
        }

        @Test
        fun `should set the radius without exception if value is zero or positive`() {
            val area = SphereArea(mockk(), 0.0)

            area.radius = 0.0
            area.radius shouldBe 0.0

            area.radius = 1.0
            area.radius shouldBe 1.0
        }
    }

    @Nested
    inner class UpdateWithRadiusChange {

        @Test
        fun `should use zero for radius`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = SphereArea(min, 0.0)

            min.copy(x = 0.0, y = 0.0, z = 0.0) isIn area shouldBe true

            min.copy(x = 0.1, y = 0.0, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 0.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 0.0, z = 0.1) isIn area shouldBe false
            min.copy(x = 0.1, y = 0.1, z = 0.1) isIn area shouldBe false

            min.copy(x = -0.1, y = 0.0, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = -0.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 0.0, z = -0.1) isIn area shouldBe false
        }

        @Test
        fun `should use positive for radius`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = SphereArea(min, 5.0)
            println(sqrt(5.0 * 5.0 / 3.0))

            min.copy(x = 0.0, y = 0.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 5.0, y = 0.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, y = 5.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, y = 0.0, z = 5.0) isIn area shouldBe true
            min.copy(x = -5.0, y = 0.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, y = -5.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, y = 0.0, z = -5.0) isIn area shouldBe true

            min.copy(x = 5.1, y = 0.0, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 5.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 0.0, z = 5.1) isIn area shouldBe false
            min.copy(x = -5.1, y = 0.0, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = -5.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, y = 0.0, z = -5.1) isIn area shouldBe false

            min.copy(x = 5.0, y = 5.0, z = 5.0) isIn area shouldBe false
            min.copy(x = -5.0, y = -5.0, z = -5.0) isIn area shouldBe false

            val maxDiagonal = floor(sqrt(5.0 * 5.0 / 3.0) * 100) / 100 // The limit is 25 for radius 5.0 with x,y,z = ~2.886
            min.copy(x = maxDiagonal, y = maxDiagonal, z = maxDiagonal) isIn area shouldBe true
            min.copy(x = -maxDiagonal, y = -maxDiagonal, z = -maxDiagonal) isIn area shouldBe true
            min.copy(x = maxDiagonal + 0.1, y = maxDiagonal + 0.1, z = maxDiagonal + 0.1) isIn area shouldBe false
        }
    }
}
