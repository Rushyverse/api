package com.github.rushyverse.api.extension.event

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.extension.ItemStack
import com.github.shynixn.mccoroutine.bukkit.callSuspendingEvent
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class EventExtTest {

    private lateinit var serverMock: ServerMock

    @BeforeTest
    fun onBefore() {
        serverMock = MockBukkit.mock()
        mockkStatic("com.github.shynixn.mccoroutine.bukkit.MCCoroutineKt")
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
        unmockkAll()
    }

    @Test
    fun `should trigger right click`() = runTest {
        val plugin = MockBukkit.createMockPlugin()
        val player = serverMock.addPlayer()
        val item = ItemStack { type = Material.DIRT }
        val slot = slot<PlayerInteractEvent>()

        lateinit var jobs: List<Job>
        val pluginManager = serverMock.pluginManager
        every { pluginManager.callSuspendingEvent(capture(slot), plugin) } answers {
            List(5) { async { delay(1.seconds) } }.apply { jobs = this }
        }

        callRightClickOnItemEvent(plugin, player, item)

        verify(exactly = 1) { pluginManager.callSuspendingEvent(any(), plugin) }
        jobs.forEach { it.isCompleted shouldBe true }

        val event = slot.captured
        event.player shouldBe player
        event.action shouldBe Action.RIGHT_CLICK_AIR
        event.item shouldBe item
        event.clickedBlock shouldBe null
    }

}
