package fr.distractic.bukkit.api.delegate

import fr.distractic.bukkit.api.koin.inject
import org.bukkit.Server
import org.bukkit.World
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Class to delegate the retrieve of a world through Bukkit.
 * @property uuid World's UUID.
 * @property server Server to find the world.
 */
public class DelegateWorld(pluginId: String, public val uuid: UUID) : ReadOnlyProperty<Any?, World?> {

    public val server: Server by inject(pluginId)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): World? {
        return server.getWorld(uuid)
    }
}