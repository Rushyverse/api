package com.github.rushyverse.api.player

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.gui.GUI
import com.github.rushyverse.api.gui.GUIManager
import com.github.rushyverse.api.player.exception.PlayerNotFoundException
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested

class ClientTest : AbstractKoinTest() {

    private lateinit var player: PlayerMock
    private lateinit var serverMock: ServerMock
    private lateinit var guiManager: GUIManager

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        serverMock = MockBukkit.mock().apply {
            player = addPlayer()
        }
        guiManager = GUIManager()

        loadApiTestModule {
            single { guiManager }
        }
    }

    @AfterTest
    override fun onAfter() {
        MockBukkit.unmock()
        super.onAfter()
    }

    @Nested
    inner class GetPlayer {

        @Test
        fun `retrieve player instance not found returns null`() {
            val client = Client(UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
            assertNull(client.player)
        }

        @Test
        fun `retrieve player instance found returns the instance`() {
            val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
            assertEquals(player, client.player)
        }

        @Test
        fun `require player instance not found throws an exception`() {
            val client = Client(UUID.randomUUID(), CoroutineScope(EmptyCoroutineContext))
            assertThrows<PlayerNotFoundException> {
                client.requirePlayer()
            }
        }

        @Test
        fun `require player instance found returns the instance`() {
            val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
            assertEquals(player, client.requirePlayer())
        }

    }

    @Nested
    inner class GetGUI  {

        @Test
        fun `get GUI returns null if no GUI is registered`() = runTest {
            val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
            client.gui() shouldBe null
        }

        @Test
        fun `get GUI returns null if no GUI contains the client`() = runTest {
            val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
            val gui = mockk<GUI> {
                coEvery { contains(client) } returns false
            }
            guiManager.add(gui)
            client.gui() shouldBe null
        }

        @Test
        fun `get GUI returns GUI if contains the client`() = runTest {
            val client = Client(player.uniqueId, CoroutineScope(EmptyCoroutineContext))
            val gui = mockk<GUI> {
                coEvery { contains(client) } returns true
            }
            guiManager.add(gui)
            client.gui() shouldBe gui
        }

    }

}
