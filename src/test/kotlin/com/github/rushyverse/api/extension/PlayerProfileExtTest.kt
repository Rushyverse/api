package com.github.rushyverse.api.extension

import be.seeseemelk.mockbukkit.MockBukkit
import com.github.rushyverse.api.utils.randomString
import io.kotest.matchers.shouldBe
import org.bukkit.Bukkit
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class PlayerProfileExtTest {

    @BeforeTest
    fun onBefore() {
        MockBukkit.mock()
    }

    @AfterTest
    fun onAfter() {
        MockBukkit.unmock()
    }

    @Test
    fun `should define textures property`() {
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val skin = randomString()
        val signature = randomString()
        profile.setTextures(skin, signature)

        profile.properties.find { it.name == "textures" }!!.let {
            it.value shouldBe skin
            it.signature shouldBe signature
        }
    }

    @Test
    fun `should get textures property`() {
        val profile = Bukkit.createProfile(UUID.randomUUID())
        val skin = randomString()
        val signature = randomString()
        profile.setTextures(skin, signature)

        profile.getTexturesProperty()!!.let {
            it.value shouldBe skin
            it.signature shouldBe signature
        }
    }
}
