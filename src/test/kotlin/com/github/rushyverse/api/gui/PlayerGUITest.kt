package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.extension.ItemStack
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PlayerGUITest : AbstractKoinTest() {

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
    inner class Register {

        @Test
        fun `should register if not already registered`() = runTest {
            val gui = TestGUI(serverMock)
            gui.register() shouldBe true
            guiManager.guis shouldContainAll listOf(gui)
        }

        @Test
        fun `should not register if already registered`() = runTest {
            val gui = TestGUI(serverMock)
            gui.register() shouldBe true
            gui.register() shouldBe false
            guiManager.guis shouldContainAll listOf(gui)
        }

        @Test
        fun `should throw exception if GUI is closed`() = runTest {
            val gui = TestGUI(serverMock)
            gui.close()
            shouldThrow<GUIClosedException> { gui.register() }
        }
    }

    @Nested
    inner class Viewers {

        @Test
        fun `should return empty list if no client is viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            gui.viewers().toList() shouldBe emptyList()
        }

        @Test
        fun `should return the list of clients viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val playerClients = List(5) { registerPlayer() }

            playerClients.forEach { (_, client) ->
                gui.open(client) shouldBe true
            }

            gui.viewers().toList() shouldContainExactlyInAnyOrder playerClients.map { it.first }
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
    inner class Open {

        @Test
        fun `should throw exception if GUI is closed`() = runTest {
            val gui = TestGUI(serverMock)
            gui.close()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type
            shouldThrow<GUIClosedException> { gui.open(client) }
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should do nothing if the client has the same GUI opened`() = runTest {
            val gui = TestGUI(serverMock)
            gui.register()
            val (player, client) = registerPlayer()

            gui.open(client) shouldBe true
            player.assertInventoryView(gui.type)

            gui.open(client) shouldBe false
            player.assertInventoryView(gui.type)
        }

        @Test
        fun `should do nothing if the player is dead`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.damage(Double.MAX_VALUE)
            gui.open(client) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should create a new inventory for the client`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(gui.type)
            player2.assertInventoryView(gui.type)

            player.openInventory.topInventory shouldNotBe player2.openInventory.topInventory
        }

        @Test
        fun `should create a new inventory for the same client if previous is closed before`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            gui.open(client) shouldBe true
            val firstInventory = player.openInventory.topInventory

            gui.close(client, true) shouldBe true

            gui.open(client) shouldBe true
            player.openInventory.topInventory shouldNotBe firstInventory

            player.assertInventoryView(gui.type)
        }

        @Test
        fun `should fill the inventory in the same thread if no suspend operation`() {
            runBlocking {
                val currentThread = Thread.currentThread()

                val gui = TestFilledGUI(serverMock)
                gui.register()
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(InventoryType.CHEST)

                val inventory = player.openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe false

                val content = inventory.contents
                TestFilledGUI.EXPECTED_INV.forEachIndexed { index, item ->
                    content[index] shouldBe item
                }

                for (i in TestFilledGUI.EXPECTED_INV.size until content.size) {
                    content[i] shouldBe null
                }

                gui.calledThread shouldBe currentThread
                gui.newThread shouldBe currentThread
            }
        }

        @Test
        fun `should fill the inventory in the other thread after suspend operation`() {
            runBlocking {
                val currentThread = Thread.currentThread()

                val delay = 100.milliseconds
                val gui = TestFilledGUI(serverMock, delay)
                gui.register()
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(InventoryType.CHEST)

                val inventory = player.openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                val content = inventory.contents
                content.forEach { it shouldBe null }

                delay(delay * 2)
                gui.isInventoryLoading(inventory) shouldBe false

                TestFilledGUI.EXPECTED_INV.forEachIndexed { index, item ->
                    content[index] shouldBe item
                }

                for (i in TestFilledGUI.EXPECTED_INV.size until content.size) {
                    content[i] shouldBe null
                }

                gui.calledThread shouldBe currentThread
                gui.newThread shouldNotBe currentThread
            }
        }
    }

    @Nested
    inner class Close {

        @Test
        fun `should close all inventories and remove all viewers`() = runTest {
            val gui = TestGUI(serverMock, InventoryType.BREWING)
            gui.register()

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
            gui.register()
            guiManager.guis shouldContainAll listOf(gui)
            gui.close()
            guiManager.guis shouldContainAll listOf()
        }

        @Test
        fun `should not be able to open the GUI after closing it`() = runTest {
            val gui = TestGUI(serverMock)
            gui.register()
            val (_, client) = registerPlayer()
            gui.close()

            shouldThrow<GUIClosedException> {
                gui.open(client)
            }
        }

        @Test
        fun `should not be able to register the GUI after closing it`() = runTest {
            val gui = TestGUI(serverMock)
            gui.close()
            shouldThrow<GUIClosedException> {
                gui.register()
            }
        }
    }

    @Nested
    inner class CloseForClient {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.assertInventoryView(initialInventoryViewType)
            gui.close(client, true) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            gui.open(client) shouldBe true
            player.assertInventoryView(gui.type)
            gui.close(client, true) shouldBe true
            player.assertInventoryView(initialInventoryViewType)
        }

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `should stop loading the inventory if the client is viewing the GUI`(closeInventory: Boolean) {
            runBlocking {
                val gui = TestFilledGUI(serverMock, 10.minutes, InventoryType.ENDER_CHEST)
                gui.register()
                val (player, client) = registerPlayer()

                val initialInventoryViewType = player.openInventory.type

                gui.open(client) shouldBe true
                player.assertInventoryView(gui.type)

                val openInventory = player.openInventory
                val inventory = openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                gui.close(client, closeInventory) shouldBe true
                gui.isInventoryLoading(inventory) shouldBe false

                if (closeInventory) {
                    player.assertInventoryView(initialInventoryViewType)
                } else {
                    player.assertInventoryView(gui.type)
                }
            }
        }

        @Test
        fun `should remove client inventory without closing it if closeInventory is false`() =
            runTest {
                val gui = TestGUI(serverMock)
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(gui.type)

                gui.close(client, false) shouldBe true
                player.assertInventoryView(gui.type)

                gui.contains(client) shouldBe false
            }

        @Test
        fun `should not close for other clients`() = runTest {
            val gui = TestGUI(serverMock)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()
            val initialInventoryViewType = player2.openInventory.type

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(gui.type)
            player2.assertInventoryView(gui.type)

            gui.close(client2, true) shouldBe true
            player.assertInventoryView(gui.type)
            player2.assertInventoryView(initialInventoryViewType)
        }
    }

    private suspend fun registerPlayer(): Pair<PlayerMock, Client> {
        val player = serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }
}

