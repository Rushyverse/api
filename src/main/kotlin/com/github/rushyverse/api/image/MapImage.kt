package com.github.rushyverse.api.image

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
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
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

/**
 * Read an image from the resources and build the packets to send to the players.
 * @see buildPacketsFromInputStream
 * @receiver Object to display image on the server.
 * @param resourceImage Path of the image in the resources.
 * @param loadImageCoroutineContext Coroutine context to load the image. We recommend to use Dispatchers.IO.
 * @param modifyTransform Function to modify the transform of the image.
 * @return The packets list to send to players.
 */
public suspend fun MapImage.buildPacketsFromResources(
    resourceImage: String,
    loadImageCoroutineContext: CoroutineContext = Dispatchers.IO,
    modifyTransform: AffineTransform.(BufferedImage) -> Unit = {}
): Array<SendablePacket> {
    val inputStream = withContext(loadImageCoroutineContext) {
        MapImage::class.java.getResourceAsStream("/$resourceImage")
            ?: error("Unable to retrieve the image $resourceImage in resources.")
    }

    return inputStream.buffered().use { buildPacketsFromInputStream(it, loadImageCoroutineContext, modifyTransform) }
}

/**
 * Read an image from an input stream and build the packets to send to the players.
 * **This method does not close the provided [inputStream] after the read operation has completed.
 * It is the responsibility of the caller to close the stream, if desired.**
 * @see [MapImage.buildPacketsFromImage]
 * @receiver Object to display image on the server.
 * @param inputStream Input stream to retrieve the image's data.
 * @param loadImageCoroutineContext Coroutine context to load the image. We recommend to use Dispatchers.IO.
 * @param modifyTransform Function to modify the transform of the image.
 * @return The packets list to send to players.
 */
public suspend fun MapImage.buildPacketsFromInputStream(
    inputStream: InputStream,
    loadImageCoroutineContext: CoroutineContext = Dispatchers.IO,
    modifyTransform: AffineTransform.(BufferedImage) -> Unit = {}
): Array<SendablePacket> {
    val image = withContext(loadImageCoroutineContext) {
        @Suppress("BlockingMethodInNonBlockingContext")
        ImageIO.read(inputStream)
    }
    return buildPacketsFromImage(image, modifyTransform)
}

/**
 * A class that allows you to create an Image as Map Item Frame on the server.
 * @property packets The packets list to send to new players.
 * @property itemFramesPerLine The width blocks size desired for the item frame. The value define the number of item frames by line.
 * @property itemFramesPerColumn The height blocks size desired for the item frame. The value define the number of item frames by column.
 * @property numberOfItemFrames The number of item frames needed to display the image.
 * @property isLoaded `true` if the image is loaded, `false` otherwise.
 * @property itemFrames The list of item frames created.
 */
public class MapImage {

    public companion object {

        /**
         * The number of pixels per item frame is 128x128.
         */
        private const val MAP_ITEM_FRAME_PIXELS = 128

        /**
         * The number of pixels per item frame is 128.
         * So to improve the performance, we will use the bitwise operator to divide by 128.
         */
        private const val MAP_ITEM_FRAME_PIXELS_BITWISE = 7
    }

    public lateinit var packets: Array<SendablePacket>
        private set

    public var itemFramesPerLine: Int by Delegates.notNull()
        private set

    public var itemFramesPerColumn: Int by Delegates.notNull()
        private set

    public val isLoaded: Boolean
        get() = ::packets.isInitialized

    public var itemFrames: List<Entity>? = null

    private val numberOfItemFrames: Int
        get() = itemFramesPerLine * itemFramesPerColumn

