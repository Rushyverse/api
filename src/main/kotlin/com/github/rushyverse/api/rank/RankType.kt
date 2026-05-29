package com.github.rushyverse.api.rank

import net.kyori.adventure.text.format.NamedTextColor

public enum class RankType(
    public val displayName: String,
    public val color: NamedTextColor,
    public val weight: Int
) {
    // Staff
    ADMIN("Admin", NamedTextColor.RED, 100),
    MODO("Modo", NamedTextColor.GOLD, 90),

    // Joueurs
    ASTRO("Astro", NamedTextColor.LIGHT_PURPLE, 30),
    JEDI("Jedi", NamedTextColor.AQUA, 20),
    VIP("VIP", NamedTextColor.GREEN, 10),

    // Default
    PLAYER("Player", NamedTextColor.GRAY, 0)
}