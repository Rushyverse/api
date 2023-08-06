package com.github.rushyverse.api.serializer

import org.bukkit.Material

/**
 * Serializer for [Material].
 */
public object MaterialSerializer : EnumSerializer<Material>("material", Material.entries)
