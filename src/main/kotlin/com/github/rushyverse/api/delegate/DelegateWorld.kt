package com.github.rushyverse.api.delegate

import org.bukkit.Bukkit
import org.bukkit.World
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Class to delegate the retrieve of a world through Bukkit.
 * @property uuid World's UUID.
 */
public class DelegateWorld(public val uuid: UUID) : ReadOnlyProperty<Any?, World?> {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): World? {
        return Bukkit.getWorld(uuid)
    }
}