private class TestGUI(val serverMock: ServerMock, val type: InventoryType = InventoryType.HOPPER) :
    PlayerGUI() {
    override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
        return serverMock.createInventory(owner, type)
    }

    override fun getItems(key: Client, size: Int): Flow<ItemStackIndex> {
        return emptyFlow()
    }

    override suspend fun onClick(
        client: Client,
        clickedInventory: Inventory,
        clickedItem: ItemStack,
        event: InventoryClickEvent
    ) {
        error("Should not be called")
    }
}

private class TestFilledGUI(
    val serverMock: ServerMock,
    val delay: Duration? = null,
    val type: InventoryType = InventoryType.CHEST
) : PlayerGUI() {

    companion object {
        val EXPECTED_INV = arrayOf(
            ItemStack { type = Material.DIAMOND_ORE },
            ItemStack { type = Material.STICK },
        )
    }

    var calledThread: Thread? = null

    var newThread: Thread? = null

    override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
        return serverMock.createInventory(owner, type)
    }

    override fun getItems(key: Client, size: Int): Flow<ItemStackIndex> {
        calledThread = Thread.currentThread()
        return flow {
            delay?.let { delay(it) }
            emit(0 to EXPECTED_INV[0])
            newThread = Thread.currentThread()
            emit(1 to EXPECTED_INV[1])
        }
    }

    override suspend fun onClick(
        client: Client,
        clickedInventory: Inventory,
        clickedItem: ItemStack,
        event: InventoryClickEvent
    ) {
        error("Should not be called")
    }
}
