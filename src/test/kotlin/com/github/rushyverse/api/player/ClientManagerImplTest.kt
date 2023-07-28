package com.github.rushyverse.api.player

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.koin.CraftContext
import com.github.rushyverse.api.player.exception.ClientNotFoundException
import com.github.rushyverse.api.utils.randomString
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.runTest
import org.bukkit.entity.Player
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.*

class ClientManagerImplTest {

    private lateinit var clientManager: ClientManager

    @BeforeTest
    fun onBefore() {
        CraftContext.startKoin(APIPlugin.ID_API) { }
        clientManager = ClientManagerImpl()
    }

    @AfterTest
    fun onAfter() {
        CraftContext.stopKoin(APIPlugin.ID_API)
    }

    @Test
    fun `has no clients when created`() {
        assertEquals(0, clientManager.clients.size)
    }

    @Nested
    @DisplayName("Put client")
    inner class Put {

        @Test
        fun `save client linked to the name of the player`() = runTest {
            val player = createPlayerMock()
            val client = createClient(player)
            clientManager.put(player, client)

            val clients = clientManager.clients
            assertEquals(1, clients.size)
            assertEquals(client, clients.values.first())
            assertEquals(player.name, clients.keys.first())
        }

        @Test
        fun `save client returns the previous client instance`() = runTest {
            val player = createPlayerMock()
            val client = createClient(player)
            assertNull(clientManager.put(player, client))

            val client2 = createClient(player)
            assertEquals(client, clientManager.put(player, client2))

            assertEquals(client2, clientManager.getClient(player))
        }

        @Test
        fun `save client if there is no instance already linked`() = runTest {
            val player = createPlayerMock()
            val client = createClient(player)
            assertNull(clientManager.put(player, client))

            val client2 = createClient(player)
            assertEquals(client, clientManager.putIfAbsent(player, client2))

            assertEquals(client, clientManager.getClient(player))
        }
    }

    @Nested
    @DisplayName("Remove client")
    inner class Remove {

        @Test
        fun `returns null when client for player is not linked`() = runTest {
            assertNull(clientManager.removeClient(createPlayerMock()))
        }

        @Test
        fun `returns the instance of client linked to the player`() = runTest {
            val player = createPlayerMock()
            val client = createClient(player)
            clientManager.put(player, client)
            assertEquals(client, clientManager.removeClient(player))
        }

    }

    @Nested
    @DisplayName("Contains player")
    inner class Contains {

        @Test
        fun `contains a player`() = runTest {
            val player = createPlayerMock()
            clientManager.put(player, createClient(player))
            assertTrue { clientManager.contains(player) }

            val player2 = createPlayerMock()
            clientManager.put(player2, createClient(player2))
            assertTrue { clientManager.contains(player) }
            assertTrue { clientManager.contains(player2) }
        }
    }

    @Nested
    @DisplayName("Get client")
    inner class Get {

        @Test
        fun `retrieve from player`() = runTest {
            val player = createPlayerMock()
            assertThrows<ClientNotFoundException> {
                clientManager.getClient(player)
            }

            val client = createClient(player)
            clientManager.put(player, client)
            assertEquals(client, clientManager.getClient(player))
        }

        @Test
        fun `retrieve from player or null`() = runTest {
            val player = createPlayerMock()
            assertNull(clientManager.getClientOrNull(player))

            val client = createClient(player)
            clientManager.put(player, client)
            assertEquals(client, clientManager.getClientOrNull(player))
        }

        @Test
        fun `retrieve from name`() = runTest {
            val player = createPlayerMock()
            val name = player.name
            assertThrows<ClientNotFoundException> {
                clientManager.getClient(name)
            }

            val client = createClient(player)
            clientManager.put(player, client)
            assertEquals(client, clientManager.getClient(name))
        }

        @Test
        fun `retrieve from name or null`() = runTest {
            val player = createPlayerMock()
            val name = player.name
            assertNull(clientManager.getClientOrNull(name))

            val client = createClient(player)
            clientManager.put(player, client)
            assertEquals(client, clientManager.getClientOrNull(name))
        }

    }

    private fun createPlayerMock(): Player {
        val name = randomString()
        val player = mockk<Player>(name)
        every { player.name } returns name
        every { player.uniqueId } returns UUID.randomUUID()
        return player
    }

    private fun createClient(player: Player) =
        Client(player.uniqueId, CoroutineScope(Dispatchers.Default + SupervisorJob()))
}
