package com.github.rushyverse.api.world.cylinder

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.utils.randomDouble
import com.github.rushyverse.api.utils.randomFloat
import com.github.rushyverse.api.world.CylinderArea
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

class CylinderAreaSerializerTest {

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
            val height = randomDouble()..randomDouble()
            val area = CylinderArea(loc, radius, height)
            val json = Json.encodeToString(CylinderArea.serializer(), area)
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
                    "radius": $radius,
                    "height": {
                        "start": ${height.start},
                        "end": ${height.endInclusive}
                    }
                }
            """.trimIndent()
        }

        @Test
        fun `should with direction coordinate for location`() {
            val loc = Location(null, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val radius = randomDouble(from = 0.0)
            val height = randomDouble()..randomDouble()
            val area = CylinderArea(loc, radius, height)
            val json = Json.encodeToString(CylinderArea.serializer(), area)
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
                    "radius": $radius,
                    "height": {
                        "start": ${height.start},
                        "end": ${height.endInclusive}
                    }
                }
            """.trimIndent()
        }

        @Test
        fun `should with all fields`() {
            val loc = Location(world, randomDouble(), randomDouble(), randomDouble(), randomFloat(), randomFloat())
            val radius = randomDouble(from = 0.0)
            val height = randomDouble()..randomDouble()
            val area = CylinderArea(loc, radius, height)
            val json = Json.encodeToString(CylinderArea.serializer(), area)
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
                    "radius": $radius,
                    "height": {
                        "start": ${height.start},
                        "end": ${height.endInclusive}
                    }
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
            val height = randomDouble()..randomDouble()
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
                    "radius": $radius,
                    "height": {
                        "start": ${height.start},
                        "end": ${height.endInclusive}
                    }
                }
            """.trimIndent()
            Json.decodeFromString(CylinderArea.serializer(), json) shouldBe CylinderArea(loc, radius, height)
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
                    "radius": -1,
                    "height": {
                        "start": 0.0,
                        "end": 0.0
                    }
                }
            """.trimIndent()

            shouldThrow<IllegalArgumentException> {
                Json.decodeFromString(CylinderArea.serializer(), json)
            }
        }
    }
}
