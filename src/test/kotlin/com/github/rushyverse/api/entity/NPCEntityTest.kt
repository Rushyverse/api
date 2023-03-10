package com.github.rushyverse.api.entity

import com.github.rushyverse.api.position.IAreaLocatable
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertNull

class NPCEntityTest : CommonNPCEntityTest() {

    @Nested
    inner class Instantiation {

        @Test
        fun `should set area trigger as null if not defined`() {
            val npc = NPCEntity(EntityType.CREEPER)
            assertNull(npc.areaTrigger)
        }
    }

    override fun createEntity(area: IAreaLocatable<Player>?): NPCEntity {
        return NPCEntity(EntityType.CREEPER, area)
    }

}