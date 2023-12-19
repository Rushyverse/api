package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.player.Client
import com.github.shynixn.mccoroutine.bukkit.scope
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SingleGUITest : AbstractGUITest() {

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        mockkStatic("com.github.shynixn.mccoroutine.bukkit.MCCoroutineKt")
        every { plugin.scope } returns CoroutineScope(EmptyCoroutineContext)
    }

    override fun createNonFillGUI(inventoryType: InventoryType): GUI<*> {
        return SingleNonFillGUI(plugin, serverMock, inventoryType)
    }

    override fun createFillGUI(items: Array<ItemStack>, inventoryType: InventoryType, delay: Duration?): GUI<*> {
        return SingleFillGUI(plugin, serverMock, inventoryType, items, delay)
    }

    override fun getFillThreadBeforeSuspend(gui: GUI<*>): Thread? {
        return (gui as SingleFillGUI).calledThread
    }

    override fun getFillThreadAfterSuspend(gui: GUI<*>): Thread? {
        return (gui as SingleFillGUI).newThread
    }

    @Nested
    inner class Register : AbstractGUITest.Register()

    @Nested
    inner class Viewers : AbstractGUITest.Viewers()

    @Nested
    inner class Contains : AbstractGUITest.Contains()

    @Nested
    inner class OpenClient : AbstractGUITest.OpenClient() {

        @Test
        fun `should use the same inventory for all clients`() = runTest {
            val type = InventoryType.ENDER_CHEST
            val gui = createNonFillGUI(type)
            val inventories = List(5) {
                val (player, client) = registerPlayer()
                gui.openClient(client) shouldBe true
                player.assertInventoryView(type)

                player.openInventory.topInventory
            }

            inventories.all { it === inventories.first() } shouldBe true
        }

        @Test
        fun `should not create a new inventory for the same client if previously closed`() = runTest {
            val type = InventoryType.BREWING
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()

            gui.openClient(client) shouldBe true
            val firstInventory = player.openInventory.topInventory

            gui.closeClient(client, true) shouldBe true

            gui.openClient(client) shouldBe true
            player.openInventory.topInventory shouldBe firstInventory

            player.assertInventoryView(type)
        }
    }

    @Nested
    inner class UpdateClient : AbstractGUITest.UpdateClient()

    @Nested
    inner class HasInventory : AbstractGUITest.HasInventory()

    @Nested
    inner class IsInventoryLoading : AbstractGUITest.IsInventoryLoading()

    @Nested
    inner class Close : AbstractGUITest.Close()

    @Nested
    inner class CloseClient : AbstractGUITest.CloseClient() {

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `should not stop loading the inventory if the client is viewing the GUI`(closeInventory: Boolean) {
            runBlocking {
                val type = InventoryType.DROPPER
                val gui = createFillGUI(emptyArray(), delay = 10.minutes, inventoryType = type)
                gui.register()
                val (player, client) = registerPlayer()

                val initialInventoryViewType = player.openInventory.type

                gui.openClient(client) shouldBe true
                player.assertInventoryView(type)

                val openInventory = player.openInventory
                val inventory = openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                gui.closeClient(client, closeInventory) shouldBe closeInventory
                gui.isInventoryLoading(inventory) shouldBe true

                if (closeInventory) {
                    player.assertInventoryView(initialInventoryViewType)
                    gui.contains(client) shouldBe false
                } else {
                    player.assertInventoryView(type)
                    gui.contains(client) shouldBe true
                }
            }
        }

    }
}

private abstract class AbstractSingleGUITest(
    plugin: Plugin,
    val serverMock: ServerMock,
    val type: InventoryType
) : SingleGUI(plugin) {

    override fun createInventory(): Inventory {
        return serverMock.createInventory(null, type)
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

private class SingleNonFillGUI(
    plugin: Plugin,
    serverMock: ServerMock,
    type: InventoryType
) : AbstractSingleGUITest(plugin, serverMock, type) {

    override fun getItems(size: Int): Flow<ItemStackIndex> {
        return emptyFlow()
    }

}

private class SingleFillGUI(
    plugin: Plugin,
    serverMock: ServerMock,
    type: InventoryType,
    val items: Array<ItemStack>,
    val delay: Duration?
) : AbstractSingleGUITest(plugin, serverMock, type) {

    var calledThread: Thread? = null

    var newThread: Thread? = null

    override fun getItems(size: Int): Flow<ItemStackIndex> {
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
