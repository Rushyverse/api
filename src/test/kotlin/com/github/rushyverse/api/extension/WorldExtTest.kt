package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.createRandomLocation
import com.github.rushyverse.api.utils.getRandomString
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import java.util.concurrent.CompletableFuture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorldExtTest {

    @Test
    fun `await chunk at with block`() = runBlocking {
        val world = mockk<World>(getRandomString())
        val slotBlock = slot<Block>()
        val slotGen = slot<Boolean>()

        val chunk = mockk<Chunk>(getRandomString())
        every { world.getChunkAtAsync(capture(slotBlock), capture(slotGen)) } returns CompletableFuture.completedFuture(
            chunk
        )

        val block = mockk<Block>(getRandomString())
        assertEquals(chunk, world.awaitChunkAt(block, true))
        assertEquals(block, slotBlock.captured)
        assertTrue { slotGen.captured }
        assertEquals(chunk, world.awaitChunkAt(block, false))
        assertFalse { slotGen.captured }
    }

    @Test
    fun `await chunk at with location`() = runBlocking {
        val world = mockk<World>(getRandomString())
        val slotLoc = slot<Location>()
        val slotGen = slot<Boolean>()

        val chunk = mockk<Chunk>(getRandomString())
        every { world.getChunkAtAsync(capture(slotLoc), capture(slotGen)) } returns CompletableFuture.completedFuture(
            chunk
        )

        val location = createRandomLocation()
        assertEquals(chunk, world.awaitChunkAt(location, true))
        assertEquals(location, slotLoc.captured)
        assertTrue { slotGen.captured }
        assertEquals(chunk, world.awaitChunkAt(location, false))
        assertFalse { slotGen.captured }
    }

    @Test
    fun `await chunk at with coord`() = runBlocking {
        val world = mockk<World>(getRandomString())
        val slotX = slot<Int>()
        val slotZ = slot<Int>()
        val slotGen = slot<Boolean>()
        val slotUrgent = slot<Boolean>()

        val chunk = mockk<Chunk>(getRandomString())
        every {
            world.getChunkAtAsync(
                capture(slotX),
                capture(slotZ),
                capture(slotGen),
                capture(slotUrgent)
            )
        } returns CompletableFuture.completedFuture(
            chunk
        )

        var x = 10
        var z = 20
        assertEquals(chunk, world.awaitChunkAt(x, z, gen = true, urgent = true))
        assertEquals(x, slotX.captured)
        assertEquals(z, slotZ.captured)
        assertTrue { slotGen.captured }
        assertTrue { slotUrgent.captured }

        x = 42
        z = 64
        assertEquals(chunk, world.awaitChunkAt(x, z, gen = false, urgent = false))
        assertEquals(x, slotX.captured)
        assertEquals(z, slotZ.captured)
        assertFalse { slotGen.captured }
        assertFalse { slotUrgent.captured }
    }
}
