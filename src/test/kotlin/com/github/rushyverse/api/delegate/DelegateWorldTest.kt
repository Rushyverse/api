package com.github.rushyverse.api.delegate

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import org.bukkit.World
import java.util.*
import kotlin.test.*

class DelegateWorldTest {

    private lateinit var world: WorldMock

    @BeforeTest
    fun onBefore() {
        world = WorldMock()
        MockBukkit.mock().apply {
            addWorld(world)
        }
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Test
    fun `get world if server has it`() {
        val delegate = DelegateWorld(world.uid)

        val obj = object {
            val property: World? by delegate
        }

        assertEquals(world, obj.property)
    }

    @Test
    fun `get world if server doesn't have it`() {
        val uuid = UUID.randomUUID()
        val delegate = DelegateWorld(uuid)

        val obj = object {
            val property: World? by delegate
        }

        assertNull(obj.property)
    }
}
