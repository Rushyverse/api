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

    class EnchantmentMock(private val _name: String, namespace: NamespacedKey) : Enchantment(namespace) {
        override fun translationKey(): String = TODO("Not yet implemented")
        override fun getName(): String = _name
        override fun getMaxLevel(): Int = TODO("Not yet implemented")
        override fun getStartLevel(): Int = TODO("Not yet implemented")
        override fun getItemTarget(): EnchantmentTarget = TODO("Not yet implemented")
        override fun isTreasure(): Boolean = TODO("Not yet implemented")
        override fun isCursed(): Boolean = TODO("Not yet implemented")
        override fun conflictsWith(other: Enchantment): Boolean = TODO("Not yet implemented")
        override fun canEnchantItem(item: ItemStack): Boolean = TODO("Not yet implemented")
        override fun displayName(level: Int): Component = TODO("Not yet implemented")
        override fun isTradeable(): Boolean = TODO("Not yet implemented")
        override fun isDiscoverable(): Boolean = TODO("Not yet implemented")
        override fun getRarity(): EnchantmentRarity = TODO("Not yet implemented")
        override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float =
            TODO("Not yet implemented")
        override fun getActiveSlots(): MutableSet<EquipmentSlot> = TODO("Not yet implemented")
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
            val namespacedKey = NamespacedKey("namespace_test", "test")
            val enchantment = EnchantmentMock("test", namespacedKey)
            Enchantment.registerEnchantment(enchantment)

            Json.encodeToString(EnchantmentSerializer, enchantment) shouldEqualJson """
                "namespace_test:test"
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
            val namespacedKey = NamespacedKey("namespace_test", "test")
            val enchantment = EnchantmentMock("test", namespacedKey)
            Enchantment.registerEnchantment(enchantment)

            val json = """
                "namespace_test:test"
            """.trimIndent()
            Json.decodeFromString(EnchantmentSerializer, json) shouldBe enchantment
        }

        @Test
        fun `should throw if not found`() {
            val namespace = randomString()
            val key = randomString()
            val json = """
                "$namespace:$key"
            """.trimIndent()

            val exception = assertThrows<SerializationException> {
                Json.decodeFromString(EnchantmentSerializer, json)
            }

            exception.message shouldBe "Unable to find enchantment with namespaced key: $namespace:$key. Valid enchantments are: minecraft:impaling, minecraft:thorns, minecraft:piercing, minecraft:fire_protection, minecraft:smite, minecraft:unbreaking, minecraft:swift_sneak, minecraft:feather_falling, minecraft:mending, minecraft:protection, minecraft:respiration, minecraft:projectile_protection, minecraft:knockback, minecraft:fire_aspect, minecraft:luck_of_the_sea, minecraft:lure, minecraft:punch, minecraft:channeling, minecraft:frost_walker, minecraft:sharpness, minecraft:power, minecraft:riptide, minecraft:bane_of_arthropods, minecraft:efficiency, minecraft:fortune, minecraft:looting, minecraft:loyalty, minecraft:silk_touch, minecraft:quick_charge, minecraft:binding_curse, minecraft:aqua_affinity, minecraft:multishot, minecraft:depth_strider, minecraft:vanishing_curse, minecraft:infinity, minecraft:flame, minecraft:blast_protection, minecraft:sweeping"
        }
    }
}
