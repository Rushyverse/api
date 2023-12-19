package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.player.Client
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PlayerGUITest : AbstractGUITest() {

    @Nested
    inner class Register : AbstractGUITest.Register()

    @Nested
    inner class Viewers : AbstractGUITest.Viewers()

    @Nested
    inner class Contains : AbstractGUITest.Contains()

    @Nested
    inner class Open : AbstractGUITest.Open() {

        @Test
        fun `should create a new inventory for the client`() = runTest {
            val type = InventoryType.ENDER_CHEST
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(type)
            player2.assertInventoryView(type)

            player.openInventory.topInventory shouldNotBe player2.openInventory.topInventory
        }

        @Test
        fun `should create a new inventory for the same client if previous is closed before`() = runTest {
            val type = InventoryType.BREWING
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()

            gui.open(client) shouldBe true
            val firstInventory = player.openInventory.topInventory

            gui.close(client, true) shouldBe true

            gui.open(client) shouldBe true
            player.openInventory.topInventory shouldNotBe firstInventory

            player.assertInventoryView(type)
        }
    }

    @Nested
    inner class Update : AbstractGUITest.Update()

    @Nested
    inner class Close : AbstractGUITest.Close()

    @Nested
    inner class CloseForClient : AbstractGUITest.CloseForClient() {

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `should stop loading the inventory if the client is viewing the GUI`(closeInventory: Boolean) {
            runBlocking {
                val type = InventoryType.DROPPER
                val gui = createFillGUI(items = emptyArray(), inventoryType = type, delay = 10.minutes)
                gui.register()
                val (player, client) = registerPlayer()

                val initialInventoryViewType = player.openInventory.type

                gui.open(client) shouldBe true
                player.assertInventoryView(type)

                val openInventory = player.openInventory
                val inventory = openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                gui.close(client, closeInventory) shouldBe true
                gui.isInventoryLoading(inventory) shouldBe false

                if (closeInventory) {
                    player.assertInventoryView(initialInventoryViewType)
                } else {
                    player.assertInventoryView(type)
                }
            }
        }

        @Test
        fun `should remove client inventory without closing it if closeInventory is false`() =
            runTest {
                val type = InventoryType.ENDER_CHEST
                val gui = NonFillGUI(serverMock, type = type)
                val (player, client) = registerPlayer()

                gui.open(client) shouldBe true
                player.assertInventoryView(type)

                gui.close(client, false) shouldBe true
                player.assertInventoryView(type)

                gui.contains(client) shouldBe false
            }
    }

    override fun createNonFillGUI(inventoryType: InventoryType): GUI<*> {
        return NonFillGUI(serverMock, inventoryType)
    }

    override fun createFillGUI(items: Array<ItemStack>, inventoryType: InventoryType, delay: Duration?): GUI<*> {
        return FillGUI(serverMock, inventoryType, items, delay)
    }

    override fun getFillThreadBeforeSuspend(gui: GUI<*>): Thread? {
        return (gui as FillGUI).calledThread
    }

    override fun getFillThreadAfterSuspend(gui: GUI<*>): Thread? {
        return (gui as FillGUI).newThread
    }
}

private abstract class AbstractPlayerGUITest(
    val serverMock: ServerMock,
    val type: InventoryType
) : PlayerGUI() {

    override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
        return serverMock.createInventory(owner, type)
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

private class NonFillGUI(
    serverMock: ServerMock,
    type: InventoryType
) : AbstractPlayerGUITest(serverMock, type) {

    override fun getItems(key: Client, size: Int): Flow<ItemStackIndex> {
        return emptyFlow()
    }
}

private class FillGUI(
    serverMock: ServerMock,
    type: InventoryType,
    val items: Array<ItemStack>,
    val delay: Duration?
) : AbstractPlayerGUITest(serverMock, type) {

    var calledThread: Thread? = null

    var newThread: Thread? = null

    override fun getItems(key: Client, size: Int): Flow<ItemStackIndex> {
        calledThread = Thread.currentThread()
        return flow {
            delay?.let { delay(it) }
            items.forEachIndexed { index, item ->
                emit(index to item)
            }
            newThread = Thread.currentThread()
        }
    }
}
