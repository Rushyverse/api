package com.github.rushyverse.api.utils

import java.net.ServerSocket
import java.util.*

private val stringGenerator = generateSequence { UUID.randomUUID().toString() }.distinct().iterator()

fun randomString() = stringGenerator.next()

fun getAvailablePort(): Int {
    return ServerSocket(0).use {
        it.localPort
    }
}