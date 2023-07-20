package com.github.rushyverse.api.extension

import io.mockk.every
import io.mockk.mockk
import org.bukkit.entity.Damageable
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EventExtTest {

    @Nested
    inner class EntityDamageEventTest {

        @Nested
        @DisplayName("Get final health of damaged")
        inner class FinalHealth {

            @Test
            fun `null if the damaged is not damaged`() {
                val entityNotDamaged = mockk<Entity>()
                val event = mockk<EntityDamageEvent>()
                every { event.entity } returns entityNotDamaged
                assertNull(event.finalDamagedHealth())
            }

            @Test
            fun `future health computed when entity is damaged`() {
                val damaged = mockk<Damageable>()
                val currentHealth = Random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)
                every { damaged.health } returns currentHealth

                val event = mockk<EntityDamageEvent>()
                every { event.entity } returns damaged

                val finalDamage = Random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)
                every { event.finalDamage } returns finalDamage
                assertEquals(currentHealth - finalDamage, event.finalDamagedHealth())
            }
        }

    }


}