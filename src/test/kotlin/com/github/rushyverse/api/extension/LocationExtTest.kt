package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.randomString
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import org.bukkit.Location
import org.bukkit.World
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationExtTest {

    private lateinit var loc: Location

    @BeforeTest
    fun onBefore() {
        loc = Location(mockk(randomString()), 0.0, 1.0, 2.0, 3.0f, 4.0f)
    }

    @Nested
    @DisplayName("Copy properties")
    inner class Copy {

        @Test
        fun `copyFrom will copy properties into the instance`() {
            val dest = Location(mockk("World 2"), -1.0, -1.0, -1.0, -1.0f, -1.0f)

            dest.copyFrom(loc)
            assertEquals(loc.world, dest.world)
            assertEquals(loc.x, dest.x)
            assertEquals(loc.y, dest.y)
            assertEquals(loc.z, dest.z)
            assertEquals(loc.yaw, dest.yaw)
            assertEquals(loc.pitch, dest.pitch)
        }

        @Test
        fun `copy without arg will clone the location`() {
            assertEquals(loc.clone(), loc.copy())
        }

        @Test
        fun `copy create a new instance`() {
            assertTrue { loc !== loc.copy() }
        }

        @Test
        fun `copy with only world property will change only the property`() {
            val world = mockk<World>(randomString())
            assertEquals(Location(world, loc.x, loc.y, loc.z, loc.yaw, loc.pitch), loc.copy(world = world))
        }

        @Test
        fun `copy with only x property will change only the property`() {
            val x = loc.x + 10
            assertEquals(Location(loc.world, x, loc.y, loc.z, loc.yaw, loc.pitch), loc.copy(x = x))
        }

        @Test
        fun `copy with only y property will change only the property`() {
            val y = loc.y + 10
            assertEquals(Location(loc.world, loc.x, y, loc.z, loc.yaw, loc.pitch), loc.copy(y = y))
        }

        @Test
        fun `copy with only z property will change only the property`() {
            val z = loc.z + 10
            assertEquals(Location(loc.world, loc.x, loc.y, z, loc.yaw, loc.pitch), loc.copy(z = z))
        }

        @Test
        fun `copy with only yaw property will change only the property`() {
            val yaw = loc.yaw + 10
            assertEquals(Location(loc.world, loc.x, loc.y, loc.z, yaw, loc.pitch), loc.copy(yaw = yaw))
        }

        @Test
        fun `copy with only pitch property will change only the property`() {
            val pitch = loc.pitch + 10
            assertEquals(Location(loc.world, loc.x, loc.y, loc.z, loc.yaw, pitch), loc.copy(pitch = pitch))
        }

        @Test
        fun `copy with all args will change all properties`() {
            val world = mockk<World>(randomString())
            val x = loc.x + 10
            val y = loc.y + 20
            val z = loc.z + 30
            val yaw = loc.yaw + 40
            val pitch = loc.pitch + 50
            assertEquals(Location(world, x, y, z, yaw, pitch), loc.copy(world, x, y, z, yaw, pitch))
        }
    }

    @Nested
    inner class CenterRelative {

        @Test
        fun `should return the center position between two points at 0 0 0`() {
            loc = Location(loc.world, 0.0, 0.0, 0.0)
            val other = Location(loc.world, 0.0, 0.0, 0.0)
            val expected = Location(loc.world, 0.0, 0.0, 0.0)
            loc.centerRelative(other) shouldBe expected

            other shouldBe Location(loc.world, 0.0, 0.0, 0.0)
            loc shouldBe Location(loc.world, 0.0, 0.0, 0.0)
        }

        @Test
        fun `should return the center position between two points`() {
            val previousLoc = loc.copy()
            val other = Location(loc.world, 10.0, 10.0, 10.0)
            val expected = Location(loc.world, 5.0, 5.5, 6.0)
            loc.centerRelative(other) shouldBe expected

            other shouldBe Location(loc.world, 10.0, 10.0, 10.0)
            loc shouldBe previousLoc
        }
    }
}
