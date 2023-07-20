package com.github.rushyverse.api.utils

import org.bukkit.Location
import org.bukkit.World
import java.util.*
import kotlin.random.Random

val stringGenerator = generateSequence { UUID.randomUUID().toString() }.distinct().iterator()

fun getRandomString() = stringGenerator.next()

const val LIMIT_RANDOM_COORDINATE = 1000.0

fun createRandomLocation(world: World? = null): Location {
    return Location(
        world,
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE).toFloat(),
        Random.nextDouble(LIMIT_RANDOM_COORDINATE).toFloat()
    )
}