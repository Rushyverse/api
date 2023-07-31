package com.github.rushyverse.api.utils

import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.random.Random

val stringGenerator = generateSequence { UUID.randomUUID().toString() }.distinct().iterator()

fun randomString() = stringGenerator.next()

fun randomBoolean() = Random.nextBoolean()

fun randomInt(from: Int = Int.MIN_VALUE, until: Int = Int.MAX_VALUE) = Random.nextInt(from, until)

fun randomLong(from: Long = Long.MIN_VALUE, until: Long = Long.MAX_VALUE) = Random.nextLong(from, until)

fun randomFloat(from: Float = Float.MIN_VALUE, until: Float = Float.MAX_VALUE) =
    randomDouble(from.toDouble(), until.toDouble()).toFloat()

fun randomDouble(from: Double = Double.MIN_VALUE, until: Double = Double.MAX_VALUE) = Random.nextDouble(from, until)

const val LIMIT_RANDOM_COORDINATE = 1000.0

fun randomLocation(world: World? = null): Location {
    return Location(
        world,
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE).toFloat(),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE).toFloat()
    )
}
