package io.github.distractic.bukkit.api.extension

import kotlinx.coroutines.future.await
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block

/**
 * Requests a [Chunk] to be loaded at the given coordinates;
 *
 * This method makes no guarantee on how fast the chunk will load and will return the chunk when finished.
 *
 * You should use this method if you need a chunk but do not need it
 * immediately, and you wish to let the server control the speed
 * of chunk loads, keeping performance in mind.
 *
 * @receiver World where the chunk will be retrieved.
 * @param block Block into the chunk must be retrieved.
 * @param gen `true` to generate the chunk if necessary, `false` otherwise.
 * @return The chunk retrieved.
 */
public suspend fun World.awaitChunkAt(block: Block, gen: Boolean = true): Chunk =
    getChunkAtAsync(block, gen).await()

/**
 * Requests a [Chunk] to be loaded at the given coordinates;
 *
 * This method makes no guarantee on how fast the chunk will load and will return the chunk when finished.
 *
 * You should use this method if you need a chunk but do not need it
 * immediately, and you wish to let the server control the speed
 * of chunk loads, keeping performance in mind.
 *
 * @receiver World where the chunk will be retrieved.
 * @param location Location to load the corresponding chunk from.
 * @param gen `true` to generate the chunk if necessary, `false` otherwise.
 * @return The chunk retrieved.
 */
public suspend fun World.awaitChunkAt(location: Location, gen: Boolean = true): Chunk =
    getChunkAtAsync(location, gen).await()

/**
 * Requests a [Chunk] to be loaded at the given coordinates;
 *
 * This method makes no guarantee on how fast the chunk will load and will return the chunk when finished.
 *
 * You should use this method if you need a chunk but do not need it
 * immediately, and you wish to let the server control the speed
 * of chunk loads, keeping performance in mind.
 *
 * @receiver World where the chunk will be retrieved.
 * @param x X coord.
 * @param z Z coord.
 * @param gen `true` to generate the chunk if necessary, `false` otherwise.
 * @param urgent `true` if the load must have the priority, `false` otherwise.
 * @return The chunk retrieved.
 */
public suspend fun World.awaitChunkAt(x: Int, z: Int, gen: Boolean = true, urgent: Boolean = false): Chunk =
    getChunkAtAsync(x, z, gen, urgent).await()