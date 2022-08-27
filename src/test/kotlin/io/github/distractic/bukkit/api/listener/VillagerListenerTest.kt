package io.github.distractic.bukkit.api.listener

import io.github.distractic.bukkit.api.AbstractKoinTest
import io.github.distractic.bukkit.api.extension.namespacedKeyKeepJob
import io.github.distractic.bukkit.api.utils.getRandomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.bukkit.NamespacedKey
import org.bukkit.entity.Villager
import org.bukkit.event.entity.VillagerCareerChangeEvent
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.*

class VillagerListenerTest : AbstractKoinTest() {

    private lateinit var listener: VillagerListener

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        listener = VillagerListener(plugin)
    }

    @Nested
    @DisplayName("Career change event")
    inner class CareerChange {

        @Test
        fun `cancel event when tag present in entity`() {
            val villager = mockk<Villager>(getRandomString())
            val container = mockk<PersistentDataContainer>()

            val slotNamespaced = slot<NamespacedKey>()
            every { container.get(capture(slotNamespaced), PersistentDataType.BYTE) } returns 0.toByte()
            every { villager.persistentDataContainer } returns container

            val event = VillagerCareerChangeEvent(
                villager,
                Villager.Profession.LEATHERWORKER,
                VillagerCareerChangeEvent.ChangeReason.EMPLOYED
            )
            event.isCancelled = false
            listener.onChangeCareer(event)
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
            assertTrue { event.isCancelled }
        }

        @Test
        fun `not cancel event when tag present in entity`() {
            val villager = mockk<Villager>(getRandomString())

            val container = mockk<PersistentDataContainer>()
            val slotNamespaced = slot<NamespacedKey>()
            every { container.get(capture(slotNamespaced), any<PersistentDataType<*, *>>()) } returns null
            every { villager.persistentDataContainer } returns container

            val event = VillagerCareerChangeEvent(
                villager,
                Villager.Profession.LEATHERWORKER,
                VillagerCareerChangeEvent.ChangeReason.EMPLOYED
            )
            event.isCancelled = true
            listener.onChangeCareer(event)
            assertEquals(namespacedKeyKeepJob(plugin), slotNamespaced.captured)
            assertFalse { event.isCancelled }
        }

    }
}