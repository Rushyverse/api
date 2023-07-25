package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.getRandomString
import io.mockk.mockk
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataHolder
import kotlin.test.Test
import kotlin.test.assertTrue

class PersistentDataHolderTest {

    @Test
    fun `open data container and manage it`() {
        val container = mockk<PersistentDataContainer>(getRandomString())
        val holder = PersistentDataHolder { container }

        val isEquals = holder.dataContainer {
            this == container
        }
        assertTrue { isEquals }
    }

}
