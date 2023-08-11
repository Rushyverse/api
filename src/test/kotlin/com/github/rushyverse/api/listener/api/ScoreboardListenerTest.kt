package com.github.rushyverse.api.listener.api

import com.github.rushyverse.api.AbstractKoinTest
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.test.BeforeTest
import kotlin.test.Test

class ScoreboardListenerTest: AbstractKoinTest() {

    private lateinit var listener: ScoreboardListener
    private lateinit var scoreboardManager: ScoreboardManager

    @BeforeTest
    override fun onBefore() {
        super.onBefore()
        listener = ScoreboardListener()
        scoreboardManager = mockk()

        loadApiTestModule {
            single { scoreboardManager }
        }
    }

    @Test
    fun `should call remove in manager when player leave`() = runTest {
        val player = mockk<Player>()
        coJustRun { scoreboardManager.remove(player) }
        listener.onQuit(PlayerQuitEvent(player, Component.empty(), PlayerQuitEvent.QuitReason.DISCONNECTED))
        coVerify(exactly = 1) { scoreboardManager.remove(player) }
    }

}
