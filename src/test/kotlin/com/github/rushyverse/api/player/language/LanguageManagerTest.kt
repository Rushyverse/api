package com.github.rushyverse.api.player.language

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.github.rushyverse.api.translation.SupportedLanguage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Nested
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class LanguageManagerTest {

    private lateinit var manager: LanguageManager

    private lateinit var server: ServerMock

    @BeforeTest
    fun onBefore() {
        manager = LanguageManager()
        server = MockBukkit.mock()
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Nested
    inner class Get {

        @Test
        fun `should return the language associated with the player`() = runTest {
            val player = server.addPlayer()
            manager.set(player, SupportedLanguage.FRENCH)
            manager.get(player) shouldBe SupportedLanguage.FRENCH
        }

        @Test
        fun `should return the default language if the player has no language`() = runTest {
            val player = server.addPlayer()
            manager.get(player) shouldBe SupportedLanguage.ENGLISH
        }

    }

    @Nested
    inner class Set {

        @Test
        fun `should overwrite the language for the player`() = runTest {
            val player = server.addPlayer()
            manager.set(player, SupportedLanguage.FRENCH)
            manager.get(player) shouldBe SupportedLanguage.FRENCH

            manager.set(player, SupportedLanguage.ENGLISH)
            manager.get(player) shouldBe SupportedLanguage.ENGLISH
        }

        @Test
        fun `should set for several players`() = runTest {
            val player1 = server.addPlayer()
            val player2 = server.addPlayer()

            manager.set(player1, SupportedLanguage.FRENCH)
            manager.set(player2, SupportedLanguage.GERMAN)

            manager.get(player1) shouldBe SupportedLanguage.FRENCH
            manager.get(player2) shouldBe SupportedLanguage.GERMAN
        }

    }

    @Nested
    inner class Remove {

        @Test
        fun `should remove the language associated with the player`() = runTest {
            val player = server.addPlayer()
            val player2 = server.addPlayer()

            manager.set(player, SupportedLanguage.FRENCH)
            manager.get(player) shouldBe SupportedLanguage.FRENCH

            manager.set(player2, SupportedLanguage.GERMAN)
            manager.get(player2) shouldBe SupportedLanguage.GERMAN

            manager.remove(player)

            manager.get(player) shouldBe SupportedLanguage.ENGLISH
            manager.get(player2) shouldBe SupportedLanguage.GERMAN
        }

        @Test
        fun `should do nothing if the player has no language`() = runTest {
            val player = server.addPlayer()
            manager.remove(player)

            manager.get(player) shouldBe SupportedLanguage.ENGLISH
        }

    }
}
