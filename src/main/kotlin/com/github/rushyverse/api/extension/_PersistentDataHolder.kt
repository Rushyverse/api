package com.github.rushyverse.api.extension

import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Open the data container and manage info in it.
 * @receiver PersistentDataHolder.
 * @param block Function to use data container.
 * @return Type of the returns type.
 */
public inline fun <T> PersistentDataHolder.dataContainer(block: PersistentDataContainer.() -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return persistentDataContainer.block()
}