package com.github.rushyverse.api.world.sphere

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.utils.randomDouble
import com.github.rushyverse.api.utils.randomFloat
import com.github.rushyverse.api.world.SphereArea
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.bukkit.Location
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class SphereAreaSerializerTest {

    private lateinit var world: WorldMock

    @BeforeTest
    fun onBefore() {
        world = WorldMock()
        MockBukkit.mock().apply {
            addWorld(world)
        }
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Serialize {

        @Test
        fun `should with only coordinate for location`() {
            val loc = Location(null, randomDouble(), randomDouble(), randomDouble())
            val radius = randomDouble(from = 0.0)
            val sphereArea = SphereArea(loc, radius)
            val json = Json.encodeToString(SphereArea.serializer(), sphereArea)
            json shouldEqualJson """
                {
                    "location": {
                        "x": ${loc.x},
                        "y": ${loc.y},
                        "z": ${loc.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    },
                    "radius": $radius
                }
            """.trimIndent()
        }

        @Test
        fun `should with direction coordinate for location`() {
            val loc = Location(null, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val radius = randomDouble(from = 0.0)
            val sphereArea = SphereArea(loc, radius)
            val json = Json.encodeToString(SphereArea.serializer(), sphereArea)
            json shouldEqualJson """
                {
                    "location": {
                        "x": ${loc.x},
                        "y": ${loc.y},
                        "z": ${loc.z},
                        "yaw": ${loc.yaw},
                        "pitch": ${loc.pitch},
                        "world": null
                    },
                    "radius": $radius
                }
            """.trimIndent()
        }

        @Test
        fun `should with all fields`() {
            val loc = Location(world, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val radius = randomDouble(from = 0.0)
            val sphereArea = SphereArea(loc, radius)
            val json = Json.encodeToString(SphereArea.serializer(), sphereArea)
            json shouldEqualJson """
                {
                    "location": {
                        "x": ${loc.x},
                        "y": ${loc.y},
                        "z": ${loc.z},
                        "yaw": ${loc.yaw},
                        "pitch": ${loc.pitch},
                        "world": "${world.name}"
                    },
                    "radius": $radius
                }
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should with all fields`() {
            val loc = Location(world, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val radius = randomDouble(from = 0.0)
            val json = """
                {
                    "location": {
                        "x": ${loc.x},
                        "y": ${loc.y},
                        "z": ${loc.z},
                        "yaw": ${loc.yaw},
                        "pitch": ${loc.pitch},
                        "world": "${world.name}"
                    },
                    "radius": $radius
                }
            """.trimIndent()

            Json.decodeFromString(SphereArea.serializer(), json) shouldBe SphereArea(loc, radius)
        }

        @Test
        fun `should throw if radius is negative`() {
            val json = """
                {
                    "location": {
                        "x": 0.0,
                        "y": 0.0,
                        "z": 0.0,
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    },
                    "radius": -1
                }
            """.trimIndent()

            shouldThrow<IllegalArgumentException> {
                Json.decodeFromString(SphereArea.serializer(), json)
            }
        }
    }
}
