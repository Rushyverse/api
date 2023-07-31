package com.github.rushyverse.api.world.cube

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import com.github.rushyverse.api.extension.minMaxOf
import com.github.rushyverse.api.utils.randomDouble
import com.github.rushyverse.api.utils.randomFloat
import com.github.rushyverse.api.world.CubeArea
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.bukkit.Location
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class CubeAreaSerializerTest {

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
            val xs = minMaxOf(randomDouble(), randomDouble())
            val ys = minMaxOf(randomDouble(), randomDouble())
            val zs = minMaxOf(randomDouble(), randomDouble())
            val min = Location(null, xs.first, ys.first, zs.first)
            val max = Location(null, xs.second, ys.second, zs.second)
            val area = CubeArea(min, max)

            val json = Json.encodeToString(CubeArea.serializer(), area)
            json shouldEqualJson """
                {
                    "location1": {
                        "x": ${min.x},
                        "y": ${min.y},
                        "z": ${min.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    },
                    "location2": {
                        "x": ${max.x},
                        "y": ${max.y},
                        "z": ${max.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    }
                }
            """.trimIndent()
        }

        @Test
        fun `should with direction coordinate for location`() {
            val xs = minMaxOf(randomDouble(), randomDouble())
            val ys = minMaxOf(randomDouble(), randomDouble())
            val zs = minMaxOf(randomDouble(), randomDouble())
            val yaws = minMaxOf(randomFloat(), randomFloat())
            val pitchs = minMaxOf(randomFloat(), randomFloat())
            val min = Location(null, xs.first, ys.first, zs.first, yaws.first, pitchs.first)
            val max = Location(null, xs.second, ys.second, zs.second, yaws.second, pitchs.second)
            val area = CubeArea(min, max)

            val json = Json.encodeToString(CubeArea.serializer(), area)
            json shouldEqualJson """
                {
                    "location1": {
                        "x": ${min.x},
                        "y": ${min.y},
                        "z": ${min.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    },
                    "location2": {
                        "x": ${max.x},
                        "y": ${max.y},
                        "z": ${max.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": null
                    }
                }
            """.trimIndent()
        }

        @Test
        fun `should with all fields`() {
            val xs = minMaxOf(randomDouble(), randomDouble())
            val ys = minMaxOf(randomDouble(), randomDouble())
            val zs = minMaxOf(randomDouble(), randomDouble())
            val yaws = minMaxOf(randomFloat(), randomFloat())
            val pitchs = minMaxOf(randomFloat(), randomFloat())
            val min = Location(world, xs.first, ys.first, zs.first, yaws.first, pitchs.first)
            val max = Location(world, xs.second, ys.second, zs.second, yaws.second, pitchs.second)
            val area = CubeArea(min, max)

            val json = Json.encodeToString(CubeArea.serializer(), area)
            json shouldEqualJson """
                {
                    "location1": {
                        "x": ${min.x},
                        "y": ${min.y},
                        "z": ${min.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": "${world.name}"
                    },
                    "location2": {
                        "x": ${max.x},
                        "y": ${max.y},
                        "z": ${max.z},
                        "yaw": 0.0,
                        "pitch": 0.0,
                        "world": "${world.name}"
                    }
                }
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should with all fields`() {
            val xs = minMaxOf(randomDouble(), randomDouble())
            val ys = minMaxOf(randomDouble(), randomDouble())
            val zs = minMaxOf(randomDouble(), randomDouble())
            val yaws = minMaxOf(randomFloat(), randomFloat())
            val pitchs = minMaxOf(randomFloat(), randomFloat())

            val json = """
                {
                    "location1": {
                        "x": ${xs.first},
                        "y": ${ys.second},
                        "z": ${zs.second},
                        "yaw": ${yaws.first},
                        "pitch": ${pitchs.second},
                        "world": "${world.name}"
                    },
                    "location2": {
                        "x": ${xs.second},
                        "y": ${ys.first},
                        "z": ${zs.first},
                        "yaw": ${yaws.second},
                        "pitch": ${pitchs.first},
                        "world": "${world.name}"
                    }
                }
            """.trimIndent()
            Json.decodeFromString(CubeArea.serializer(), json) shouldBe CubeArea(
                Location(world, xs.first, ys.first, zs.first, 0f, 0f),
                Location(world, xs.second, ys.second, zs.second, 0f, 0f)
            )
        }
    }
}
