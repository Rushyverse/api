package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested

class PersonalGUITest : AbstractKoinTest() {

    private lateinit var guiManager: GUIManager
    private lateinit var clientManager: ClientManager
    private lateinit var serverMock: ServerMock

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        guiManager = GUIManager()
        clientManager = ClientManagerImpl()

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
    inner class Open {

    }

    @Nested
    inner class Viewers {

        @Test
        fun `should return empty list if no client is viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            gui.viewers() shouldBe emptyList()
        }

        @Test
        fun `should return the list of clients viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val playerClients = List(5) { registerPlayer() }

            playerClients.forEach { (_, client) ->
                gui.open(client) shouldBe true
            }

            gui.viewers() shouldContainExactlyInAnyOrder playerClients.map { it.first }
        }

    }

    @Nested
    inner class Contains {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val (_, client) = registerPlayer()
            gui.contains(client) shouldBe false
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val (_, client) = registerPlayer()
            gui.open(client) shouldBe true
            gui.contains(client) shouldBe true
        }

    }

    @Nested
    inner class CloseForClient {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest(timeout = 1.minutes) {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.assertInventoryView(initialInventoryViewType)
            gui.close(client, true) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should close the inventory if the client is viewing the GUI`() = runTest(timeout = 1.minutes) {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            gui.open(client) shouldBe true
            player.assertInventoryView(gui.type)
            gui.close(client, true) shouldBe true
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should remove client inventory without closing it if closeInventory is false`() =
            runTest(timeout = 1.minutes) {
                val gui = TestGUI(serverMock)
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(gui.type)
                gui.close(client, false) shouldBe true
                player.assertInventoryView(gui.type)
                gui.contains(client) shouldBe false
            }

    }

    @Nested
    inner class Close {

        @Test
        fun `should close all inventories and remove all viewers`() = runTest(timeout = 1.minutes) {
            val gui = TestGUI(serverMock, InventoryType.BREWING)

            val playerClients = List(5) { registerPlayer() }
            val initialInventoryViewType = playerClients.first().first.openInventory.type

            playerClients.forEach { (player, client) ->
                player.assertInventoryView(initialInventoryViewType)
                gui.open(client) shouldBe true
                player.assertInventoryView(gui.type)
                client.gui() shouldBe gui
            }

            gui.close()
            playerClients.forEach { (player, client) ->
                player.assertInventoryView(initialInventoryViewType)
                client.gui() shouldBe null
            }
        }

        @Test
        fun `should set isClosed to true`() = runTest {
            val gui = TestGUI(serverMock)
            gui.isClosed shouldBe false
            gui.close()
            gui.isClosed shouldBe true
        }

        @Test
        fun `should unregister the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            guiManager.guis shouldContainAll listOf(gui)
            gui.close()
            guiManager.guis shouldContainAll listOf()
        }

    }

    private suspend fun registerPlayer(): Pair<PlayerMock, Client> {
        val player = serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }
}

private class TestGUI(val serverMock: ServerMock, val type: InventoryType = InventoryType.HOPPER) : PersonalGUI() {
    override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
        return serverMock.createInventory(owner, type)
    }

    override suspend fun fill(client: Client, inventory: Inventory) {
    }

    override suspend fun onClick(client: Client, clickedItem: ItemStack, event: InventoryClickEvent) {
        error("Should not be called")
    }
}
