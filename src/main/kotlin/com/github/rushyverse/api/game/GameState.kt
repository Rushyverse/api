package com.github.rushyverse.api.game

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Represents the various states a game can be in at any given moment.
 * Each state corresponds to a different phase in the game's lifecycle.
 */
public enum class GameState(
    public val color: NamedTextColor,
    public val miniColor: String,
) {

    NOT_STARTED(NamedTextColor.BLUE, "<blue>"),

    /**
     * Represents the state where the game is waiting for necessary conditions to start.
     * This could be waiting for more players to join, or waiting for some setup process to finish.
     */
    WAITING(NamedTextColor.GOLD, "<gold>"),

    /**
     * Represents the state when the game is in the process of starting.
     * This is a transitional phase, initialization of game resources,
     * a countdown timer before the game starts, etc.
     */
    STARTING(NamedTextColor.YELLOW, "<yellow>"),

    /**
     * Represents the state where the game has officially started.
     * Gameplay is active during this state.
     */
    STARTED(NamedTextColor.GREEN, "<green>"),

    /**
     * Represents the state when the game is in the process of ending.
     * This is a transitional phase, where final scores might be calculated, game resources might be cleaned up, etc.
     */
    ENDED(NamedTextColor.RED, "<red>");
}
