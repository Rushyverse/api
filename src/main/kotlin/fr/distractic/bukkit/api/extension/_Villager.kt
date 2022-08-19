package fr.distractic.bukkit.api.extension

import org.bukkit.NamespacedKey
import org.bukkit.entity.Villager
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

/**
 * Key of the [NamespacedKey] to define if a villager must be keep his job or lose it.
 */
private const val KEY_KEEP_JOB = "KEEP_JOB"

/**
 * Create an instance of [NamespacedKey] corresponding to the key about the keep of profession for a villager.
 * @param plugin Plugin to use for the namespace.
 * @return New instance of [NamespacedKey].
 */
public fun namespacedKeyKeepJob(plugin: Plugin): NamespacedKey = NamespacedKey(plugin, KEY_KEEP_JOB)

/**
 * Check if a villager must be keep his profession.
 * @receiver Villager.
 * @param plugin Plugin to find the data.
 * @return `true` if the villager must be keep his job, `false` otherwise.
 */
public fun Villager.keepProfession(plugin: Plugin): Boolean {
    return dataContainer {
        get(namespacedKeyKeepJob(plugin), PersistentDataType.BYTE)
    } != null
}

/**
 * Define if a villager must be keep his profession or not.
 * @receiver Villager.
 * @param plugin Plugin to define the data.
 * @param keep `true` if the villager must be keep his job, `false` otherwise.
 */
public fun Villager.keepProfession(plugin: Plugin, keep: Boolean) {
    dataContainer {
        val key = namespacedKeyKeepJob(plugin)
        if (keep) {
            set(key, PersistentDataType.BYTE, 0)
        } else {
            remove(key)
        }
    }
}