package com.github.rushyverse.api.listener

import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.rushyverse.api.player.exception.ClientAlreadyExistsException
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import com.github.rushyverse.api.utils.randomString
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.*

class PlayerListenerTest : AbstractKoinTest() {

    lateinit var clientManager: ClientManager

    private lateinit var listener: PlayerListener

    private lateinit var scoreboardManagerMock: ScoreboardManager

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        clientManager = ClientManagerImpl()
        loadTestModule {
            single { clientManager }
        }

        scoreboardManagerMock = mockk<ScoreboardManager>()
        loadApiTestModule {
            single { scoreboardManagerMock }
        }
        listener = PlayerListener(plugin)
    }

    @Test
    fun `contains no clients when instance is created`() {
        assertTrue { clientManager.clients.isEmpty() }
    }

    @Nested
    @DisplayName("Player join")
    inner class PlayerJoin {

        private lateinit var event: PlayerJoinEvent
        private lateinit var player: Player

        @BeforeTest
        fun onBefore() {
            player = createPlayerMock()
            event = createEvent(player)
        }

        @Test
        fun `create and save a new client`() = runTest {
            val client = createClient(player)
            every { plugin.createClient(any()) } returns client

            listener.onJoin(event)

            val clients = clientManager.clients
            assertEquals(1, clients.size)
            assertTrue { clientManager.contains(player) }
            assertEquals(client, clients.values.first())

            val otherPlayer = createPlayerMock()
            val otherEvent = createEvent(otherPlayer)

            val otherClient = createClient(otherPlayer)
            every { plugin.createClient(otherPlayer) } returns otherClient

            listener.onJoin(otherEvent)
            assertEquals(2, clients.size)
            assertTrue { clientManager.contains(player) && clientManager.contains(otherPlayer) }
            clients.values.containsAll(listOf(client, otherClient))
        }

        @Test
        fun `try to store a client with the same name but already exists and keep first instance`() = runTest {
            val client = createClient(player)
            every { plugin.createClient(player) } returns client

            listener.onJoin(event)
            assertThrows<ClientAlreadyExistsException> {
                listener.onJoin(event)
            }
        }

        private fun createEvent(player: Player): PlayerJoinEvent {
            return PlayerJoinEvent(player, mockk<Component>())
        }
    }

    @Nested
    @DisplayName("Player leave")
    inner class PlayerLeave {

        @Test
        fun `client linked to the player is removed and cancelled`() = runTest {
            val player = createPlayerMock()
            coJustRun { scoreboardManagerMock.remove(any()) }

            val client = Client(player.uniqueId, CoroutineScope(Dispatchers.Main + SupervisorJob()))
            clientManager.put(player, client)

            listener.onQuit(createEvent(player))

            assertEquals(0, clientManager.clients.size)
            assertFalse { client.isActive }
            coVerify { scoreboardManagerMock.remove(player) }
        }

        private fun createEvent(player: Player): PlayerQuitEvent {
            return PlayerQuitEvent(player, mockk<Component>(), PlayerQuitEvent.QuitReason.DISCONNECTED)
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
