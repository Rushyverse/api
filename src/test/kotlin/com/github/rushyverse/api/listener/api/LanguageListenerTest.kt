package com.github.rushyverse.api.listener.api

import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.language.LanguageManager
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.test.BeforeTest
import kotlin.test.Test

class LanguageListenerTest: AbstractKoinTest() {

    private lateinit var listener: LanguageListener
    private lateinit var languageManager: LanguageManager

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        listener = LanguageListener()
        languageManager = mockk()

        loadApiTestModule {
            single { languageManager }
        }
    }

    @Test
    fun `should call remove in manager when player leave`() = runTest {
        val player = mockk<Player>()
        coJustRun { languageManager.remove(player) }
        listener.onQuit(PlayerQuitEvent(player, Component.empty(), PlayerQuitEvent.QuitReason.DISCONNECTED))
        coVerify(exactly = 1) { languageManager.remove(player) }
    }

}
