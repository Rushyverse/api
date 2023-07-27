package com.github.rushyverse.api.utils

import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.random.Random

val stringGenerator = generateSequence { UUID.randomUUID().toString() }.distinct().iterator()

fun randomString() = stringGenerator.next()

fun randomBoolean() = Random.nextBoolean()

fun randomInt() = Random.nextInt()

fun randomLong() = Random.nextLong()

fun randomFloat() = Random.nextFloat()

fun randomDouble() = Random.nextDouble()

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
