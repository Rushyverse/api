package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested

class GUIManagerTest {

    private lateinit var manager: GUIManager

    @BeforeTest
    fun onBefore() {
        manager = GUIManager()
    }

    @Nested
    inner class Get {

        @Test
        fun `should returns null if no GUI is registered`() = runTest {
            val client = mockk<Client>()
            manager.get(client) shouldBe null
        }

        @Test
        fun `should returns null if no GUI contains the client`() = runTest {
            val client = mockk<Client>()
            val gui = mockk<GUI> {
                coEvery { contains(any()) } returns false
            }
            manager.add(gui)
            manager.get(client) shouldBe null
        }

        @Test
        fun `should returns GUI if contains the client`() = runTest {
            val client = mockk<Client>()
            val gui = mockk<GUI> {
                coEvery { contains(client) } returns true
            }
            manager.add(gui)
            manager.get(client) shouldBe gui
        }

        @Test
        fun `should returns GUI if contains the asked client`() = runTest {
            val client = mockk<Client>()
            val client2 = mockk<Client>()
            val gui = mockk<GUI> {
                coEvery { contains(client) } returns true
                coEvery { contains(client2) } returns false
            }
            manager.add(gui)
            manager.get(client) shouldBe gui
            manager.get(client2) shouldBe null
        }

    }

    @Nested
    inner class Add {

        @Test
        fun `should add non registered GUI`() = runTest {
            val gui = mockk<GUI>()
            manager.add(gui) shouldBe true
            manager.guis.contains(gui) shouldBe true
            manager.guis.size shouldBe 1
        }

        @Test
        fun `should not add registered GUI`() = runTest {
            val gui = mockk<GUI>()
            manager.add(gui) shouldBe true
            manager.add(gui) shouldBe false
            manager.guis.contains(gui) shouldBe true
            manager.guis.size shouldBe 1
        }

        @Test
        fun `should add multiple GUIs`() = runTest {
            val gui1 = mockk<GUI>()
            val gui2 = mockk<GUI>()
            manager.add(gui1) shouldBe true
            manager.add(gui2) shouldBe true
            manager.guis.contains(gui1) shouldBe true
            manager.guis.contains(gui2) shouldBe true
            manager.guis.size shouldBe 2
        }

    }

    @Nested
    inner class Remove {

        @Test
        fun `should remove registered GUI`() = runTest {
            val gui = mockk<GUI>()
            manager.add(gui) shouldBe true
            manager.remove(gui) shouldBe true
            manager.guis.contains(gui) shouldBe false
            manager.guis.size shouldBe 0
        }

        @Test
        fun `should not remove non registered GUI`() = runTest {
            val gui = mockk<GUI>()
            manager.remove(gui) shouldBe false
            manager.guis.contains(gui) shouldBe false
            manager.guis.size shouldBe 0
        }

        @Test
        fun `should remove one GUI`() = runTest {
            val gui1 = mockk<GUI>()
            val gui2 = mockk<GUI>()
            manager.add(gui1) shouldBe true
            manager.add(gui2) shouldBe true
            manager.remove(gui1) shouldBe true
            manager.guis.contains(gui1) shouldBe false
            manager.guis.contains(gui2) shouldBe true
            manager.guis.size shouldBe 1
        }

    }
}
