package com.github.rushyverse.api.extension

import com.destroystokyo.paper.profile.PlayerProfile
import com.github.rushyverse.api.utils.randomBoolean
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.junit.jupiter.api.Nested
import kotlin.test.*

class PlayerExtTest {

    @Nested
    inner class Profile {

        @Test
        fun `edit profile use the current profile and redefine it with the same modified instance`() {
            val player = mockk<Player>(randomString())
            val profile = mockk<PlayerProfile>(randomString())
            every { player.playerProfile } returns profile

            val slot = slot<Boolean>()
            every { profile.complete(capture(slot)) } returns randomBoolean()

            val slotProfile = slot<PlayerProfile>()
            justRun { player.playerProfile = capture(slotProfile) }

            val expectedValue = randomBoolean()
            player.editProfile {
                complete(expectedValue)
            }

            assertEquals(profile, slotProfile.captured)
            assertEquals(expectedValue, slot.captured)
        }
    }

    @Nested
    inner class ItemInHand {

        private lateinit var player: Player
        private val inventory get() = player.inventory

        @BeforeTest
        fun onBefore() {
            player = mockk(randomString())
            val inventory = mockk<PlayerInventory>()
            every { player.inventory } returns inventory
        }

        @Test
        fun `compare with equals and item found`() {
            val expectedItem = mockk<ItemStack>(randomString())
            every { inventory.itemInMainHand } returns expectedItem
            every { inventory.itemInOffHand } returns mockk(randomString())

            assertTrue { player.itemInHand(expectedItem) }

            every { inventory.itemInMainHand } returns mockk(randomString())
            every { inventory.itemInOffHand } returns expectedItem

            assertTrue { player.itemInHand(expectedItem) }
        }

        @Test
        fun `compare with equals and item not found`() {
            val expectedItem = mockk<ItemStack>(randomString())
            every { inventory.itemInMainHand } returns mockk(randomString())
            every { inventory.itemInOffHand } returns mockk(randomString())

            assertFalse { player.itemInHand(expectedItem) }
        }

        @Test
        fun `compare with lambda and item found`() {
            val expectedItem = ItemStack(Material.ACACIA_DOOR)
            every { inventory.itemInMainHand } returns expectedItem
            every { inventory.itemInOffHand } returns ItemStack(Material.AIR)

            assertTrue { player.itemInHand { it.type == expectedItem.type } }

            every { inventory.itemInMainHand } returns ItemStack(Material.COOKED_BEEF)
            every { inventory.itemInOffHand } returns expectedItem

            assertTrue { player.itemInHand { it.type == expectedItem.type } }
        }

        @Test
        fun `compare with lambda and item not found`() {
            val expectedItem = ItemStack(Material.ACACIA_DOOR)
            every { inventory.itemInMainHand } returns ItemStack(Material.SWEET_BERRIES)
            every { inventory.itemInOffHand } returns ItemStack(Material.BLUE_WOOL)

            assertFalse { player.itemInHand { expectedItem.isSimilar(it) } }
        }
    }

}
