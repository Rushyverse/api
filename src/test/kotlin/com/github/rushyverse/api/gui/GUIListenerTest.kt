package com.github.rushyverse.api.gui

import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.rushyverse.api.utils.randomString
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.junit.jupiter.api.Nested

class GUIListenerTest : AbstractKoinTest() {

    private lateinit var guiManager: GUIManager
    private lateinit var clientManager: ClientManager
    private lateinit var listener: GUIListener

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        guiManager = GUIManager()
        clientManager = ClientManagerImpl()
        listener = GUIListener(plugin)

        loadApiTestModule {
            single { guiManager }
            single { clientManager }
        }
    }

    @Nested
    inner class OnInventoryClick {


    }

    abstract inner class CloseGUIDuringEvent {

        @Test
        fun `should do nothing if client doesn't have a GUI opened`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns false
            }

            val event = PlayerQuitEvent(player, Component.empty(), PlayerQuitEvent.QuitReason.DISCONNECTED)
            listener.onPlayerQuit(event)

            coVerify(exactly = 0) { gui.close(client, any()) }
        }

        @Test
        fun `should close the GUI if client has opened one`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
                coEvery { close(client, any()) } returns true
            }

            val gui2 = registerGUI {
                coEvery { contains(client) } returns false
            }

            callEvent(player)

            coVerify(exactly = 1) { gui.close(client, false) }
            coVerify(exactly = 0) { gui2.close(client, any()) }
        }

        protected abstract suspend fun callEvent(player: Player)

    }

    @Nested
    inner class OnInventoryClose : CloseGUIDuringEvent() {

        override suspend fun callEvent(player: Player) {
            val event = mockk<InventoryCloseEvent> {
                every { getPlayer() } returns player
            }
            listener.onInventoryClose(event)
        }
    }

    @Nested
    inner class OnPlayerQuit : CloseGUIDuringEvent() {

        override suspend fun callEvent(player: Player) {
            val event = mockk<PlayerQuitEvent> {
                every { getPlayer() } returns player
            }
            listener.onPlayerQuit(event)
        }
    }

    private suspend fun registerPlayer(): Pair<Player, Client> {
        val player = mockk<Player> {
            every { name } returns randomString()
            every { uniqueId } returns UUID.randomUUID()
        }
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }

    private inline fun registerGUI(block: GUI.() -> Unit): GUI {
        val gui = mockk<GUI>(block = block)
        guiManager.add(gui)
        return gui
    }
}
