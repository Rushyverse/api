package io.github.distractic.bukkit.api.delegate

import io.github.distractic.bukkit.api.AbstractKoinTest
import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerWorldTest : AbstractKoinTest() {

    @Test
    fun `get player if server has it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegatePlayer(pluginId, uuid)

        val player = mockk<Player>()
        every { server.getPlayer(uuid) } returns player

        val obj = object {
            val property by delegate
        }

        assertEquals(player, obj.property)
    }

    @Test
    fun `get player if server doesn't have it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegatePlayer(pluginId, uuid)
        every { server.getPlayer(uuid) } returns null

        val obj = object {
            val property by delegate
        }

        assertNull(obj.property)
    }
}