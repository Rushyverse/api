package com.github.rushyverse.api.world

import org.bukkit.Location
import kotlin.math.pow
import kotlin.math.sqrt

public class CylinderArea(
    override var location: Location,
    radius: Double,
    public val limitY: ClosedRange<Double>,
) : Area {

    public var radius: Double = radius
        set(value) {
            verifyNewRadiusValue(value)
            field = value
        }

    init {
        verifyNewRadiusValue(radius)
    }

    /**
     * Verifies that the new radius value is greater than or equal to 0.0.
     * @param value New radius value.
     */
    private fun verifyNewRadiusValue(value: Double) {
        require(value >= 0.0) { "Radius must be greater than or equal to 0.0" }
    }

    override fun isInArea(location: Location): Boolean {
        val areaLocation = this.location
        return location.world === areaLocation.world // Same world
                && sqrt((location.x - areaLocation.x).pow(2.0) + (location.z - areaLocation.z).pow(2.0)) <= radius // Within radius
                && location.y in limitY // Within height
    }
}
