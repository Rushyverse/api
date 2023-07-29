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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class CylinderAreaTest {

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
                CylinderArea(mockk(), -1.0, 0.0..0.0)
            }
        }

        @Test
        fun `should throw an exception if the radius is set`() {
            val area = CylinderArea(mockk(), 0.0, 0.0..0.0)
            assertThrows<IllegalArgumentException> {
                area.radius = -1.0
            }
        }

        @Test
        fun `should set the radius without exception if value is zero or positive`() {
            val area = CylinderArea(mockk(), 0.0, 0.0..0.0)

            area.radius = 0.0
            area.radius shouldBe 0.0

            area.radius = 1.0
            area.radius shouldBe 1.0
        }
    }

    @Nested
    inner class UpdateWithYChange {

        @Test
        fun `should use negative y limit`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 1.0, -10.0..-5.0)

            min.copy(y = -5.0) isIn area shouldBe true
            min.copy(y = -8.1) isIn area shouldBe true
            min.copy(y = -11.0) isIn area shouldBe false
            min.copy(y = -4.9) isIn area shouldBe false
        }

        @Test
        fun `should use positive y limit`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 0.0, 5.0..10.0)

            min.copy(y = 5.0) isIn area shouldBe true
            min.copy(y = 7.3) isIn area shouldBe true
            min.copy(y = 4.3) isIn area shouldBe false
            min.copy(y = 10.1) isIn area shouldBe false
        }

        @Test
        fun `should use negative and positive y limit`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 0.0, -5.0..10.0)

            min.copy(y = 0.0) isIn area shouldBe true
            min.copy(y = -3.0) isIn area shouldBe true
            min.copy(y = 8.0) isIn area shouldBe true
            min.copy(y = 10.1) isIn area shouldBe false
            min.copy(y = -5.1) isIn area shouldBe false
        }

        @Test
        fun `should use zero y limit`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 0.0, 0.0..0.0)

            min.copy(y = 0.0) isIn area shouldBe true
            min.copy(y = -0.1) isIn area shouldBe false
            min.copy(y = 0.1) isIn area shouldBe false
        }

    }

    @Nested
    inner class UpdateWithRadiusChange {

        @Test
        fun `should use zero for radius`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 0.0, 0.0..0.0)

            min.copy(x = 0.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, z = 0.1) isIn area shouldBe false
            min.copy(x = -0.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, z = -0.1) isIn area shouldBe false
        }

        @Test
        fun `should use positive for radius`() {
            val min = Location(worldMock, 0.0, 0.0, 0.0)
            val area = CylinderArea(min, 1.0, 0.0..0.0)

            min.copy(x = 0.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 1.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, z = 1.0) isIn area shouldBe true
            min.copy(x = -1.0, z = 0.0) isIn area shouldBe true
            min.copy(x = 0.0, z = -1.0) isIn area shouldBe true
            min.copy(x = 1.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, z = 1.1) isIn area shouldBe false
            min.copy(x = -1.1, z = 0.0) isIn area shouldBe false
            min.copy(x = 0.0, z = -1.1) isIn area shouldBe false

            min.copy(x = 1.0, z = 0.1) isIn area shouldBe false
            min.copy(x = -1.0, z = 0.1) isIn area shouldBe false
            min.copy(x = 1.0, z = -0.1) isIn area shouldBe false
            min.copy(x = -1.0, z = -0.1) isIn area shouldBe false

            min.copy(x = 0.1, z = 1.0) isIn area shouldBe false
            min.copy(x = -0.1, z = 1.0) isIn area shouldBe false
            min.copy(x = 0.1, z = -1.0) isIn area shouldBe false
            min.copy(x = -0.1, z = -1.0) isIn area shouldBe false
        }
    }
}
