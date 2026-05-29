package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.MockPlugin
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.extension.ItemStack
import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.net.InetSocketAddress
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

abstract class AbstractGUITest : AbstractKoinTest() {

    protected lateinit var guiManager: GUIManager
    protected lateinit var clientManager: ClientManager
    protected lateinit var serverMock: ServerMock
    protected lateinit var pluginMock: MockPlugin

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
        pluginMock = MockBukkit.createMockPlugin()
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
        fun `should register GUI if was closed`() = runTest {
            val gui = createNonFillGUI()
            gui.close()
            gui.register() shouldBe true
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
                gui.openClient(client) shouldBe true
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
            gui.openClient(client) shouldBe true
            gui.contains(client) shouldBe true
        }

    }

    abstract inner class OpenClient<T> {

        @Test
        fun `should open if GUI was closed`() = runTest {
            val type = InventoryType.FURNACE
            val gui = createNonFillGUI(type)
            gui.close()
            val (player, client) = registerPlayer()

            gui.openClient(client) shouldBe true
            player.assertInventoryView(type)
        }

        @Test
        fun `should do nothing if the client has the same GUI opened`() = runTest {
            val type = InventoryType.HOPPER
            val gui = createNonFillGUI(inventoryType = type)
            gui.register()
            val (player, client) = registerPlayer()

            gui.openClient(client) shouldBe true
            player.assertInventoryView(type)

            gui.openClient(client) shouldBe false
            player.assertInventoryView(type)
        }

        @Test
        fun `should do nothing if the player is dead`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.health = 0.0
            gui.openClient(client) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should do nothing if the player open inventory is cancelled`() = runTest {
            val gui = createNonFillGUI()

            val basePlayer = serverMock.addPlayer()
            val uuid = UUID.randomUUID()
            val player = spyk(basePlayer) {
                every { uniqueId } returns uuid
                every { address } returns InetSocketAddress(0)
                every { openInventory(any<Inventory>()) } returns null
            }
            val (_, client) = registerPlayer(player)

            gui.openClient(client) shouldBe false
            gui.contains(client) shouldBe false
            gui.viewers().toList() shouldBe emptyList()
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

                gui.openClient(client) shouldBe true
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

                gui.openClient(client) shouldBe true
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

        @Test
        fun `should use loading animation`() {
            val loadingItem = ItemStack { type = Material.BARRIER }
            val item1 = ItemStack { type = Material.APPLE }
            val item2 = ItemStack { type = Material.BEEF }

            val animation = createAnimation(loadingItem)

            val gui = createDelayGUI(
                item1,
                item2,
                delay = 1.seconds,
                inventoryType = InventoryType.CHEST,
                loadingAnimation = animation
            )

            runBlocking {
                val (player, client) = registerPlayer()
                gui.openClient(client) shouldBe true
                val guiInventory = player.openInventory.topInventory

                // Animation should be called so the real items should not be in the inventory
                val firstContents = guiInventory.contents
                firstContents.getOrNull(0) shouldBe loadingItem
                firstContents.getOrNull(1) shouldBe null
                gui.isInventoryLoading(guiInventory) shouldBe true

                delay(100.milliseconds)

                // Until all items are emitted, the inventory should not be filled
                val secondContents = guiInventory.contents
                secondContents.getOrNull(0) shouldBe loadingItem
                secondContents.getOrNull(1) shouldBe null
                gui.isInventoryLoading(guiInventory) shouldBe true

                delay(1.seconds)

                // After all items are emitted, the inventory should be filled
                val thirdContents = guiInventory.contents
                thirdContents.getOrNull(0) shouldBe item1
                thirdContents.getOrNull(1) shouldBe item2
                gui.isInventoryLoading(guiInventory) shouldBe false
            }
        }

        @Test
        fun `should set bit by bit if no loading animation`() {
            val item1 = ItemStack { type = Material.APPLE }
            val item2 = ItemStack { type = Material.BEEF }

            val gui = createDelayGUI(
                item1,
                item2,
                delay = 1.seconds,
                inventoryType = InventoryType.CHEST,
                loadingAnimation = null
            )

            runBlocking {
                val (player, client) = registerPlayer()
                gui.openClient(client) shouldBe true
                delay(100.milliseconds)
                val guiInventory = player.openInventory.topInventory

                // Animation should be called so the real items should not be in the inventory
                val firstContents = guiInventory.contents
                firstContents.getOrNull(0) shouldBe item1
                firstContents.getOrNull(1) shouldBe null
                gui.isInventoryLoading(guiInventory) shouldBe true

                delay(1.seconds)

                // After all items are emitted, the inventory should be filled
                val secondContents = guiInventory.contents
                secondContents.getOrNull(0) shouldBe item1
                secondContents.getOrNull(1) shouldBe item2
                gui.isInventoryLoading(guiInventory) shouldBe false
            }
        }

        private fun createAnimation(loadingItem: ItemStack) =
            InventoryLoadingAnimation<T> { _, inventory ->
                inventory.setItem(0, loadingItem)
            }

        protected abstract fun createDelayGUI(
            item1: ItemStack,
            item2: ItemStack,
            delay: Duration,
            inventoryType: InventoryType,
            loadingAnimation: InventoryLoadingAnimation<T>?
        ): GUI<T>

    }

    abstract inner class UpdateClient {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()
            val initialInventoryViewType = player.openInventory.type
            gui.updateClient(client) shouldBe false

            gui.viewers().toList() shouldBe emptyList()
            gui.contains(client) shouldBe false

            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should return false if the client is viewing the GUI with a loading inventory`() {
            runBlocking {
                val type = InventoryType.DISPENSER
                val delay = 100.milliseconds
                val gui = createFillGUI(emptyArray(), inventoryType = type, delay = delay)
                val (player, client) = registerPlayer()

                gui.openClient(client) shouldBe true

                val guiInventory = player.openInventory.topInventory
                gui.isInventoryLoading(guiInventory) shouldBe true

                // We're waiting the half of the delay to be sure that the inventory is loading
                delay(50.milliseconds)

                gui.updateClient(client) shouldBe false
                player.assertInventoryView(type)
                gui.viewers().toList() shouldContainExactlyInAnyOrder listOf(player)
                gui.contains(client) shouldBe true

                // if we interrupt the loading, the inventory should be loading from 0 to new delay
                // but here, we didn't interrupt the loading, so the inventory should be loaded (50 + 80 = 130 > 100)
                delay(80.milliseconds)

                gui.isInventoryLoading(guiInventory) shouldBe false
            }
        }

        @Test
        fun `should return true if the client is viewing the GUI with a loaded inventory`() {
            runBlocking {
                val type = InventoryType.DISPENSER
                val delay = 100.milliseconds
                val gui = createFillGUI(emptyArray(), inventoryType = type, delay = delay)
                val (player, client) = registerPlayer()

                gui.openClient(client) shouldBe true
                val guiInventory = player.openInventory.topInventory
                gui.isInventoryLoading(guiInventory) shouldBe true

                // We're waiting the half of the delay to be sure that the inventory is loading
                delay(70.milliseconds)

                gui.updateClient(client, true) shouldBe true
                player.assertInventoryView(type)
                gui.viewers().toList() shouldContainExactlyInAnyOrder listOf(player)
                gui.contains(client) shouldBe true

                // if we interrupt the loading, the inventory should be loading from 0 to new delay
                delay(70.milliseconds)
                gui.isInventoryLoading(guiInventory) shouldBe true

                delay(50.milliseconds)
                gui.isInventoryLoading(guiInventory) shouldBe false
            }
        }
    }

    abstract inner class HasInventory {

        @Test
        fun `should return false if the inventory doesn't come from GUI`() = runTest {
            val gui = createNonFillGUI()
            val (_, client) = registerPlayer()
            gui.openClient(client) shouldBe true
            gui.hasInventory(mockk()) shouldBe false
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()
            gui.openClient(client) shouldBe true

            val inventory = player.openInventory.topInventory
            gui.hasInventory(inventory) shouldBe true
        }

    }

    abstract inner class IsInventoryLoading {

        @Test
        fun `should return false if the inventory doesn't come from GUI`() = runTest {
            val gui = createNonFillGUI()
            val (_, client) = registerPlayer()
            gui.openClient(client) shouldBe true
            gui.isInventoryLoading(mockk()) shouldBe false
        }

        @Test
        fun `should return false if the inventory is not loading`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()
            gui.openClient(client) shouldBe true

            val inventory = player.openInventory.topInventory
            gui.isInventoryLoading(inventory) shouldBe false
        }

        @Test
        fun `should return true if the inventory is loading`() {
            runBlocking {
                val delay = 50.milliseconds
                val gui = createFillGUI(emptyArray(), delay = delay)
                val (player, client) = registerPlayer()
                gui.openClient(client) shouldBe true

                val inventory = player.openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                delay(delay * 2)

                gui.isInventoryLoading(inventory) shouldBe false
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

            val inventories = playerClients.map { (player, client) ->
                player.assertInventoryView(initialInventoryViewType)
                gui.openClient(client) shouldBe true
                player.assertInventoryView(type)
                client.gui() shouldBe gui
                player.openInventory.topInventory
            }

            gui.close()

            inventories.forEach { inventory ->
                gui.hasInventory(inventory) shouldBe false
            }

            playerClients.forEach { (player, client) ->
                player.assertInventoryView(initialInventoryViewType)
                client.gui() shouldBe null
            }
        }

        @Test
        fun `should unregister the GUI`() = runTest {
            val gui = createNonFillGUI()
            gui.register()
            guiManager.guis shouldContainAll listOf(gui)
            gui.close()
            guiManager.guis shouldContainAll listOf()
        }
    }

    abstract inner class CloseClient {

        @Test
        fun `should return false if the client is not viewing the GUI`() = runTest {
            val gui = createNonFillGUI()
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            player.assertInventoryView(initialInventoryViewType)
            gui.closeClient(client, true) shouldBe false
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should return true if the client is viewing the GUI`() = runTest {
            val type = InventoryType.DISPENSER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()

            val initialInventoryViewType = player.openInventory.type

            gui.openClient(client) shouldBe true
            player.assertInventoryView(type)
            gui.closeClient(client, true) shouldBe true
            player.assertInventoryView(initialInventoryViewType)
        }

        @Test
        fun `should not close for other clients`() = runTest {
            val type = InventoryType.HOPPER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()
            val initialInventoryViewType = player2.openInventory.type

            gui.openClient(client) shouldBe true
            gui.openClient(client2) shouldBe true

            player.assertInventoryView(type)
            player2.assertInventoryView(type)

            gui.closeClient(client2, true) shouldBe true
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

    protected suspend fun registerPlayer(playerMock: PlayerMock? = null): Pair<PlayerMock, Client> {
        val player = playerMock?.also { serverMock.addPlayer(it) } ?: serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }
}
