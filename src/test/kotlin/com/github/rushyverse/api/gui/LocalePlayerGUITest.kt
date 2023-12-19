package com.github.rushyverse.api.gui

import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.player.Client
import com.github.rushyverse.api.player.language.LanguageManager
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.shynixn.mccoroutine.bukkit.scope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockkStatic
import java.util.*
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

class LocalePlayerGUITest : AbstractGUITest() {

    private lateinit var languageManager: LanguageManager

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        languageManager = LanguageManager()

        loadApiTestModule {
            single { languageManager }
        }

        mockkStatic("com.github.shynixn.mccoroutine.bukkit.MCCoroutineKt")
        every { plugin.scope } returns CoroutineScope(EmptyCoroutineContext)
    }

    override fun createFillGUI(items: Array<ItemStack>, inventoryType: InventoryType, delay: Duration?): GUI<*> {
        return LocaleFillGUI(plugin, serverMock, inventoryType, items, delay)
    }

    override fun createNonFillGUI(inventoryType: InventoryType): GUI<*> {
        return LocaleNonFillGUI(plugin, serverMock, inventoryType)
    }

    override fun getFillThreadAfterSuspend(gui: GUI<*>): Thread? {
        return (gui as LocaleFillGUI).newThread
    }

    override fun getFillThreadBeforeSuspend(gui: GUI<*>): Thread? {
        return (gui as LocaleFillGUI).calledThread
    }

    @Nested
    inner class Register : AbstractGUITest.Register()

    @Nested
    inner class Viewers : AbstractGUITest.Viewers()

    @Nested
    inner class Contains : AbstractGUITest.Contains()

    @Nested
    inner class Open : AbstractGUITest.Open() {

        @Test
        fun `should create a new inventory according to the language client`() = runTest {
            val type = InventoryType.HOPPER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()
            languageManager.set(player, SupportedLanguage.ENGLISH)
            languageManager.set(player2, SupportedLanguage.FRENCH)

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(type)
            player2.assertInventoryView(type)

            player.openInventory.topInventory shouldNotBe player2.openInventory.topInventory
        }

        @Test
        fun `should use the same inventory according to the language client`() = runTest {
            val type = InventoryType.DISPENSER
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()
            val (player2, client2) = registerPlayer()
            languageManager.set(player, SupportedLanguage.FRENCH)
            languageManager.set(player2, SupportedLanguage.FRENCH)

            gui.open(client) shouldBe true
            gui.open(client2) shouldBe true

            player.assertInventoryView(type)
            player2.assertInventoryView(type)

            player.openInventory.topInventory shouldBe player2.openInventory.topInventory
        }

        @Test
        fun `should not create a new inventory for the same client if previously closed`() = runTest {
            val type = InventoryType.BREWING
            val gui = createNonFillGUI(type)
            val (player, client) = registerPlayer()

            gui.open(client) shouldBe true
            val firstInventory = player.openInventory.topInventory

            gui.close(client, true) shouldBe true

            gui.open(client) shouldBe true
            player.openInventory.topInventory shouldBe firstInventory

            player.assertInventoryView(type)
        }

    }

    @Nested
    inner class Close : AbstractGUITest.Close()

    @Nested
    inner class CloseForClient : AbstractGUITest.CloseForClient() {

        @ParameterizedTest
        @ValueSource(booleans = [true, false])
        fun `should not stop loading the inventory if the client is viewing the GUI`(closeInventory: Boolean) {
            runBlocking {
                val type = InventoryType.HOPPER
                val gui = createFillGUI(emptyArray(), delay = 10.minutes, inventoryType = type)
                gui.register()
                val (player, client) = registerPlayer()

                val initialInventoryViewType = player.openInventory.type

                gui.open(client) shouldBe true
                player.assertInventoryView(type)

                val openInventory = player.openInventory
                val inventory = openInventory.topInventory
                gui.isInventoryLoading(inventory) shouldBe true

                gui.close(client, closeInventory) shouldBe closeInventory
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

private abstract class AbstractLocaleGUITest(
    plugin: Plugin,
    val serverMock: ServerMock,
    val type: InventoryType = InventoryType.HOPPER
) : LocaleGUI(plugin) {

    override fun createInventory(key: Locale): Inventory {
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

private class LocaleNonFillGUI(
    plugin: Plugin,
    serverMock: ServerMock,
    type: InventoryType
) : AbstractLocaleGUITest(plugin, serverMock, type) {

    override fun getItems(key: Locale, size: Int): Flow<ItemStackIndex> {
        return emptyFlow()
    }
}

private class LocaleFillGUI(
    plugin: Plugin,
    serverMock: ServerMock,
    type: InventoryType,
    val items: Array<ItemStack>,
    val delay: Duration?
) : AbstractLocaleGUITest(plugin, serverMock, type) {

    var calledThread: Thread? = null

    var newThread: Thread? = null

    override fun getItems(key: Locale, size: Int): Flow<ItemStackIndex> {
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