    /**
     * Create the packets list to send to new players.
     * The image data are loaded from the resource file [resourceImageName].
     * Item frames are created at the position [pos] with the orientation [orientation] to display the image.
     * The result is stored in the [packets] property.
     *
     * **This method does not close the provided [inputStream] after the read operation has completed.
     * It is the responsibility of the caller to close the stream, if desired.**
     *
     * @param modifyTransform The function to apply transformation to the image. By default, the image is turned upside down.
     * For example, to rotate the image of 90° clockwise, you can use the following code:
     * ```
     * // 'this' is the AffineTransform instance.
     * // 'it' is the Image instance.
     * rotate(Math.toRadians(90.0), it.width / 2.0, it.height / 2.0)
     * ```
     * @return The packets list to send to players.
     */
    public fun buildPacketsFromImage(
        image: BufferedImage,
        modifyTransform: AffineTransform.(BufferedImage) -> Unit = {}
    ): Array<SendablePacket> {
        require(!isLoaded) { "An image is already loaded using this instance." }

        val imageWidth = image.width
        val imageHeight = image.height
        // We need to round the value to the nearest integer.
        // For example :
        // If the image is 1x1, we need 1 item frame by line and 1 item frame by column.
        // If the image is 129x129, we need 2 item frames by line and 2 item frames by column.
        // If the  image is 129x128, we need 2 item frames by line and 1 item frame by column.
        itemFramesPerLine = (imageWidth + MAP_ITEM_FRAME_PIXELS - 1) ushr MAP_ITEM_FRAME_PIXELS_BITWISE
        itemFramesPerColumn = (imageHeight + MAP_ITEM_FRAME_PIXELS - 1) ushr MAP_ITEM_FRAME_PIXELS_BITWISE

        val transform = AffineTransform.getScaleInstance(1.0, 1.0).apply {
            modifyTransform(image)
        }

        val framebuffer = LargeGraphics2DFramebuffer(imageWidth, imageHeight).apply {
            renderer.drawRenderedImage(image, transform)
        }

        return createPackets(framebuffer).also { packets = it }
    }

    /**
     * Creates packets from the image.
     * @param framebuffer The frame buffer to convert as packets.
     * @return The list of packets.
     */
    private fun createPackets(framebuffer: LargeGraphics2DFramebuffer): Array<SendablePacket> {
        return Array(numberOfItemFrames) {
            val x = it % itemFramesPerLine
            val y = it / itemFramesPerLine
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
     * @param orientation The orientation of the frame.
     */
    public suspend fun createItemFrames(
        instance: Instance,
        pos: Pos,
        orientation: ItemFrameMeta.Orientation,
        metaModifier: ItemFrameMeta.() -> Unit = {
            isInvisible = true
        }
    ) {
        require(!atLeastOneItemFrameIsPresent()) { "The item frames are already created." }
        if(numberOfItemFrames == 0) {
            itemFrames = emptyList()
            return
        }

        val imageMath = MapImageMath.getFromOrientation(orientation)
        val beginX = pos.blockX()
        val beginY = pos.blockY()
        val beginZ = pos.blockZ()

        // Workaround to avoid unpredictable rotation of the item frames.
        val yaw = imageMath.yaw
        val pitch = imageMath.pitch

        itemFrames = (0..<numberOfItemFrames).map { numberOfFrame ->
            // We need to calculate the position of the item frame.
            // The position is calculated from the top left corner of the image.
            // The item frames are place to the right and bottom of the beginning position.
            val x = imageMath.computeX(beginX, numberOfFrame, itemFramesPerLine)
            val y = imageMath.computeY(beginY, numberOfFrame, itemFramesPerLine)
            val z = imageMath.computeZ(beginZ, numberOfFrame, itemFramesPerLine)

            Entity(EntityType.ITEM_FRAME).apply {
                with(entityMeta as ItemFrameMeta) {
                    setNotifyAboutChanges(false)

                    item = ItemStack.builder(Material.FILLED_MAP)
                        .meta(MapMeta::class.java) { it.mapId(numberOfFrame) }
                        .build()

                    this.orientation = orientation
                    metaModifier()

                    setNotifyAboutChanges(true)
                }

                setInstance(instance, Pos(x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)).await()
            }
        }
    }

    /**
     * Remove all item frames linked to the image.
     * Do nothing if the item frames are not present.
     * Will set the [itemFrames] property to `null`.
     * @see [Entity.remove]
     */
    public fun removeItemFrames() {
        val itemFrames = itemFrames ?: return
        itemFrames.forEach(Entity::remove)
        this.itemFrames = null
    }

    /**
     * Check if at least one item frame is present in the [instance].
     * @return `true` if at least one item frame is present, `false` otherwise.
     */
    private fun atLeastOneItemFrameIsPresent() = itemFrames?.any { it.isRemoved } == false
}