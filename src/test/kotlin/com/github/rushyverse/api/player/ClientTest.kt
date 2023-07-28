package com.github.rushyverse.api.player

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.exception.PlayerNotFoundException
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*

class ClientTest : AbstractKoinTest() {

    private lateinit var player: PlayerMock
    private lateinit var serverMock: ServerMock

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        serverMock = MockBukkit.mock().apply {
            player = addPlayer()
        }
    }

    @AfterTest
    override fun onAfter() {
        MockBukkit.unmock()
        super.onAfter()
    }

    @Test
    fun `retrieve player instance not found returns null`() {
        val client = Client(UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
        assertNull(client.player)
    }

    @Test
    fun `retrieve player instance found returns the instance`() {
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        assertEquals(player, client.player)
    }

    @Test
    fun `require player instance not found throws an exception`() {
        val client = Client(UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
        assertThrows<PlayerNotFoundException> {
            client.requirePlayer()
        }
    }

    @Test
    fun `require player instance found returns the instance`() {
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        assertEquals(player, client.requirePlayer())
    }
}
