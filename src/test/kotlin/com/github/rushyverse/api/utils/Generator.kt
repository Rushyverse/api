package com.github.rushyverse.api.utils

import net.minestom.server.coordinate.Pos
import java.net.ServerSocket
import java.util.*
import kotlin.random.Random

private val stringGenerator = generateSequence { UUID.randomUUID().toString() }.distinct().iterator()

fun randomString() = stringGenerator.next()

private val intGenerator = generateSequence { Random.nextInt() }.distinct().iterator()

fun randomInt() = intGenerator.next()

private val posGenerator =
    generateSequence { Pos(Random.nextDouble(), Random.nextDouble(), Random.nextDouble()) }.distinct().iterator()

fun randomPos() = posGenerator.next()

fun getAvailablePort(): Int {
    return ServerSocket(0).use {
        it.localPort
    }
}