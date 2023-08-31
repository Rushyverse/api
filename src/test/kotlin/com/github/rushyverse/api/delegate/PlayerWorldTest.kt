package com.github.rushyverse.api.delegate

import be.seeseemelk.mockbukkit.MockBukkit
import org.bukkit.entity.Player
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PlayerWorldTest {

    private lateinit var player: Player

    @BeforeTest
    fun onBefore() {
        MockBukkit.mock().apply {
            player = addPlayer()
        }
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Test
    fun `get player if server has it`() {
        val delegate = DelegatePlayer(player.uniqueId)

        val obj = object {
            val property by delegate
        }

        assertEquals(player, obj.property)
    }

    @Test
    fun `get player if server doesn't have it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegatePlayer(uuid)

        val obj = object {
            val property by delegate
        }

        assertNull(obj.property)
    }
}
