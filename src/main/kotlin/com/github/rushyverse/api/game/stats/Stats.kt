package com.github.rushyverse.api.game.stats

/**
 * A functional interface that represents game statistics.
 * Implementing classes/entities are expected to provide a mechanism
 * to calculate a score based on their specific game-related stats.
 */
public fun interface Stats {

    /**
     * Calculates the score based on the implementing class's/game entity's statistics.
     *
     * @return The calculated score as an integer value.
     */
    public fun calculateScore(): Int
}
