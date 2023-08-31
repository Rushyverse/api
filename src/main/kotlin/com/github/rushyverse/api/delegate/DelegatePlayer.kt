package com.github.rushyverse.api.delegate

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Class to delegate the retrieve of a player through Bukkit.
 * @property uuid Player's UUID.
 */
public class DelegatePlayer(public val uuid: UUID) : ReadOnlyProperty<Any?, Player?> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Player? {
        return Bukkit.getPlayer(uuid)
    }
}
