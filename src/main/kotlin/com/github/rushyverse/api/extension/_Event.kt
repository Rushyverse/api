package com.github.rushyverse.api.extension

import org.bukkit.entity.Damageable
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Future life of the damaged entity.
 * @return If the entity is not damageable returns null, otherwise return the future health.
 */
public fun EntityDamageEvent.finalDamagedHealth(): Double? {
    val damaged = entity
    return if (damaged is Damageable) {
        damaged.health - finalDamage
    } else {
        null
    }
}