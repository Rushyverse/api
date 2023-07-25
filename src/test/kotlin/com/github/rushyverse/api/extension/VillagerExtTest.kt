package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.getRandomString
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import org.bukkit.NamespacedKey
import org.bukkit.entity.Villager
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.*

class VillagerExtTest {

    private lateinit var plugin: com.github.rushyverse.api.Plugin

    @BeforeTest
    fun onBefore() {
        plugin = mockk<com.github.rushyverse.api.Plugin>()
        every { plugin.name } returns "test"
    }

    @Nested
    @DisplayName("Keep profession")
    inner class KeepProfession {

        @Test
        fun `returns true when data is present`() {
            val villager = mockk<Villager>(getRandomString())
            val container = mockk<PersistentDataContainer>()

            val slotNamespaced = slot<NamespacedKey>()
            every { container.get(capture(slotNamespaced), PersistentDataType.BYTE) } returns 0.toByte()
            every { villager.persistentDataContainer } returns container
            assertTrue { villager.keepProfession(plugin) }
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
        }

        @Test
        fun `returns false when data is not present`() {
            val villager = mockk<Villager>(getRandomString())
            val container = mockk<PersistentDataContainer>()

            val slotNamespaced = slot<NamespacedKey>()
            every { container.get(capture(slotNamespaced), PersistentDataType.BYTE) } returns null
            every { villager.persistentDataContainer } returns container
            assertFalse { villager.keepProfession(plugin) }
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
        }

    }

    @Nested
    @DisplayName("Set keep profession")
    inner class SetKeepProfession {

        @Test
        fun `when true, set key into the data container`() {
            val villager = mockk<Villager>(getRandomString())
            val container = mockk<PersistentDataContainer>()

            val slotNamespaced = slot<NamespacedKey>()
            justRun { container.set(capture(slotNamespaced), PersistentDataType.BYTE, 0) }
            every { villager.persistentDataContainer } returns container

            villager.keepProfession(plugin, true)
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
        }

        @Test
        fun `when false, remove key into the data container`() {
            val villager = mockk<Villager>(getRandomString())
            val container = mockk<PersistentDataContainer>()

            val slotNamespaced = slot<NamespacedKey>()
            justRun { container.remove(capture(slotNamespaced)) }
            every { villager.persistentDataContainer } returns container

            villager.keepProfession(plugin, false)
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
        }

    }
}
