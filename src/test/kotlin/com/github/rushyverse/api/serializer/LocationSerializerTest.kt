package com.github.rushyverse.api.serializer

import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.utils.randomDouble
import com.github.rushyverse.api.utils.randomFloat
import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.Location
import org.bukkit.World
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.BeforeTest
import kotlin.test.Test

class LocationSerializerTest : AbstractKoinTest() {

    private lateinit var world: World

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        val worldName = randomString()
        world = mockk() {
            every { name } returns worldName
        }

        every { server.getWorld(worldName) } returns world
    }

    @Nested
    inner class Serialize {

        @Nested
        inner class OnlyCoordinate {

            @Test
            fun `with positive values`() {
                assertSerialize(14.0, 2.0, 375.0)
            }

            @Test
            fun `with negative values`() {
                assertSerialize(-58518.0, -7.0, -6828126.0)
            }

            @Test
            fun `with zero values`() {
                assertSerialize(0.0, 0.0, 0.0)
            }

            @Test
            fun `with decimal values`() {
                assertSerialize(0.5, 0.7, 0.6)
            }

            @Test
            fun `with decimal values and negative values`() {
                assertSerialize(-0.5, -0.7, -0.6)
            }

            @Test
            fun `with mixed values`() {
                assertSerialize(0.5, -0.7, 0.6)
            }

        }

        @Nested
        inner class WithRotation {

            @Test
            fun `with positive values`() {
                assertSerialize(14.0, 2.0, 375.0, 0.5f, 0.7f)
            }

            @Test
            fun `with negative values`() {
                assertSerialize(-58518.0, -7.0, -6828126.0, -0.5f, -0.7f)
            }

            @Test
            fun `with zero values`() {
                assertSerialize(0.0, 0.0, 0.0, 0.0f, 0.0f)
            }

            @Test
            fun `with decimal values`() {
                assertSerialize(0.5, 0.7, 0.6, 0.5f, 0.7f)
            }

            @Test
            fun `with decimal values and negative values`() {
                assertSerialize(-0.5, -0.7, -0.6, -0.5f, -0.7f)
            }

            @Test
            fun `with mixed values`() {
                assertSerialize(0.5, -0.7, 0.6, 0.1f, -0.2f)
            }

        }

        @Test
        fun `without world`() {
            val loc = Location(null, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val json = Json.encodeToString(LocationSerializer(), loc)
            json shouldEqualJson """
                {
                    "x": ${loc.x},
                    "y": ${loc.y},
                    "z": ${loc.z},
                    "yaw": ${loc.yaw},
                    "pitch": ${loc.pitch},
                    "world": null
                }
            """.trimIndent()
        }

        private fun assertSerialize(x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f) {
            val loc = Location(world, x, y, z, yaw, pitch)
            val json = Json.encodeToString(LocationSerializer(), loc)
            json shouldEqualJson """
                {
                    "x": $x,
                    "y": $y,
                    "z": $z,
                    "yaw": $yaw,
                    "pitch": $pitch,
                    "world": "${world.name}"
                }
            """.trimIndent()
        }

    }

    @Nested
    inner class Deserialize {

        @Nested
        inner class OnlyCoordinate {

            @Test
            fun `with positive values`() {
                assertDeserialize(14.0, 2.0, 375.0)
            }

            @Test
            fun `with negative values`() {
                assertDeserialize(-58518.0, -7.0, -6828126.0)
            }

            @Test
            fun `with zero values`() {
                assertDeserialize(0.0, 0.0, 0.0)
            }

            @Test
            fun `with decimal values`() {
                assertDeserialize(0.5, 0.7, 0.6)
            }

            @Test
            fun `with decimal values and negative values`() {
                assertDeserialize(-0.5, -0.7, -0.6)
            }

            @Test
            fun `with mixed values`() {
                assertDeserialize(0.5, -0.7, 0.6)
            }

        }

        @Nested
        inner class WithRotation {

            @Test
            fun `with positive values`() {
                assertDeserialize(14.0, 2.0, 375.0, 0.5f, 0.7f)
            }

            @Test
            fun `with negative values`() {
                assertDeserialize(-58518.0, -7.0, -6828126.0, -0.5f, -0.7f)
            }

            @Test
            fun `with zero values`() {
                assertDeserialize(0.0, 0.0, 0.0, 0.0f, 0.0f)
            }

            @Test
            fun `with decimal values`() {
                assertDeserialize(0.5, 0.7, 0.6, 0.5f, 0.7f)
            }

            @Test
            fun `with decimal values and negative values`() {
                assertDeserialize(-0.5, -0.7, -0.6, -0.5f, -0.7f)
            }

            @Test
            fun `with mixed values`() {
                assertDeserialize(0.5, -0.7, 0.6, 0.1f, -0.2f)
            }

        }

        @Nested
        inner class MissingField {

            @Test
            fun `with missing x`() {
                val json = """
                    {
                      "y": 0.0,
                      "z": 0.0,
                      "yaw": 0.0,
                      "pitch": 0.0,
                      "world": "${world.name}"
                    }
                """.trimIndent()
                val exception =
                    assertThrows<SerializationException> { Json.decodeFromString(LocationSerializer(), json) }
                exception.message shouldBe "The field x is missing"
            }

            @Test
            fun `with missing y`() {
                val json = """
                    {
                      "x": 0.0,
                      "z": 0.0,
                      "yaw": 0.0,
                      "pitch": 0.0,
                      "world": "${world.name}"
                    }
                """.trimIndent()
                val exception =
                    assertThrows<SerializationException> { Json.decodeFromString(LocationSerializer(), json) }
                exception.message shouldBe "The field y is missing"
            }

            @Test
            fun `with missing z`() {
                val json = """
                    {
                      "x": 0.0,
                      "y": 0.0,
                      "yaw": 0.0,
                      "pitch": 0.0,
                      "world": "${world.name}"
                    }
                """.trimIndent()
                val exception =
                    assertThrows<SerializationException> { Json.decodeFromString(LocationSerializer(), json) }
                exception.message shouldBe "The field z is missing"
            }

            @Test
            fun `with missing yaw`() {
                val json = """
                    {
                      "x": 1.0,
                      "y": 2.0,
                      "z": 3.0,
                      "pitch": 4.0,
                      "world": "${world.name}"
                    }
                """.trimIndent()
                val location = Json.decodeFromString(LocationSerializer(), json)
                location shouldBe Location(world, 1.0, 2.0, 3.0, 0.0f, 4.0f)
            }

            @Test
            fun `with missing pitch`() {
                val json = """
                    {
                      "x": 1.0,
                      "y": 2.0,
                      "z": 3.0,
                      "yaw": 4.0,
                      "world": "${world.name}"
                    }
                """.trimIndent()
                val location = Json.decodeFromString(LocationSerializer(), json)
                location shouldBe Location(world, 1.0, 2.0, 3.0, 4.0f, 0.0f)
            }

            @Test
            fun `with missing world`() {
                val json = """
                    {
                      "x": 1.0,
                      "y": 2.0,
                      "z": 3.0,
                      "yaw": 4.0,
                      "pitch": 5.0
                    }
                """.trimIndent()
                val location = Json.decodeFromString(LocationSerializer(), json)
                location shouldBe Location(null, 1.0, 2.0, 3.0, 4.0f, 5.0f)
            }
        }

        private fun assertDeserialize(x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f) {
            val json = """
                {
                  "x": $x,
                  "y": $y,
                  "z": $z,
                  "yaw": $yaw,
                  "pitch": $pitch
                }
            """.trimIndent()
            val location = Json.decodeFromString(LocationSerializer(), json)
            location shouldBe Location(null, x, y, z, yaw, pitch)
        }
    }
}
