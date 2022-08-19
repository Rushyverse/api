package io.github.distractic.bukkit.api.delegate

import io.github.distractic.bukkit.api.koin.inject
import org.bukkit.Server
import org.bukkit.entity.Player
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Class to delegate the retrieve of a player through Bukkit.
 * @property uuid Player's UUID.
 * @property server Server to find the player.
 */
public class DelegatePlayer(pluginId: String, public val uuid: UUID) : ReadOnlyProperty<Any?, Player?> {

    private val server: Server by inject(pluginId)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Player? {
        return server.getPlayer(uuid)
    }
}