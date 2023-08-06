package com.github.rushyverse.api.serializer

import org.bukkit.DyeColor

/**
 * Serializer for [DyeColor].
 */
public object DyeColorSerializer : EnumSerializer<DyeColor>("dyeColor", DyeColor.entries)
