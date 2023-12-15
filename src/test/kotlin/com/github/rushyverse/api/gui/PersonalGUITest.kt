package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.ClientManager
import com.github.rushyverse.api.player.ClientManagerImpl
import com.github.rushyverse.api.translation.Translator
import io.kotest.matchers.collections.shouldContainAll
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

class PersonalGUITest: AbstractKoinTest() {

    private lateinit var guiManager: GUIManager
    private lateinit var clientManager: ClientManager
    private lateinit var translator: Translator
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

    }

    @Nested
    inner class Contains {

    }

    @Nested
    inner class CloseForClient {

    }

    @Nested
    inner class Close {

        @Test
        fun `should close all inventories and remove all viewers`() = runTest(timeout = 1.minutes) {
            val type = InventoryType.HOPPER
            val gui = object : PersonalGUI() {
                override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
                    return serverMock.createInventory(owner, type)
                }

                override suspend fun fill(client: Client, inventory: Inventory) {}

                override suspend fun onClick(client: Client, clickedItem: ItemStack, event: InventoryClickEvent) {
                    error("Should not be called")
                }
            }

            val playerClients = List(5) { registerPlayer() }
            playerClients.forEach { (player, client) ->
                gui.open(client) shouldBe true
                player.assertInventoryView(type)
                client.gui() shouldBe gui
            }

            gui.close()
            playerClients.forEach { (player, client) ->
                player.assertInventoryView(InventoryType.CRAFTING)
                client.gui() shouldBe null
            }
        }

        @Test
        fun `should set isClosed to true`() {
            val gui = createGUI()
            gui.isClosed shouldBe false
            gui.close()
            gui.isClosed shouldBe true
        }

        @Test
        fun `should unregister the GUI`() {
            val gui = createGUI()
            guiManager.guis shouldContainAll listOf(gui)
            gui.close()
            guiManager.guis shouldContainAll listOf()
        }

        private fun createGUI(): PersonalGUI {
            return object : PersonalGUI() {
                override fun createInventory(owner: InventoryHolder, client: Client): Inventory {
                    error("Should not be called")
                }

                override suspend fun fill(client: Client, inventory: Inventory) {
                    error("Should not be called")
                }

                override suspend fun onClick(client: Client, clickedItem: ItemStack, event: InventoryClickEvent) {
                    error("Should not be called")
                }
            }
        }

    }

    private suspend fun registerPlayer(): Pair<PlayerMock, Client> {
        val player = serverMock.addPlayer()
        val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
        clientManager.put(player, client)
        return player to client
    }
}
