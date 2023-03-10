package com.github.rushyverse.api.image

import com.github.rushyverse.api.image.exception.ImageAlreadyLoadedException
import com.github.rushyverse.api.image.exception.ImageNotLoadedException
import com.github.rushyverse.api.image.exception.ItemFramesAlreadyExistException
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.metadata.other.ItemFrameMeta
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.MapMeta
import net.minestom.server.network.packet.server.SendablePacket
import net.minestom.server.network.packet.server.play.MapDataPacket
import net.minestom.server.utils.Rotation
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import kotlin.test.*

class MapImageTest {

    companion object {
        private const val BLACK_COLOR_PACKET = 119.toByte()
    }

    @Nested
    @EnvTest
    inner class CreateItemFrames {

        @Nested
        inner class Position {

            @Test
            fun `should spawn item frame at the target position`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.NORTH
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)

                repeat(10) { x ->
                    repeat(10) { y ->
                        repeat(10) { z ->
                            val mapImage = MapImage()
                            mapImage.loadImageAsPackets(image)
                            mapImage.createItemFrames(
                                instance,
                                Pos(x.toDouble(), y.toDouble(), z.toDouble()),
                                orientation
                            )
                            assertEquals(
                                Pos(x.toDouble(), y.toDouble(), z.toDouble(), math.yaw, math.pitch),
                                mapImage.itemFrames!!.first().position
                            )
                        }
                    }
                }
            }

