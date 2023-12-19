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
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
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
        mockkStatic("com.github.shynixn.mccoroutine.bukkit.MCCoroutineKt")
    }

    @AfterTest
    override fun onAfter() {
        super.onAfter()
        MockBukkit.unmock()
        unmockkAll()
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
            coVerify(exactly = 0) { gui.onClick(any(), any(), any(), any()) }
        }

        @Test
        fun `should do nothing if item is null or air`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
            }

            suspend fun callEvent(item: ItemStack?) {
                val event = callEvent(false, player, item, player.inventory)
                coVerify(exactly = 0) { gui.onClick(any(), any(), any(), any()) }
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
                coEvery { hasInventory(any()) } returns false
            }

            val pluginManager = server.pluginManager

            val item = ItemStack { type = Material.DIRT }
            callEvent(false, player, item, mockk())

            coVerify(exactly = 0) { gui.onClick(any(), any(), any(), any()) }
            verify(exactly = 0) { pluginManager.callSuspendingEvent(any(), plugin) }
        }

        @Test
        fun `should trigger right click event if player select his own inventory`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns false
            }

            val pluginManager = server.pluginManager

            val slot = slot<PlayerInteractEvent>()
            val jobs = List(5) {
                async { delay(1.seconds) }
            }
            every { pluginManager.callSuspendingEvent(capture(slot), plugin) } returns jobs

            val item = ItemStack { type = Material.DIRT }

            callEvent(false, player, item, player.inventory)
            coVerify(exactly = 0) { gui.onClick(any(), any(), any(), any()) }
            verify(exactly = 1) { pluginManager.callSuspendingEvent(any(), plugin) }
            jobs.forEach { it.isCompleted shouldBe true }

            val event = slot.captured
            event.player shouldBe player
            event.action shouldBe Action.RIGHT_CLICK_AIR
            event.item shouldBe item
            event.clickedBlock shouldBe null
        }

        @Test
        fun `should call GUI onClick if client has opened one`() = runTest {
            val (player, client) = registerPlayer()
            val inventory = mockk<Inventory>()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
                coEvery { onClick(client, any(), any(), any()) } returns Unit
                coEvery { hasInventory(inventory) } returns true
            }

            val item = ItemStack { type = Material.DIRT }
            val event = callEvent(false, player, item, inventory)
            coVerify(exactly = 1) { gui.onClick(client, inventory, item, event) }
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

    @Nested
    inner class OnInventoryClose {

        @Test
        fun `should do nothing if client doesn't have a GUI opened`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns false
            }

            callEvent(player)
            coVerify(exactly = 0) { gui.closeClient(client, any()) }
        }

        @Test
        fun `should close the GUI if client has opened one`() = runTest {
            val (player, client) = registerPlayer()
            val gui = registerGUI {
                coEvery { contains(client) } returns true
                coEvery { closeClient(client, any()) } returns true
            }

            val gui2 = registerGUI {
                coEvery { contains(client) } returns false
            }

            callEvent(player)

            coVerify(exactly = 1) { gui.closeClient(client, false) }
            coVerify(exactly = 0) { gui2.closeClient(client, any()) }
        }

        private suspend fun callEvent(player: Player) {
            val event = mockk<InventoryCloseEvent> {
                every { getPlayer() } returns player
            }
            listener.onInventoryClose(event)
        }
    }

    private suspend fun registerPlayer(): Pair<Player, Client> {
        val player = serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }

    private suspend inline fun registerGUI(block: GUI<*>.() -> Unit): GUI<*> {
        val gui = mockk<GUI<*>>(block = block)
        guiManager.add(gui)
        return gui
    }
}
