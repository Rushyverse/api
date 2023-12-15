package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.extension.ItemStack
import com.github.rushyverse.api.extension.event.cancel
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.shynixn.mccoroutine.bukkit.callSuspendingEvent
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested

class GUIListenerTest : AbstractKoinTest() {

    private lateinit var guiManager: GUIManager
    private lateinit var clientManager: ClientManager
    private lateinit var listener: GUIListener
    private lateinit var serverMock: ServerMock

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

        serverMock = MockBukkit.mock()
    }

    @AfterTest
    override fun onAfter() {
        super.onAfter()
        MockBukkit.unmock()
    }

    @Nested
    inner class OnInventoryClick {

        @Test
        fun `should do nothing if event is cancelled`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
            }

            callEvent(true, player, ItemStack { type = Material.DIRT }, player.inventory)
            coVerify(exactly = 0) { gui.onClick(any(), any(), any()) }
        }

        @Test
        fun `should do nothing if item is null or air`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
            }

            suspend fun callEvent(item: ItemStack?) {
                val event = callEvent(false, player, item, player.inventory)
                coVerify(exactly = 0) { gui.onClick(any(), any(), any()) }
                verify(exactly = 0) { event.cancel() }
            }

            callEvent(null)
            callEvent(ItemStack { type = Material.AIR })
        }

        @Test
        fun `should do nothing if client doesn't have a GUI opened`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns false
            }

            val pluginManager = server.pluginManager

            mockkStatic("com.github.shynixn.mccoroutine.bukkit.MCCoroutineKt")
            every { pluginManager.callSuspendingEvent(any(), plugin) } returns emptyList()

            callEvent(false, player, ItemStack { type = Material.DIRT }, player.inventory)
            coVerify(exactly = 0) { gui.onClick(any(), any(), any()) }
        }

        @Test
        fun `should call GUI onClick if client has opened one`() = runTest {
            val (player, client) = registerPlayer()
            val inventory = mockk<Inventory>()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
                coEvery { onClick(client, any(), any()) } returns Unit
                coEvery { hasInventory(inventory) } returns true
            }

            val item = ItemStack { type = Material.DIRT }
            val event = callEvent(false, player, item, inventory)
            coVerify(exactly = 1) { gui.onClick(client, item, event) }
            verify(exactly = 1) { event.cancel() }
        }

        private suspend fun callEvent(
            cancel: Boolean,
            player: Player,
            item: ItemStack?,
            inventory: Inventory?
        ): InventoryClickEvent {
            val event = mockk<InventoryClickEvent> {
                every { isCancelled } returns cancel
                every { whoClicked } returns player
                every { currentItem } returns item
                every { cancel() } returns Unit
                every { clickedInventory } returns inventory
            }

            listener.onInventoryClick(event)
            return event
        }

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
        val player = serverMock.addPlayer()
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
