package com.github.rushyverse.api.extension

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty

/**
 * Name of the property to set and get textures.
 */
public const val PROPERTY_TEXTURES: String = "textures"

/**
 * Set a new texture for the profile.
 * @receiver Profile of a player.
 * @param skin Skin in string format.
 * @param signature Signature of the skin for validation.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun PlayerProfile.setTextures(skin: String, signature: String? = null) {
    setProperty(ProfileProperty(PROPERTY_TEXTURES, skin, signature))
}

/**
 * Find the property containing the textures' data.
 * @receiver Profile of a player.
 * @return The property with the name, `null` otherwise.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun PlayerProfile.getTexturesProperty(): ProfileProperty? =
    properties.find { it.name == PROPERTY_TEXTURES }
