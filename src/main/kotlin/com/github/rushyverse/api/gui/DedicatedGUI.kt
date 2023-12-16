package com.github.rushyverse.api.gui

import com.github.rushyverse.api.player.Client
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * GUI where a new inventory is created for each key used.
 * @param T Type of the key.
 * @property inventories Map of inventories for each key.
 * @property mutex Mutex to process thread-safe operations.
 */
public abstract class DedicatedGUI<T> : GUI() {

    protected var inventories: MutableMap<T, Inventory> = mutableMapOf()

    protected val mutex: Mutex = Mutex()

    override suspend fun openGUI(client: Client): Boolean {
        val key = getKey(client)
        val inventory = getOrCreateInventory(key)

        val player = client.requirePlayer()
        player.openInventory(inventory)

        return true
    }

    /**
     * Get the inventory for the key.
     * If the inventory does not exist, create it.
     * @param key Key to get the inventory for.
     * @return The inventory for the key.
     */
    private suspend fun getOrCreateInventory(key: T): Inventory {
        return mutex.withLock {
            inventories[key] ?: createInventory(key).also {
                inventories[key] = it

                val coroutineScopeLoading = coroutineScopeFill(key)
                coroutineScopeLoading.launch {
                    val fillJob = coroutineScopeLoading.async {
                        createInventoryContents(key, it)
                    }

                    val loadingAnimationJob = loadingAnimation(key, it)
                    // Set the inventory contents only when the fill is done.
                    // This will erase the loading animation.
                    it.contents = fillJob.await().also {
                        loadingAnimationJob.cancel()
                    }
                }
            }
        }
    }

    override suspend fun hasInventory(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.contains(inventory)
        }
    }

    override suspend fun viewers(): List<HumanEntity> {
        return mutex.withLock {
            unsafeViewers()
        }
    }

    /**
     * Get the viewers of the inventory.
     * This function is not thread-safe.
     * @return The viewers of the inventory.
     */
    protected open fun unsafeViewers(): List<HumanEntity> {
        return inventories.values.flatMap(Inventory::getViewers)
    }

    override suspend fun contains(client: Client): Boolean {
        return mutex.withLock {
            unsafeContains(client)
        }
    }

    /**
     * Check if the GUI contains the client.
     * This function is not thread-safe.
     * @param client Client to check.
     * @return True if the GUI contains the client, false otherwise.
     */
    protected open fun unsafeContains(client: Client): Boolean {
        val player = client.player ?: return false
        return inventories.values.any { it.viewers.contains(player) }
    }

    override suspend fun close() {
        super.close()
        mutex.withLock {
            inventories.values.forEach(Inventory::close)
            inventories.clear()
        }
    }

    /**
     * Get the items to use for the loading animation.
     * @return Sequence of ItemStack to use for the loading animation.
     */
    protected open fun loadingItems(key: T): Sequence<ItemStack> {
        return sequence {
            yield(ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE))
            yield(ItemStack(Material.BLUE_STAINED_GLASS_PANE))
            yield(ItemStack(Material.PURPLE_STAINED_GLASS_PANE))

            val blackGlassPaneItem = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
            yieldAll(generateSequence { blackGlassPaneItem })
        }
    }

    /**
     * Animate the inventory while it is being filled by another coroutine.
     * @receiver Scope to launch the animation in.
     * @param inventory Inventory to animate.
     * @return Job of the animation.
     */
    protected open fun CoroutineScope.loadingAnimation(key: T, inventory: Inventory): Job {
        return launch {
            val size = inventory.size
            val contents = arrayOfNulls<ItemStack>(size)
            loadingItems(key).take(size).forEachIndexed { index, item ->
                contents[index] = item
            }

            val contentList = contents.toMutableList()
            while (isActive) {
                delay(100)
                Collections.rotate(contentList, 1)
                inventory.contents = contentList.toTypedArray()
            }
        }
    }

    /**
     * Create a new array of ItemStack to fill the inventory later.
     * This function is used to avoid conflicts when filling the inventory and the loading animation.
     * @param inventory Inventory to fill.
     * @param key Key to fill the inventory for.
     * @return New created array of ItemStack that will be set to the inventory.
     */
    private suspend fun createInventoryContents(key: T, inventory: Inventory): Array<ItemStack?> {
        return arrayOfNulls<ItemStack>(inventory.size).apply { fill(key, this) }
    }

    /**
     * Get the key linked to the client to interact with the GUI.
     * @param client Client to get the key for.
     * @return The key.
     */
    protected abstract suspend fun getKey(client: Client): T

    /**
     * Create the inventory for the key.
     * @param key Key to create the inventory for.
     * @return New created inventory.
     */
    protected abstract suspend fun createInventory(key: T): Inventory

    /**
     * Fill the inventory for the key.
     * @param key Key to fill the inventory for.
     * @param inventory Inventory to fill.
     */
    protected abstract suspend fun fill(key: T, inventory: Array<ItemStack?>)

    /**
     * Get the coroutine scope to fill the inventory and the loading animation.
     * @param key Key to get the coroutine scope for.
     * @return The coroutine scope.
     */
    protected abstract suspend fun coroutineScopeFill(key: T): CoroutineScope
}
