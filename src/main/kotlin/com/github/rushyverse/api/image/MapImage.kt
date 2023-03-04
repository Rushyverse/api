package com.github.rushyverse.api.image

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.other.ItemFrameMeta
import net.minestom.server.instance.Instance
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.item.metadata.MapMeta
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer
import net.minestom.server.network.packet.server.SendablePacket
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.properties.Delegates

/**
 * A class that allows you to create an Image as Map Item Frame on the server.
 * @property instance The instance where the item frames will be created.
 * @property pos The position where the item frames will be created.
 * The position is the top left corner of the image.
 * @property orientation The orientation of the item frames.
 * @property resourceImageName The resource that used to be printed as map.
 * @property packets The packets list to send to new players.
 * @property blocksPerLine The width blocks size desired for the item frame. The value define the number of item frames by line.
 * @property blocksPerColumn The height blocks size desired for the item frame. The value define the number of item frames by column.
 * @property numberOfItemFrames The number of item frames needed to display the image.
 */
public class MapImage(
    public val instance: Instance,
    public val pos: Pos,
    public val orientation: ItemFrameMeta.Orientation,
    private val resourceImageName: String,
) {

    public companion object {
        /**
         * The number of pixels per item frame is 128.
         * So to improve the performance, we will use the bitwise operator to divide by 128.
         */
        private const val MAP_ITEM_FRAME_PIXELS_BITWISE = 7
    }

    public lateinit var packets: Array<SendablePacket>
        private set

    public var blocksPerLine: Int by Delegates.notNull()
        private set

    public var blocksPerColumn: Int by Delegates.notNull()
        private set

    private val numberOfItemFrames: Int
        get() = blocksPerLine * blocksPerColumn

    /**
     * Create the packets list to send to new players.
     * The image data are loaded from the resource file [resourceImageName].
     * Item frames are created at the position [pos] with the orientation [orientation] to display the image.
     * The result is stored in the [packets] property.
     * @param modifyTransform The function to apply transformation to the image. By default, the image is turned upside down.
     * For example, to rotate the image of 90° clockwise, you can use the following code:
     * ```
     * // 'this' is the AffineTransform instance.
     * // 'it' is the Image instance.
     * rotate(Math.toRadians(90.0), it.width / 2.0, it.height / 2.0)
     * ```
     * @return The packets list to send to new players.
     */
    public fun buildPackets(
        modifyTransform: AffineTransform.(BufferedImage) -> Unit = {}
    ): Array<SendablePacket> {
        val inputStream = MapImage::class.java.getResourceAsStream("/$resourceImageName")
            ?: error("Unable to retrieve the image $resourceImageName in resources.")

        val image = inputStream.buffered().use { ImageIO.read(it) }
        val imageWidth = image.width
        val imageHeight = image.height
        blocksPerLine = imageWidth shr MAP_ITEM_FRAME_PIXELS_BITWISE
        blocksPerColumn = imageHeight shr MAP_ITEM_FRAME_PIXELS_BITWISE

        val transform = AffineTransform.getScaleInstance(1.0, 1.0).apply {
            modifyTransform(image)
        }

        val framebuffer = LargeGraphics2DFramebuffer(imageWidth, imageHeight).apply {
            renderer.drawRenderedImage(image, transform)
        }

        createItemFrames(instance, pos, orientation)
        return createPackets(framebuffer).also { packets = it }
    }

    /**
     * Creates packets from the image.
     * @param framebuffer The frame buffer to convert as packets.
     * @return The list of packets.
     */
    private fun createPackets(framebuffer: LargeGraphics2DFramebuffer): Array<SendablePacket> {
        return Array(numberOfItemFrames) {
            val x = it % blocksPerLine
            val y = it / blocksPerLine
            framebuffer.createSubView(
                x shl MAP_ITEM_FRAME_PIXELS_BITWISE,
                y shl MAP_ITEM_FRAME_PIXELS_BITWISE
            ).preparePacket(it)
        }
    }

    /**
     * Create necessary item frames on which the image will be displayed.
     * @param instance The instance where you want to create the frame.
     * @param pos The position of the frame.
     */
    private fun createItemFrames(instance: Instance, pos: Pos, orientation: ItemFrameMeta.Orientation) {
        val imageMath = MapImageMath.getFromOrientation(orientation)
        val beginX = pos.blockX()
        val beginY = pos.blockY()
        val beginZ = pos.blockZ()
        val yaw = imageMath.yaw
        val pitch = imageMath.pitch

        repeat(numberOfItemFrames) { i ->
            // We need to calculate the position of the item frame.
            // The position is calculated from the top left corner of the image.
            // The item frames are place to the right and bottom of the beginning position.
            val x = imageMath.computeX(beginX, i, blocksPerLine)
            val y = imageMath.computeY(beginY, i, blocksPerLine)
            val z = imageMath.computeZ(beginZ, i, blocksPerLine)

            val itemFrame = Entity(EntityType.ITEM_FRAME)
            itemFrame.setInstance(instance, Pos(x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch))

            with(itemFrame.entityMeta as ItemFrameMeta) {
                setNotifyAboutChanges(false)
                this.orientation = orientation
                isInvisible = true
                item = ItemStack.builder(Material.FILLED_MAP).meta(MapMeta::class.java) { it.mapId(i) }.build()
                setNotifyAboutChanges(true)
            }
        }
    }
}