package com.github.rushyverse.api.image

import kotlinx.coroutines.test.runTest
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.metadata.other.ItemFrameMeta
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.MapMeta
import net.minestom.server.utils.Rotation
import net.minestom.testing.Env
import net.minestom.testing.EnvTest
import org.junit.jupiter.api.Nested
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@EnvTest
class MapImageTest {

    @Nested
    inner class CreateItemFrames {

        @Test
        fun `should create one item frame if image is 128x128 max`(env: Env) = runTest {
            val instance = env.createFlatInstance()
            (1..128).forEach { width ->
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB))
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
            mapImage.buildPacketsFromImage(BufferedImage(129, 128, BufferedImage.TYPE_INT_ARGB))
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
            mapImage.buildPacketsFromImage(BufferedImage(128, 129, BufferedImage.TYPE_INT_ARGB))
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
            mapImage.buildPacketsFromImage(BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB))
            mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)
            assertEquals(instance, mapImage.itemFrames!!.first().instance)
        }

        @Nested
        inner class Position {

            @Test
            fun `should spawn item frame at the target position`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val orientation = ItemFrameMeta.Orientation.NORTH
                val math = MapImageMath.getFromOrientation(orientation)
                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)

                repeat(10) { x ->
                    repeat(10) { y ->
                        repeat(10) { z ->
                            val mapImage = MapImage()
                            mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)
                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)

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
                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)

                ItemFrameMeta.Orientation.values().forEach { orientation ->
                    val mapImage = MapImage()
                    mapImage.buildPacketsFromImage(image)
                    mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation)
                    val itemFrame = mapImage.itemFrames!!.first()
                    assertEquals(orientation, (itemFrame.entityMeta as ItemFrameMeta).orientation)
                }
            }

            @Test
            fun `should spawn item frame with invisibility by default`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
                mapImage.createItemFrames(instance, Pos(0.0, 0.0, 0.0), orientation = ItemFrameMeta.Orientation.NORTH)
                val itemFrame = mapImage.itemFrames!!.first()
                assertEquals(true, itemFrame.isInvisible)
            }

            @Test
            fun `should spawn item frame with map item in meta`(env: Env) = runTest {
                val instance = env.createFlatInstance()
                val image = BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
                val image = BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)

                val mapImage = MapImage()
                mapImage.buildPacketsFromImage(image)
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
    }
}