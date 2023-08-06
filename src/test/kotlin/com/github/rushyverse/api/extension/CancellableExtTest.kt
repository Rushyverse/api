package com.github.rushyverse.api.extension

import com.github.rushyverse.api.extension.event.cancel
import io.kotest.matchers.shouldBe
import org.bukkit.event.Cancellable
import kotlin.test.Test

class CancellableExtTest {

    @Test
    fun `cancel() sets isCancelled to true`() {
        var setCancel: Boolean? = null
        val cancellable = object : Cancellable {
            override fun isCancelled(): Boolean {
                error("Should not be called")
            }

            override fun setCancelled(cancel: Boolean) {
                setCancel = cancel
            }

        }
        cancellable.cancel()
        setCancel shouldBe true
    }
}
