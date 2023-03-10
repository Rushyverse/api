package com.github.rushyverse.api.extension

import com.github.rushyverse.api.utils.randomString
import net.minestom.server.network.packet.server.play.PlayerInfoPacket
import net.minestom.server.network.player.GameProfile
import kotlin.test.Test
import kotlin.test.assertEquals

class PropertyExtTest {

    @Test
    fun `should create a GameProfile property for textures`() {
        val textures = randomString()
        val signature = randomString()
        val expected = GameProfile.Property("textures", textures, signature)
        val actual = GameProfileTextureProperty(textures, signature)
        assertEquals(expected, actual)
    }

    @Test
    fun `should create an AddPlayer property for textures`() {
        val textures = randomString()
        val signature = randomString()
        val expected = PlayerInfoPacket.AddPlayer.Property("textures", textures, signature)
        val actual = AddPlayerTextureProperty(textures, signature)
        assertEquals(expected, actual)
    }
}