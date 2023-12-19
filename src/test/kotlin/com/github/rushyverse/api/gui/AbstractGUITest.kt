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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

abstract class AbstractGUITest : AbstractKoinTest() {

    protected lateinit var guiManager: GUIManager
    protected lateinit var clientManager: ClientManager
    protected lateinit var serverMock: ServerMock

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

    abstract inner class Register {

        @Test
        fun `should register if not already registered`() = runTest {
            val gui = createNonFillGUI()
            gui.register() shouldBe true
            guiManager.guis shouldContainAll listOf(gui)
        }

        @Test
        fun `should not register if already registered`() = runTest {
            val gui = createNonFillGUI()
            gui.register() shouldBe true
            gui.register() shouldBe false
            guiManager.guis shouldContainAll listOf(gui)
        }

        @Test
        fun `should throw exception if GUI is closed`() = runTest {
            val gui = createNonFillGUI()
            gui.close()
            shouldThrow<GUIClosedException> { gui.register() }
        }
    }

    abstract inner class Viewers {

        @Test
        fun `should return empty list if no client is viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            gui.viewers().toList() shouldBe emptyList()
        }

        @Test
        fun `should return the list of clients viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val playerClients = List(5) { registerPlayer() }

            playerClients.forEach { (_, client) ->
                gui.open(client) shouldBe true
            }

            gui.viewers().toList() shouldContainExactlyInAnyOrder playerClients.map { it.first }
        }

    }

    abstract inner class Contains {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (_, client) = registerPlayer()
            gui.contains(client) shouldBe false
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (_, client) = registerPlayer()
            gui.open(client) shouldBe true
            gui.contains(client) shouldBe true
        }

    }

    abstract inner class Open {

        @Test
        fun `should throw exception if GUI is closed`() = runTest {
            val gui = createNonFillGUI()
            gui.close()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type
            shouldThrow<GUIClosedException> { gui.open(client) }
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should do nothing if the client has the same GUI opened`() = runTest {
            val type = InventoryType.HOPPER
            val gui = createNonFillGUI(inventoryType = type)
            gui.register()
            val (player, client) = registerPlayer()

            gui.open(client) shouldBe true
            player.assertInventoryView(type)

            gui.open(client) shouldBe false
            player.assertInventoryView(type)
        }

        @Test
        fun `should do nothing if the player is dead`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.health = 0.0
            gui.open(client) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should fill the inventory in the same thread if no suspend operation`() {

            val items: Array<ItemStack> = arrayOf(
                ItemStack { type = Material.DIAMOND_ORE },
                ItemStack { type = Material.STICK },
            )

            runBlocking {
                val currentThread = Thread.currentThread()

                val type = InventoryType.ENDER_CHEST
                val gui = createFillGUI(items, delay = null, inventoryType = type)
                gui.register()
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(type)

                val inventory = player.openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe false

                val content = inventory.contents
                items.forEachIndexed { index, item ->
                    content[index] shouldBe item
                }

                for (i in items.size until content.size) {
                    content[i] shouldBe null
                }

                getFillThreadBeforeSuspend(gui) shouldBe currentThread
                getFillThreadAfterSuspend(gui) shouldBe currentThread
            }
        }

        @Test
        fun `should fill the inventory in the other thread after suspend operation`() {

            val items: Array<ItemStack> = arrayOf(
                ItemStack { type = Material.DIAMOND_AXE },
                ItemStack { type = Material.ACACIA_LEAVES },
            )

            runBlocking {
                val currentThread = Thread.currentThread()

                val type = InventoryType.ENDER_CHEST
                val delay = 100.milliseconds
                val gui = createFillGUI(items = items, delay = delay, inventoryType = type)
                gui.register()
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(type)

                val inventory = player.openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                val content = inventory.contents
                content.forEach { it shouldBe null }

                delay(delay * 2)
                gui.isInventoryLoading(inventory) shouldBe false

                items.forEachIndexed { index, item ->
                    content[index] shouldBe item
                }

                for (i in items.size until content.size) {
                    content[i] shouldBe null
                }

                getFillThreadBeforeSuspend(gui) shouldBe currentThread
                getFillThreadAfterSuspend(gui) shouldNotBe currentThread
            }
        }

    }

    abstract inner class Close {

        @Test
        fun `should close all inventories and remove all viewers`() = runTest {
            val type = InventoryType.BREWING
            val gui = createNonFillGUI(type)
            gui.register()

            val playerClients = List(5) { registerPlayer() }
            val initialInventoryViewType = playerClients.first().first.openInventory.type

            playerClients.forEach { (player, client) ->
                player.assertInventoryView(initialInventoryViewType)
                gui.open(client) shouldBe true
                player.assertInventoryView(type)
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
            val gui = createNonFillGUI()
            gui.isClosed shouldBe false
            gui.close()
            gui.isClosed shouldBe true
        }

        @Test
        fun `should unregister the GUI`() = runTest {
            val gui = createNonFillGUI()
            gui.register()
            guiManager.guis shouldContainAll listOf(gui)
            gui.close()
            guiManager.guis shouldContainAll listOf()
        }

        @Test
        fun `should not be able to open the GUI after closing it`() = runTest {
            val gui = createNonFillGUI()
            gui.register()
            val (_, client) = registerPlayer()
            gui.close()

            shouldThrow<GUIClosedException> {
                gui.open(client)
            }
        }

        @Test
        fun `should not be able to register the GUI after closing it`() = runTest {
            val gui = createNonFillGUI()
            gui.close()
            shouldThrow<GUIClosedException> {
                gui.register()
            }
        }
    }

    abstract inner class CloseForClient {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.assertInventoryView(initialInventoryViewType)
            gui.close(client, true) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val type = InventoryType.DISPENSER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            gui.open(client) shouldBe true
            player.assertInventoryView(type)
            gui.close(client, true) shouldBe true
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should not close for other clients`() = runTest {
            val type = InventoryType.HOPPER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()
            val initialInventoryViewType = player2.openInventory.type

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(type)
            player2.assertInventoryView(type)

            gui.close(client2, true) shouldBe true
            player.assertInventoryView(type)
            player2.assertInventoryView(initialInventoryViewType)
        }


    }

    abstract fun createNonFillGUI(inventoryType: InventoryType = InventoryType.HOPPER): GUI<*>

    abstract fun createFillGUI(
        items: Array<ItemStack>,
        inventoryType: InventoryType = InventoryType.HOPPER,
        delay: Duration? = null
    ): GUI<*>

    abstract fun getFillThreadBeforeSuspend(gui: GUI<*>): Thread?

    abstract fun getFillThreadAfterSuspend(gui: GUI<*>): Thread?

    protected suspend fun registerPlayer(): Pair<PlayerMock, Client> {
        val player = serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }
}