package com.github.rushyverse.api.delegate

import com.github.rushyverse.api.AbstractKoinTest
import io.mockk.every
import io.mockk.mockk
import org.bukkit.World
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DelegateWorldTest : AbstractKoinTest() {

    @Test
    fun `get world if server has it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegateWorld(pluginId, uuid)

        val world = mockk<World>()
        every { server.getWorld(uuid) } returns world

        val obj = object {
            val property: World? by delegate
        }

        assertEquals(world, obj.property)
    }

    @Test
    fun `get world if server doesn't have it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegateWorld(pluginId, uuid)
        every { server.getWorld(uuid) } returns null

        val obj = object {
            val property: World? by delegate
        }

        assertNull(obj.property)
    }
}