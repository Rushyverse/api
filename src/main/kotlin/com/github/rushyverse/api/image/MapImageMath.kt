package com.github.rushyverse.api.image

import net.minestom.server.entity.metadata.other.ItemFrameMeta

/**
 * This class is used to calculate the position of the map image.
 * The [yaw] and [pitch] values are used to fix the orientation of the item frame.
 * Check https://github.com/Minestom/Minestom/issues/760.
 * @property yaw Get the pitch for the orientation.
 * This is a workaround to fix the orientation of the item frame.
 * Without these values, the item frame will be rotated in another direction after several seconds.
 * @property pitch Get the pitch for the orientation.
 * This is a workaround to fix the orientation of the item frame.
 * Without these values, the item frame will be rotated in another direction after several seconds.
 */
public sealed interface MapImageMath {

    public companion object {

        /**
         * Link the item frame orientation to the [MapImageMath] instance.
         */
        private val orientations = mapOf(
            ItemFrameMeta.Orientation.DOWN to Down,
            ItemFrameMeta.Orientation.UP to Up,
            ItemFrameMeta.Orientation.NORTH to North,
            ItemFrameMeta.Orientation.SOUTH to South,
            ItemFrameMeta.Orientation.WEST to West,
            ItemFrameMeta.Orientation.EAST to East
        )

        /**
         * Get the [MapImageMath] linked to the orientation.
         * @param orientation The orientation of the item frame.
         * @return The [MapImageMath] for the orientation.
         */
        public fun getFromOrientation(orientation: ItemFrameMeta.Orientation): MapImageMath {
            return orientations[orientation] ?: throw IllegalArgumentException("Unsupported orientation: $orientation")
        }
    }

    public val yaw: Float
    public val pitch: Float

    /**
     * Compute the x position of the item frame.
     * @param beginX Initial x position.
     * @param frameNumber Number of the item frame.
     * @param itemFramesPerLine Number of blocks by line.
     * @return The x position of the item frame.
     */
    public fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int

    /**
     * Compute the y position of the item frame.
     * @param beginY Initial y position.
     * @param frameNumber Number of the item frame.
     * @param itemFramesPerLine Number of blocks by line.
     * @return The y position of the item frame.
     */
    public fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int

    /**
     * Compute the z position of the item frame.
     * @param beginZ Initial z position.
     * @param frameNumber Number of the item frame.
     * @param itemFramesPerLine Number of blocks by line.
     * @return The z position of the item frame.
     */
    public fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.DOWN].
     */
    public object Down : MapImageMath {
        override val yaw: Float = 0f
        override val pitch: Float = 90f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX + (frameNumber % itemFramesPerLine)

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ - (frameNumber / itemFramesPerLine)
    }

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.UP].
     */
    public object Up : MapImageMath {
        override val yaw: Float = 0f
        override val pitch: Float = 270f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX + (frameNumber % itemFramesPerLine)

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ + (frameNumber / itemFramesPerLine)
    }

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.NORTH].
     */
    public object North : MapImageMath {
        override val yaw: Float = 180f
        override val pitch: Float = 0f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX - (frameNumber % itemFramesPerLine)

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY - (frameNumber / itemFramesPerLine)

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ
    }

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.SOUTH].
     */
    public object South : MapImageMath {
        override val yaw: Float = 0f
        override val pitch: Float = 0f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX + (frameNumber % itemFramesPerLine)

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY - (frameNumber / itemFramesPerLine)

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ
    }

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.WEST].
     */
    public object West : MapImageMath {
        override val yaw: Float = 90f
        override val pitch: Float = 0f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY - (frameNumber / itemFramesPerLine)

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ + (frameNumber % itemFramesPerLine)
    }

    /**
     * Use to calculate the position of the item frame when the orientation is [ItemFrameMeta.Orientation.EAST].
     */
    public object East : MapImageMath {
        override val yaw: Float = 270f
        override val pitch: Float = 0f

        override fun computeX(beginX: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginX

        override fun computeY(beginY: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginY - (frameNumber / itemFramesPerLine)

        override fun computeZ(beginZ: Int, frameNumber: Int, itemFramesPerLine: Int): Int =
            beginZ - (frameNumber % itemFramesPerLine)
    }
}