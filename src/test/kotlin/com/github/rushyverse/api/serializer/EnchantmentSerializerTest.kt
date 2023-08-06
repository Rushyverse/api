package com.github.rushyverse.api.serializer

import be.seeseemelk.mockbukkit.MockBukkit
import com.github.rushyverse.api.utils.randomString
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldBe
import io.papermc.paper.enchantments.EnchantmentRarity
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class EnchantmentSerializerTest {

    class EnchantmentMock(private val enchantmentName: String, namespace: NamespacedKey) : Enchantment(namespace) {
        override fun translationKey(): String = error("Not implemented")
        @Deprecated("Deprecated in Java")
        override fun getName(): String = enchantmentName
        override fun getMaxLevel(): Int = error("Not implemented")
        override fun getStartLevel(): Int = error("Not implemented")
        override fun getItemTarget(): EnchantmentTarget = error("Not implemented")
        override fun isTreasure(): Boolean = error("Not implemented")
        override fun isCursed(): Boolean = error("Not implemented")
        override fun conflictsWith(other: Enchantment): Boolean = error("Not implemented")
        override fun canEnchantItem(item: ItemStack): Boolean = error("Not implemented")
        override fun displayName(level: Int): Component = error("Not implemented")
        override fun isTradeable(): Boolean = error("Not implemented")
        override fun isDiscoverable(): Boolean = error("Not implemented")
        override fun getRarity(): EnchantmentRarity = error("Not implemented")
        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float =
            error("Not implemented")

        override fun getActiveSlots(): MutableSet<EquipmentSlot> = error("Not implemented")
    }

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
            fun assertEnchant(enchant: Enchantment) {
                val namespace = enchant.key.namespace
                val key = enchant.key.key
                Json.encodeToString(EnchantmentSerializer, enchant) shouldEqualJson """
                    "$namespace:$key"
                """.trimIndent()
            }

            Enchantment.values().forEach(::assertEnchant)
        }

        @Test
        fun `should serialize custom enchantment`() {
            val namespace = randomAcceptableNamespace()
            val key = randomAcceptableNamespace()
            val namespacedKey = NamespacedKey(namespace, key)
            val enchantment = EnchantmentMock(key, namespacedKey)
            Enchantment.registerEnchantment(enchantment)

            Json.encodeToString(EnchantmentSerializer, enchantment) shouldEqualJson """
                "$namespace:$key"
            """.trimIndent()
        }
    }

    @Nested
    inner class Deserialize {

        @Test
        fun `should find with only key if minecraft is namespace`() {
            val enchant = Enchantment.values().random()
            val key = enchant.key.key
            val json = """
                "$key"
            """.trimIndent()

            Json.decodeFromString(EnchantmentSerializer, json) shouldBe enchant
        }

        @Test
        fun `should find with namespace and key`() {
            val enchant = Enchantment.values().random()
            val namespace = enchant.key.namespace
            val key = enchant.key.key
            val json = """
                "$namespace:$key"
            """.trimIndent()

            Json.decodeFromString(EnchantmentSerializer, json) shouldBe enchant
        }

        @Test
        fun `should find with uppercase instead of underscore`() {
            val enchant = Enchantment.FIRE_ASPECT
            fun decode(key: String) {
                val json = """
                    "$key"
                """.trimIndent()

                Json.decodeFromString(EnchantmentSerializer, json) shouldBe enchant
            }
            decode("fire_aspect")
            decode("fireAspect")
        }

        @Test
        fun `should find custom enchantment`() {
            val namespace = randomAcceptableNamespace()
            val key = randomAcceptableNamespace()
            val namespacedKey = NamespacedKey(namespace, key)
            val enchantment = EnchantmentMock(key, namespacedKey)
            Enchantment.registerEnchantment(enchantment)

            val json = """
                "$namespace:$key"
            """.trimIndent()
            Json.decodeFromString(EnchantmentSerializer, json) shouldBe enchantment
        }

        @Test
        fun `should throw if not found`() {
            val namespace = randomAcceptableNamespace()
            val key = randomAcceptableNamespace()
            val json = """
                "$namespace:$key"
            """.trimIndent()

            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(EnchantmentSerializer, json)
            }

            exception.message shouldBe "Unable to find enchantment with namespaced key: $namespace:$key. " +
                    "Valid enchantments are: ${Enchantment.values().joinToString(", ") { it.key.toString() }}"
        }
    }

    fun randomAcceptableNamespace() = randomString(('a'..'z') + ('0'..'9') + '_')
}
