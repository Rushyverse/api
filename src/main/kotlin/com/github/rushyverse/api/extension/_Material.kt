package com.github.rushyverse.api.extension

import org.bukkit.Material
import org.bukkit.Tag

/**
 * Checks whether the material is wool.
 *
 * @receiver Material the material to be checked
 * @return `true` if the material is wool, `false` otherwise
 */
public fun Material.isWool(): Boolean = Tag.WOOL.isTagged(this)

/**
 * Checks if the material is a wool carpet.
 *
 * @receiver The material to check.
 * @return `true` if the material is a wool carpet, `false` otherwise.
 */
public fun Material.isWoolCarpet(): Boolean = Tag.WOOL_CARPETS.isTagged(this)
