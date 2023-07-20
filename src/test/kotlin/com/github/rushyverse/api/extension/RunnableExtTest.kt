package com.github.rushyverse.api.extension

import kotlin.test.Test
import kotlin.test.assertTrue

class RunnableExtTest {

    @Test
    fun `create bukkit runnable instance`() {
        var isCalled = false
        val runnable = BukkitRunnable {
            isCalled = true
        }
        runnable.run()
        assertTrue { isCalled }
    }
}