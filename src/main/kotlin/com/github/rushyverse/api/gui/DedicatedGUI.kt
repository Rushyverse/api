package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Pair of an index and an ItemStack.
 */
public typealias ItemStackIndex = Pair<Int, ItemStack?>

/**
 * Data class to store the inventory and the loading job.
 * Can be used to cancel the loading job if the inventory is closed.
 * @property inventory Inventory created.
 * @property job Loading job to fill & animate the loading of the inventory.
 * @property isLoading If true, the inventory is loading; otherwise it is filled or cancelled.
 */
public data class InventoryData(
    val inventory: Inventory,
    val job: Job,
) {

    val isLoading: Boolean get() = job.isActive

}

/**
 * GUI where a new inventory is created for each key used.
 * @param T Type of the key.
 * @property inventories Map of inventories for each key.
 * @property mutex Mutex to process thread-safe operations.
 */
public abstract class DedicatedGUI<T>(
    private val inventoryLoadingAnimation: InventoryLoadingAnimation<T>? = null,
) : GUI() {

    protected var inventories: MutableMap<T, InventoryData> = mutableMapOf()

    protected val mutex: Mutex = Mutex()

    override suspend fun openGUI(client: Client): Boolean {
        val key = getKey(client)
        val inventory = getOrCreateInventory(key)

        val player = client.player
        // We open the inventory out of the mutex to avoid blocking operation from registered Listener.
        if (player?.openInventory(inventory) == null) {
            // If the opening was cancelled (null returned),
            // We need to unregister the client from the GUI
            // and maybe close the inventory if it is individual.
            close(client, true)
            return false
        }

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
            val loadedInventory = inventories[key]
            if (loadedInventory != null) {
                return@withLock loadedInventory.inventory
            }

            val inventory = createInventory(key)
            // Start the fill asynchronously to avoid blocking the other inventory creation with the mutex.
            val loadingJob = startLoadingInventory(key, inventory)
            inventories[key] = InventoryData(inventory, loadingJob)

            inventory
        }
    }

    /**
     * Start the asynchronous loading animation and fill the inventory.
     * @param key Key to create the inventory for.
     * @param inventory Inventory to fill and animate.
     * @return The job that can be cancelled to stop the loading animation.
     */
    private suspend fun startLoadingInventory(key: T, inventory: Inventory): Job {
        return loadingScope(key).launch {
            val size = inventory.size
            val inventoryFlowItems = getItemStacks(key, size).cancellable()

            // If no suspend operation is used in the flow, the fill will be done in the same tick.
            if (inventoryLoadingAnimation == null) {
                // Will fill the inventory bit by bit.
                inventoryFlowItems.collect { (index, item) -> inventory.setItem(index, item) }
            } else {
                val loadingAnimationJob = launch { inventoryLoadingAnimation.loading(key, inventory) }

                // To avoid conflicts with the loading animation,
                // we need to store the items in a temporary inventory
                val temporaryInventory = arrayOfNulls<ItemStack>(size)

                inventoryFlowItems
                    .onCompletion { exception ->
                        // When the flow is finished, we cancel the loading animation.
                        loadingAnimationJob.cancelAndJoin()

                        // If the flow was completed successfully, we fill the inventory with the temporary inventory.
                        if (exception == null) {
                            inventory.contents = temporaryInventory
                        }
                    }.collect { (index, item) -> temporaryInventory[index] = item }
            }
        }
    }

    override suspend fun hasInventory(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.any { it.inventory == inventory }
        }
    }

    public override suspend fun isInventoryLoading(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.firstOrNull { it.inventory == inventory }?.isLoading == true
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
        return inventories.values.asSequence().map { it.inventory }.flatMap(Inventory::getViewers).toList()
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
        return inventories.values.any { it.inventory.viewers.contains(player) }
    }

    override suspend fun close() {
        super.close()
        mutex.withLock {
            inventories.values.forEach {
                it.job.apply {
                    cancel(GUIClosedException("The GUI is closing"))
                    join()
                }
                it.inventory.close()
            }
            inventories.clear()
        }
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
     * @param size Size of the inventory.
     * @return Flow of ItemStack to fill the inventory with.
     */
    protected abstract fun getItemStacks(key: T, size: Int): Flow<ItemStackIndex>

    /**
     * Get the coroutine scope to fill the inventory and the loading animation.
     * @param key Key to get the coroutine scope for.
     * @return The coroutine scope.
     */
    protected abstract suspend fun loadingScope(key: T): CoroutineScope
}
