package com.github.rushyverse.api.game.stats

/**
 * Represents statistics associated with entities or players that participate in win-lose scenarios within a game.
 * These statistics include win and loss counts, and also provides a method to calculate the score based on them.
 */
public open class WinnableStats(
    /**
     * The number of times the player has won.
     */
    public var wins: Int = 0,
    /**
     * The number of times the player has lost.
     */
    public var loses: Int = 0
) : Stats {

    override fun calculateScore(): Int {
        val score = wins - loses
        if (score < 0)
            return 0
        return score
    }
}
