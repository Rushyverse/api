package com.github.rushyverse.api.extension

import net.minestom.server.network.packet.server.play.PlayerInfoPacket.AddPlayer
import net.minestom.server.network.player.GameProfile

/**
 * Create a [property][GameProfile.Property] for textures.
 * @param textures Textures string.
 * @param signature Texture signature.
 * @return A property for textures.
 */
public fun GameProfileTextureProperty(textures: String, signature: String): GameProfile.Property =
    GameProfile.Property("textures", textures, signature)

/**
 * Create a [property][AddPlayer.Property] for textures.
 * @param textures Textures string.
 * @param signature Texture signature.
 * @return A property for textures.
 */
public fun AddPlayerTextureProperty(textures: String, signature: String): AddPlayer.Property =
    AddPlayer.Property("textures", textures, signature)