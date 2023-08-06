package com.github.rushyverse.api.serializer

import be.seeseemelk.mockbukkit.MockBukkit
import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class NamespacedSerializerTest {

    @BeforeTest
    fun onBefore() {
        MockBukkit.mock()
        Enchantment.values().isEmpty() shouldBe false
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Serialize {

        @Test
        fun `should use namespace and key`() {
            val namespace = randomAcceptableNamespace()
            val key = randomAcceptableNamespace()
            Json.encodeToString(NamespacedSerializer, NamespacedKey(namespace, key)) shouldEqualJson """
                "$namespace:$key"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should create with minecraft namespace by default if not defined`() {
            val key = randomAcceptableNamespace()
            val json = """
                "$key"
            """.trimIndent()

            Json.decodeFromString(NamespacedSerializer, json) shouldBe NamespacedKey("minecraft", key)
        }

        @Test
        fun `should create with namespace and key`() {
            val namespace = randomAcceptableNamespace()
            val key = randomAcceptableNamespace()
            val json = """
                "$namespace:$key"
            """.trimIndent()

            Json.decodeFromString(NamespacedSerializer, json) shouldBe NamespacedKey(namespace, key)
        }

        @Test
        fun `should replace uppercase by underscore and lowercase`() {
            fun decode(namespace: String?, key: String, expected: NamespacedKey) {
                val json = """
                    "${if (namespace != null) "$namespace:" else ""}$key"
                """.trimIndent()

                Json.decodeFromString(NamespacedSerializer, json) shouldBe expected
            }
            decode("test", "myKey", NamespacedKey("test", "my_key"))
            decode("myNamespace", "myKey", NamespacedKey("my_namespace", "my_key"))
            decode(null, "myKey", NamespacedKey.minecraft("my_key"))
        }

        @Test
        fun `should replace space by underscore`() {
            fun decode(namespace: String?, key: String, expected: NamespacedKey) {
                val json = """
                    "${if (namespace != null) "$namespace:" else ""}$key"
                """.trimIndent()

                Json.decodeFromString(NamespacedSerializer, json) shouldBe expected
            }
            decode("test", "my key", NamespacedKey("test", "my_key"))
            decode("my namespace", "my key", NamespacedKey("my_namespace", "my_key"))
            decode(null, "my key", NamespacedKey.minecraft("my_key"))
        }
    }

    fun randomAcceptableNamespace() = randomString(('a'..'z') + ('0'..'9') + '.' + '_' + '-')
}
