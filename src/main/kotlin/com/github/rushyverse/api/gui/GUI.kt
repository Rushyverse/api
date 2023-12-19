package com.github.rushyverse.api.gui

import com.github.rushyverse.api.gui.load.InventoryLoadingAnimation
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.Client
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
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

private val logger = KotlinLogging.logger {}

/**
 * Exception concerning the GUI.
 */
public open class GUIException(message: String) : CancellationException(message)

/**
 * Exception thrown when the GUI is closed.
 */
public class GUIClosedException(message: String) : GUIException(message)

/**
 * Exception thrown when the GUI is updating.
 * @property client Client for which the GUI is updating.
 */
public class GUIUpdatedException(public val client: Client) :
    GUIException("GUI updating for client ${client.playerUUID}")

/**
 * Exception thrown when the GUI is closed for a specific client.
 * @property client Client for which the GUI is closed.
 */
public class GUIClosedForClientException(public val client: Client) :
    GUIException("GUI closed for client ${client.playerUUID}")

/**
 * GUI that can be shared by multiple players.
 * Only one inventory is created for all the viewers.
 * @property server Server.
 * @property manager Manager to register or unregister the GUI.
 */
public abstract class GUI<T>(
    private val loadingAnimation: InventoryLoadingAnimation<T>? = null,
    initialNumberInventories: Int = 16,
) {

    protected val server: Server by inject()

    protected val manager: GUIManager by inject()

    protected var inventories: MutableMap<T, InventoryData> = HashMap(initialNumberInventories)

    protected val mutex: Mutex = Mutex()

    /**
     * Get the key linked to the client to interact with the GUI.
     * @param client Client to get the key for.
     * @return The key.
     */
    protected abstract suspend fun getKey(client: Client): T

    /**
     * Get the coroutine scope to fill the inventory and the loading animation.
     * @param key Key to get the coroutine scope for.
     * @return The coroutine scope.
     */
    protected abstract suspend fun fillScope(key: T): CoroutineScope

    /**
     * Open the GUI for the client only if the GUI is not closed.
     * If the client has another GUI opened, close it.
     * If the client has the same GUI opened, do nothing.
     * @param client Client to open the GUI for.
     * @return True if the GUI was opened, false otherwise.
     */
    public open suspend fun open(client: Client): Boolean {
        val player = client.player
        if (player === null) {
            logger.warn { "Cannot open inventory for player ${client.playerUUID}: player is null" }
            return false
        }
        // If the player is dead, do not open the GUI because the interface cannot be shown to the player.
        if (player.isDead) return false

        val gui = client.gui()
        if (gui === this) return false

        // Here we don't need
        // to force to close the GUI because the GUI is closed when the player opens another inventory
        // (if not cancelled).

        val key = getKey(client)
        val inventory = getOrCreateInventory(key)

        // We open the inventory out of the mutex to avoid blocking operation from registered Listener.
        if (player.openInventory(inventory) == null) {
            // If the opening was cancelled (null returned),
            // We need to unregister the client from the GUI
            // and maybe close the inventory if it is individual.
            close(client, false)
            return false
        }

        return true
    }

    /**
     * Update the opened inventory for the client.
     *
     * If the client has the GUI opened, the inventory will be updated.
     * If the client has another GUI opened, do nothing.
     *
     * Call [getItems] to get the new items to fill the inventory.
     * @param client Client to update the inventory for.
     * @param interruptLoading If true and if the inventory is loading, the loading will be interrupted
     * to start a new loading animation.
     * @return True if the inventory was updated, false otherwise.
     * @see [getItems]
     */
    public open suspend fun update(client: Client, interruptLoading: Boolean = false): Boolean {
        val key = getKey(client)

        return mutex.withLock {
            val inventoryData = inventories[key] ?: return@withLock false

            // If the client doesn't have the GUI opened, do nothing.
            if (!unsafeContains(client)) return@withLock false

            if (inventoryData.isLoading) {
                // If we don't want to interrupt the loading and the inventory is loading, do nothing.
                if (!interruptLoading) return@withLock false
                else {
                    // If we want to interrupt the loading, we cancel the loading job.
                    // We need to wait for the job to be cancelled to avoid conflicts with the new loading animation.
                    inventoryData.job.apply {
                        cancel(GUIUpdatedException(client))
                        join()
                    }
                }
            }

            val inventory = inventoryData.inventory
            // Begin a new loading job and replace the old one.
            val newLoadingJob = startLoadingInventory(key, inventory)
            inventories[key] = InventoryData(inventory, newLoadingJob)
            true
        }
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
        val size = inventory.size
        // Empty the inventory, there is no effect if the inventory is new
        // But avoid conflicts with old items if the inventory is updated.
        inventory.contents = arrayOfNulls(size)

        // If no suspend operation is used in the flow, the fill will be done in the same thread & tick.
        // That's why we start with unconfined dispatcher.
        return fillScope(key).launch(Dispatchers.Unconfined) {
            val inventoryFlowItems = getItems(key, size).cancellable()

            if (loadingAnimation == null) {
                // Will fill the inventory bit by bit.
                inventoryFlowItems.collect { (index, item) -> inventory.setItem(index, item) }
            } else {
                val loadingAnimationJob = launch { loadingAnimation.loading(key, inventory) }

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

    /**
     * Create the inventory for the key.
     * @param key Key to create the inventory for.
     * @return New created inventory.
     */
    protected abstract fun createInventory(key: T): Inventory

    /**
     * Create a new flow of [Item][ItemStack] to fill the inventory with.
     * ```kotlin
     * flow {
     *   emit(0 to ItemStack(Material.STONE))
     *   delay(1.seconds) // simulate a suspend operation
     *   emit(1 to ItemStack(Material.DIRT))
     * }
     * ```
     * If the flow doesn't suspend the coroutine,
     * the inventory will be filled in the same tick & thread than during the creation of the inventory.
     * @param key Key to fill the inventory for.
     * @param size Size of the inventory.
     * @return Flow of [Item][ItemStack] with index.
     */
    protected abstract fun getItems(key: T, size: Int): Flow<ItemStackIndex>

    /**
     * Check if the GUI contains the inventory.
     * @param inventory Inventory to check.
     * @return True if the GUI contains the inventory, false otherwise.
     */
    public open suspend fun hasInventory(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.any { it.inventory == inventory }
        }
    }

    /**
     * Check if the inventory is loading.
     * @param inventory Inventory to check.
     * @return True if the inventory is loading (all the items are not loaded),
     * false if the inventory is loaded or not present in the GUI.
     */
    public open suspend fun isInventoryLoading(inventory: Inventory): Boolean {
        return mutex.withLock {
            inventories.values.firstOrNull { it.inventory == inventory }?.isLoading == true
        }
    }

    /**
     * Get the viewers of the GUI.
     * @return List of viewers.
     */
    public open suspend fun viewers(): Sequence<HumanEntity> {
        return mutex.withLock {
            unsafeViewers()
        }
    }

    /**
     * Get the viewers of the inventory.
     * This function is not thread-safe.
     * @return The viewers of the inventory.
     */
    protected open fun unsafeViewers(): Sequence<HumanEntity> {
        return inventories.values.asSequence().map { it.inventory }.flatMap(Inventory::getViewers)
    }

    /**
     * Check if the GUI contains the player.
     * @param client Client to check.
     * @return True if the GUI contains the player, false otherwise.
     */
    public open suspend fun contains(client: Client): Boolean {
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
        return unsafeViewers().any { it == player }
    }

    /**
     * Close the inventory.
     * The inventory will be closed for all the viewers.
     * The GUI will be removed from the listener and the [onClick] function will not be called anymore.
     */
    public open suspend fun close() {
        unregister()

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
     * Remove the client has a viewer of the GUI.
     * @param client Client to close the GUI for.
     * @param closeInventory If true, the interface will be closed, otherwise it will be kept open.
     * @return True if the inventory was closed, false otherwise.
     */
    public abstract suspend fun close(client: Client, closeInventory: Boolean = true): Boolean

    /**
     * Register the GUI to the listener.
     * If the GUI is already registered, do nothing.
     * If the GUI is closed, he will be opened again.
     * @return True if the GUI was registered, false otherwise.
     */
    public open suspend fun register(): Boolean {
        return manager.add(this)
    }

    /**
     * Unregister the GUI from the listener.
     * Should be called when the GUI is closed with [close].
     * @return True if the GUI was unregistered, false otherwise.
     */
    protected open suspend fun unregister(): Boolean {
        return manager.remove(this)
    }

    /**
     * Action to do when the client clicks on an item in the inventory.
     * @param client Client who clicked.
     * @param clickedItem Item clicked by the client cannot be null or [AIR][Material.AIR]
     * @param clickedInventory Inventory where the click was detected.
     * @param event Event of the click.
     */
    public abstract suspend fun onClick(
        client: Client,
        clickedInventory: Inventory,
        clickedItem: ItemStack,
        event: InventoryClickEvent
    )
}
