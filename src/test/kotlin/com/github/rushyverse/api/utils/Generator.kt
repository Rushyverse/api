package com.github.rushyverse.api.utils

import org.bukkit.Location
import org.bukkit.World
import kotlin.random.Random

fun randomString(
    allowedChar: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9'),
    size: Int = 50
): String {
    return List(size) { allowedChar.random() }.joinToString("")
}

fun randomBoolean() = Random.nextBoolean()

fun randomInt(from: Int = Int.MIN_VALUE, until: Int = Int.MAX_VALUE) = Random.nextInt(from, until)

fun randomLong(from: Long = Long.MIN_VALUE, until: Long = Long.MAX_VALUE) = Random.nextLong(from, until)

fun randomFloat(from: Float = Float.MIN_VALUE, until: Float = Float.MAX_VALUE) =
    randomDouble(from.toDouble(), until.toDouble()).toFloat()

fun randomDouble(from: Double = Double.MIN_VALUE, until: Double = Double.MAX_VALUE) = Random.nextDouble(from, until)

inline fun <reified T: Enum<T>> randomEnum(): T {
    return enumValues<T>().random()
}

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