            @Test
            fun `should spawn item by following the north orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.NORTH
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(-1.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(-1.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[3].position)
            }

            @Test
            fun `should spawn item by following the east orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.EAST
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(0.0, 0.0, -1.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(0.0, -1.0, -1.0, math.yaw, math.pitch), itemFrames[3].position)
            }

            @Test
            fun `should spawn item by following the south orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.SOUTH
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(1.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(1.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[3].position)
            }

            @Test
            fun `should spawn item by following the west orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.WEST
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(0.0, 0.0, 1.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, -1.0, 0.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(0.0, -1.0, 1.0, math.yaw, math.pitch), itemFrames[3].position)
            }

            @Test
            fun `should spawn item by following the up orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.UP
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(1.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, 0.0, 1.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(1.0, 0.0, 1.0, math.yaw, math.pitch), itemFrames[3].position)
            }

            @Test
            fun `should spawn item by following the down orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.DOWN
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)

                val itemFrames = mapImage.itemFrames!!
                assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[0].position)
                assertEquals(Pos(1.0, 0.0, 0.0, math.yaw, math.pitch), itemFrames[1].position)
                assertEquals(Pos(0.0, 0.0, -1.0, math.yaw, math.pitch), itemFrames[2].position)
                assertEquals(Pos(1.0, 0.0, -1.0, math.yaw, math.pitch), itemFrames[3].position)
            }
        }

        @Nested
        inner class MetaInformation {

            @Test
            fun `should custom meta of item frame if needed`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)

                val invisible = false
                val rotation = Rotation.values().random()
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH) {
                    this.isInvisible = false
                    this.rotation = rotation
                }

                val itemFrame = mapImage.itemFrames!!.first()
                val itemFrameMeta = itemFrame.entityMeta as ItemFrameMeta
                assertEquals(rotation, itemFrameMeta.rotation)
                assertEquals(invisible, itemFrameMeta.isInvisible)
            }

            @Test
            fun `should spawn item frame with the target orientation`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)

                ItemFrameMeta.Orientation.values().forEach { orientation ->
                    val mapImage = MapImage()
                    mapImage.loadImageAsPackets(image)
                    mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)
                    val itemFrame = mapImage.itemFrames!!.first()
                    assertEquals(orientation, (itemFrame.entityMeta as ItemFrameMeta).orientation)
                }
            }

            @Test
            fun `should spawn item frame with invisibility by default`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)
                val itemFrame = mapImage.itemFrames!!.first()
                assertEquals(true, itemFrame.isInvisible)
            }

            @Test
            fun `should spawn item frame with map item in meta`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)
                val itemFrame = mapImage.itemFrames!!.first()
                val meta = itemFrame.entityMeta as ItemFrameMeta

                val metaItem = meta.item
                assertNotNull(metaItem)
                assertEquals(Material.FILLED_MAP, metaItem.material())
            }

            @Test
            fun `should spawn item frame with map id`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(256, 256, TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)

                val itemFrames = mapImage.itemFrames
                assertNotNull(itemFrames)
                assertEquals(4, itemFrames.size)

                itemFrames.forEachIndexed { index, entity ->
                    val meta = entity.entityMeta as ItemFrameMeta
                    val metaItem = meta.item
                    val metaOfMetaItem = metaItem.meta(MapMeta::class.java)
                    assertEquals(index, metaOfMetaItem.mapId)
                }
            }

        }

        @Nested
        inner class WithImageNotLoaded {

            @Test
            fun `should throw exception if image is not loaded`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val mapImage = MapImage()
                val ex = assertThrows<ImageNotLoadedException> {
                    mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)
                }
                assertEquals("An image must be loaded before creating the item frames.", ex.message)
            }
        }

        @Nested
        inner class ItemFramesAlreadyExist {

            @Test
            fun `should throw exception if all item frames already exist`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)

                val ex = assertThrows<ItemFramesAlreadyExistException> {
                    mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)
                }

                assertEquals("The item frames are already present in the instance.", ex.message)
            }

            @Test
            fun `should throw exception if at least one item frames already exist`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(1024, 1024, TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)

                val itemFrames = mapImage.itemFrames
                assertNotNull(itemFrames)
                itemFrames.drop(1).forEach { it.remove() }
                assertTrue { itemFrames.drop(1).all { it.isRemoved } }
                assertFalse { itemFrames.first().isRemoved }

                val ex = assertThrows<ItemFramesAlreadyExistException> {
                    mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)
                }

                assertEquals("The item frames are already present in the instance.", ex.message)
            }

        }

        @Test
        fun `should throw exception when image size is 0x0`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            val image = BufferedImage(1, 1, TYPE_INT_ARGB)
            mapImage.loadImageAsPackets(image)

            val spyMapImage = spyk(mapImage) {
                every { itemFramesPerLine } returns 0
                every { itemFramesPerColumn } returns 0
            }
            val returnedFrame =
                spyMapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)
            assertTrue { returnedFrame.isEmpty() }
            assertTrue { spyMapImage.itemFrames!!.isEmpty() }
        }

        @Test
        fun `should return and set the property of item frames`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            val image = BufferedImage(512, 1024, TYPE_INT_ARGB)
            mapImage.loadImageAsPackets(image)

            val returnedFrame = mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)
            assertEquals(returnedFrame, mapImage.itemFrames)
        }

        @Test
        fun `should create one item frame if image is between 1x1 and 128x128`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            (1..128).forEach { width ->
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(BufferedImage(width, width, TYPE_INT_ARGB))
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)

                val itemFrames = mapImage.itemFrames
                assertNotNull(itemFrames)
                assertEquals(1, itemFrames.size)
            }
        }

        @Test
        fun `should create two item frame if image is 129x128`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            val orientation = ItemFrameMeta.Orientation.NORTH
            mapImage.loadImageAsPackets(BufferedImage(129, 128, TYPE_INT_ARGB))
            mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)
            val math = MapImageMath.getFromOrientation(orientation)

            val itemFrames = mapImage.itemFrames
            assertNotNull(itemFrames)
            assertEquals(2, itemFrames.size)

            val (first, second) = itemFrames
            assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), first.position)
            assertEquals(Pos(-1.0, 0.0, 0.0, math.yaw, math.pitch), second.position)
        }

        @Test
        fun `should create two item frame if image is 128x129`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            val orientation = ItemFrameMeta.Orientation.NORTH
            mapImage.loadImageAsPackets(BufferedImage(128, 129, TYPE_INT_ARGB))
            mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)
            val math = MapImageMath.getFromOrientation(orientation)

            val itemFrames = mapImage.itemFrames
            assertNotNull(itemFrames)
            assertEquals(2, itemFrames.size)

            val (first, second) = itemFrames
            assertEquals(Pos(0.0, 0.0, 0.0, math.yaw, math.pitch), first.position)
            assertEquals(Pos(0.0, -1.0, 0.0, math.yaw, math.pitch), second.position)
        }

        @Test
        fun `should spawn item frame at the target instance`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            mapImage.loadImageAsPackets(BufferedImage(128, 128, TYPE_INT_ARGB))
            mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)
            assertEquals(instance, mapImage.itemFrames!!.first().instance)
        }
    }

    @Nested
    inner class LoadImageAsPackets {

        @Nested
        inner class ItemFramesPerLine {

            @Test
            fun `should set property according to the width`() {
                fun assertWidthPixelWithItemFramesPerLine(widths: IntRange, expectedFramesPerLine: Int) {
                    widths.forEach {
                        val image = BufferedImage(it, 128, TYPE_INT_ARGB)
                        val mapImage = MapImage()
                        mapImage.loadImageAsPackets(image)
                        assertEquals(expectedFramesPerLine, mapImage.itemFramesPerLine)
                    }
                }
                assertWidthPixelWithItemFramesPerLine(1..128, 1)
                assertWidthPixelWithItemFramesPerLine(129..256, 2)
                assertWidthPixelWithItemFramesPerLine(257..384, 3)
                assertWidthPixelWithItemFramesPerLine(385..512, 4)
                assertWidthPixelWithItemFramesPerLine(513..640, 5)
                assertWidthPixelWithItemFramesPerLine(641..768, 6)
                assertWidthPixelWithItemFramesPerLine(769..896, 7)
                assertWidthPixelWithItemFramesPerLine(897..1024, 8)
            }

        }

        @Nested
        inner class ItemFramesPerColumn {

            @Test
            fun `should set property according to the height`() {
                fun assertHeightPixelWithItemFramesPerColumn(heights: IntRange, expectedFramesPerColumn: Int) {
                    heights.forEach {
                        val image = BufferedImage(128, it, TYPE_INT_ARGB)
                        val mapImage = MapImage()
                        mapImage.loadImageAsPackets(image)
                        assertEquals(expectedFramesPerColumn, mapImage.itemFramesPerColumn)
                    }
                }
                assertHeightPixelWithItemFramesPerColumn(1..128, 1)
                assertHeightPixelWithItemFramesPerColumn(129..256, 2)
                assertHeightPixelWithItemFramesPerColumn(257..384, 3)
                assertHeightPixelWithItemFramesPerColumn(385..512, 4)
                assertHeightPixelWithItemFramesPerColumn(513..640, 5)
                assertHeightPixelWithItemFramesPerColumn(641..768, 6)
                assertHeightPixelWithItemFramesPerColumn(769..896, 7)
                assertHeightPixelWithItemFramesPerColumn(897..1024, 8)
            }

        }

        @Test
        fun `should throw exception if an image is already loaded`() {
            val mapImage = MapImage()
            mapImage.loadImageAsPackets(BufferedImage(128, 128, TYPE_INT_ARGB))
            assertThrows<ImageAlreadyLoadedException> {
                mapImage.loadImageAsPackets(BufferedImage(128, 128, TYPE_INT_ARGB))
            }
        }

        @Test
        fun `should load map data packets`() {
            val mapImage = MapImage()
            mapImage.loadImageAsPackets(BufferedImage(1000, 1000, TYPE_INT_ARGB))

            val packets = assertNotNull(mapImage.packets)
            packets.forEachIndexed { index, packet ->
                assertTrue(packet is MapDataPacket)
                assertEquals(index, packet.mapId)
            }
        }

        @Test
        fun `should create one packet if the image is between 1x1 and 128x128`() {
            (1..128).forEach { width ->
                (1..128).forEach { height ->
                    val mapImage = MapImage()
                    mapImage.loadImageAsPackets(BufferedImage(width, height, TYPE_INT_ARGB))
                    val packets = assertNotNull(mapImage.packets)
                    assertEquals(1, packets.size)
                }
            }
        }

        @Test
        fun `should create two packets if the image width is between 129 and 256`() {
            (129..256).forEach { width ->
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(BufferedImage(width, 1, TYPE_INT_ARGB))
                val packets = assertNotNull(mapImage.packets)
                assertEquals(2, packets.size)
            }
        }

        @Test
        fun `should create two packets if the image height is between 129 and 256`() {
            (129..256).forEach { height ->
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(BufferedImage(1, height, TYPE_INT_ARGB))
                val packets = assertNotNull(mapImage.packets)
                assertEquals(2, packets.size)
            }
        }

        @Test
        fun `should create four packets if the image is between 129x129 and 256x256`() {
            fun assertNumberPackets(width: Int, height: Int, expectedNumberPackets: Int) {
                val mapImage = MapImage()
                mapImage.loadImageAsPackets(BufferedImage(width, height, TYPE_INT_ARGB))
                val packets = assertNotNull(mapImage.packets)
                assertEquals(expectedNumberPackets, packets.size)
            }
            assertNumberPackets(129, 129, 4)
            assertNumberPackets(256, 129, 4)
            assertNumberPackets(129, 256, 4)
            assertNumberPackets(200, 200, 4)
            assertNumberPackets(256, 256, 4)
        }

        @Test
        fun `should have same content than the image for one packet`() {
            val mapImage = MapImage()
            val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)

            val colors = listOf(
                Color.PINK.rgb to (-110).toByte(),
                Color.BLUE.rgb to (49).toByte(),
                Color.GREEN.rgb to (-122).toByte()
            )

            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val colorIndex = x % colors.size
                    image.setRGB(x, y, colors[colorIndex].first)
                }
            }

            mapImage.loadImageAsPackets(image)
            val packets = assertNotNull(mapImage.packets)
            assertEquals(1, packets.size)
            val packet = packets[0] as MapDataPacket

            val data = packet.colorContent!!.data
            assertEquals(128 * 128, data.size)

            for (y in 0 until 128) {
                for (x in 0 until 128) {
                    val colorIndex = x % colors.size
                    assertEquals(colors[colorIndex].second, data[y * 128 + x])
                }
            }
        }

        @Test
        fun `should have same content than the image for two packets`() {
            val mapImage = MapImage()
            val image = BufferedImage(256, 128, BufferedImage.TYPE_INT_RGB)

            val colorsFirstFrame = listOf(
                Color.PINK.rgb to (-110).toByte(),
                Color.BLUE.rgb to (49).toByte(),
                Color.GREEN.rgb to (-122).toByte()
            )

            val colorsSecondFrame = listOf(
                Color.DARK_GRAY.rgb to (85).toByte(),
                Color.YELLOW.rgb to (74).toByte(),
                Color.CYAN.rgb to (126).toByte()
            )

            // horizontal stripes
            for (y in 0 until image.height) {
                for (x in 0 until 128) {
                    val colorIndex = x % colorsFirstFrame.size
                    image.setRGB(x, y, colorsFirstFrame[colorIndex].first)
                }
            }

            // vertical stripes
            for (x in 128 until 256) {
                for (y in 0 until image.height) {
                    val colorIndex = x % colorsSecondFrame.size
                    image.setRGB(x, y, colorsSecondFrame[colorIndex].first)
                }
            }

            mapImage.loadImageAsPackets(image)
            val packets = assertNotNull(mapImage.packets)
            assertEquals(2, packets.size)

            val packet1 = packets[0] as MapDataPacket
            val data1 = packet1.colorContent!!.data
            assertTrue { data1.all { it != BLACK_COLOR_PACKET } }
            assertEquals(128 * 128, data1.size)

            for (y in 0 until 128) {
                for (x in 0 until image.height) {
                    val colorIndex = x % colorsFirstFrame.size
                    assertEquals(colorsFirstFrame[colorIndex].second, data1[y * 128 + x])
                }
            }

            val packet2 = packets[1] as MapDataPacket
            val data2 = packet2.colorContent!!.data
            assertTrue { data2.all { it != BLACK_COLOR_PACKET } }
            assertEquals(128 * 128, data2.size)

            for (x in 128 until 256) {
                for (y in 0 until image.height) {
                    val colorIndex = x % colorsSecondFrame.size
                    assertEquals(colorsSecondFrame[colorIndex].second, data2[y * 128 + (x - 128)])
                }
            }
        }

        @Test
        fun `should apply transformation on packets`() {
            val mapImage = MapImage()
            val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB)

            val colors = listOf(
                Color.PINK.rgb to (-110).toByte(),
                Color.BLUE.rgb to (49).toByte(),
                Color.GREEN.rgb to (-122).toByte()
            )

            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val colorIndex = x % colors.size
                    image.setRGB(x, y, colors[colorIndex].first)
                }
            }

            mapImage.loadImageAsPackets(image) {
                rotate(Math.toRadians(90.0), it.width / 2.0, it.height / 2.0)
            }

            val packets = assertNotNull(mapImage.packets)
            assertEquals(1, packets.size)
            val packet = packets[0] as MapDataPacket

            val data = packet.colorContent!!.data
            assertEquals(128 * 128, data.size)

            for (x in 0 until 128) {
                for (y in 0 until 128) {
                    // Use Y due to rotation
                    val colorIndex = y % colors.size
                    assertEquals(colors[colorIndex].second, data[y * 128 + x])
                }
            }
        }

        @Test
        fun `should load image from resources`() {
            val mapImage = MapImage()
            val packets = mapImage.loadImageAsPacketsFromResources("map_image.png")
            assertImagePacket(packets)
        }

        @Test
        fun `should load image from input stream`() {
            val mapImage = MapImage()
            MapImageTest::class.java.getResourceAsStream("/map_image.png")!!.buffered().use {
                val packets = mapImage.loadImageAsPacketsFromInputStream(it)
                assertImagePacket(packets)
            }
        }

        private fun assertImagePacket(packets: Array<SendablePacket>) {
            val colors = listOf(
                Color(84, 70, 162) to (23).toByte(),
                Color(55, 215, 61) to (-122).toByte(),
                Color(215, 55, 214) to (66).toByte(),
                Color(49, 61, 50) to (84).toByte(),
            )

            assertNotNull(packets)
            assertEquals(4, packets.size)
            packets.zip(colors).forEach { (packet, color) ->
                val data = (packet as MapDataPacket).colorContent!!.data
                data.forEach {
                    assertEquals(color.second, it)
                }
            }
        }

    }

    @Nested
    @EnvTest
    inner class RemoveItemFrames {

        @Test
        fun `should do nothing if no item frames`(env: Env) {
            val mapImage = MapImage()
            assertNull(mapImage.itemFrames)
            mapImage.removeItemFrames()
            assertNull(mapImage.itemFrames)
        }

        @Test
        fun `should remove item frames`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            val mapImage = MapImage()
            mapImage.loadImageAsPackets(BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB))
            mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), ItemFrameMeta.Orientation.NORTH)

            val frames = assertNotNull(mapImage.itemFrames)

            assertTrue { frames.all { !it.isRemoved } }
            mapImage.removeItemFrames()
            assertTrue { frames.all { it.isRemoved } }

            assertNull(mapImage.itemFrames)
        }
    }

    @Test
    fun `constant value should be correct`() {
        assertEquals(128, MapImage.MAP_ITEM_FRAME_PIXELS)
    }
}