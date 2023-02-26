package com.github.rushyverse.api.configuration

import com.github.rushyverse.api.utils.randomString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class BungeeCordConfigurationTest {

    @Nested
    inner class Serialize {

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with empty secrets`(enabled: Boolean) {
            val configuration = BungeeCordConfiguration(enabled, emptySet())
            val serialize = Json.encodeToString(BungeeCordConfiguration.serializer(), configuration)
            assertEquals("{\"enabled\":$enabled,\"secrets\":[]}", serialize)
        }

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with non empty secrets`(enabled: Boolean) {
            val secrets = listOf(randomString(), randomString())
            val configuration = BungeeCordConfiguration(enabled, secrets.toSet())
            val serialize = Json.encodeToString(BungeeCordConfiguration.serializer(), configuration)
            assertEquals("{\"enabled\":$enabled,\"secrets\":[\"${secrets[0]}\",\"${secrets[1]}\"]}", serialize)
        }

    }

    @Nested
    inner class Deserialize {

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with empty secrets`(enabled: Boolean) {
            val string = "{\"enabled\":$enabled,\"secrets\":[]}"
            val deserialize = Json.decodeFromString(BungeeCordConfiguration.serializer(), string)
            assertEquals(BungeeCordConfiguration(enabled, emptySet()), deserialize)
        }

        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        fun `with non empty secrets`(enabled: Boolean) {
            val secrets = listOf(randomString(), randomString())
            val string = "{\"enabled\":$enabled,\"secrets\":[\"${secrets[0]}\",\"${secrets[1]}\"]}"
            val deserialize = Json.decodeFromString(BungeeCordConfiguration.serializer(), string)
            assertEquals(BungeeCordConfiguration(enabled, secrets.toSet()), deserialize)
        }

    }
}