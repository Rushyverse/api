package com.github.rushyverse.api.serializer

import org.bukkit.inventory.ItemFlag

/**
 * Serializer for [ItemFlag].
 */
public object ItemFlagSerializer : EnumSerializer<ItemFlag>("itemFlag", ItemFlag.entries)
