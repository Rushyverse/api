package com.github.rushyverse.api.serializer

import net.minestom.server.coordinate.Pos
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

class PosSerializerTest {

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

        private fun assertSerialize(x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f) {
            val pos = Pos(x, y, z, yaw, pitch)
            val json = Json.encodeToString(PosSerializer, pos)
            assertEquals("{\"x\":$x,\"y\":$y,\"z\":$z,\"yaw\":$yaw,\"pitch\":$pitch}", json)
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

        private fun assertDeserialize(x: Double, y: Double, z: Double, yaw: Float = 0f, pitch: Float = 0f) {
            val json = "{\"x\":$x,\"y\":$y,\"z\":$z,\"yaw\":$yaw,\"pitch\":$pitch}"
            val pos = Json.decodeFromString(PosSerializer, json)
            assertEquals(Pos(x, y, z, yaw, pitch), pos)
        }
    }
}