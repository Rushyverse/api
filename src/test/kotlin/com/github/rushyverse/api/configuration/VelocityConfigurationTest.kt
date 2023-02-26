package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.utils.randomString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class VelocityConfigurationTest {

    @Nested
    inner class Serialize {

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with empty secret`(enabled: Boolean) {
            val configuration = VelocityConfiguration(enabled, "")
            val serialize = Json.encodeToString(VelocityConfiguration.serializer(), configuration)
            assertEquals("{\"enabled\":$enabled,\"secret\":\"\"}", serialize)
        }

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with non empty secret`(enabled: Boolean) {
            val secret = randomString()
            val configuration = VelocityConfiguration(enabled, secret)
            val serialize = Json.encodeToString(VelocityConfiguration.serializer(), configuration)
            assertEquals("{\"enabled\":$enabled,\"secret\":\"$secret\"}", serialize)
        }

    }

    @Nested
    inner class Deserialize {

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with empty secret`(enabled: Boolean) {
            val string = "{\"enabled\":$enabled,\"secret\":\"\"}"
            val deserialize = Json.decodeFromString(VelocityConfiguration.serializer(), string)
            assertEquals(VelocityConfiguration(enabled, ""), deserialize)
        }

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with non empty secret`(enabled: Boolean) {
            val secret = randomString()
            val string = "{\"enabled\":$enabled,\"secret\":\"$secret\"}"
            val deserialize = Json.decodeFromString(VelocityConfiguration.serializer(), string)
            assertEquals(VelocityConfiguration(enabled, secret), deserialize)
        }

    }
}