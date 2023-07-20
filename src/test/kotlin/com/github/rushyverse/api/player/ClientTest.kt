package com.github.rushyverse.api.player

import io.github.distractic.bukkit.api.AbstractKoinTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClientTest : AbstractKoinTest() {

    @Test
    fun `retrieve player instance not found returns null`() {
        val client = Client(pluginId, UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
        every { server.getPlayer(any<UUID>()) } returns null
        assertNull(client.player)
    }

    @Test
    fun `retrieve player instance found returns the instance`() {
        val uuid = UUID.randomUUID()
        val client = Client(pluginId, uuid, CoroutineScope(EmptyCoroutineContext))

        val player = mockk<Player>()
        every { server.getPlayer(uuid) } returns player
        assertEquals(player, client.player)
    }

    @Test
    fun `require player instance not found throws an exception`() {
        val client = Client(pluginId, UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
        every { server.getPlayer(any<UUID>()) } returns null
        assertThrows<io.github.distractic.bukkit.api.player.exception.PlayerNotFoundException> {
            client.requirePlayer()
        }
    }

    @Test
    fun `require player instance found returns the instance`() {
        val uuid = UUID.randomUUID()
        val client = Client(pluginId, uuid, CoroutineScope(EmptyCoroutineContext))

        val player = mockk<Player>()
        every { server.getPlayer(uuid) } returns player
        assertEquals(player, client.requirePlayer())
    }
}