package com.github.rushyverse.api.game.stats

/**
 * Represents statistics associated with players that can be killed within a game.
 * These statistics include kill and death counts, and also provides a method to calculate the score based on them.
 */
public open class KillableStats(
    /**
     * The number of kills achieved by the entity/player.
     */
    public var kills: Int = 0,

    /**
     * The number of times the entity/player has been killed.
     */
    public var deaths: Int = 0
) : Stats {

    /**
     * Calculates the score based on the kills and deaths of the entity/player.
     * The score is determined by subtracting the number of deaths from kills.
     * If the resultant score is negative, it returns 0 (to avoid negative scores).
     *
     * @return Calculated score.
     */
    public override fun calculateScore(): Int {
        val score = kills - deaths
        if (score < 0)
            return 0
        return score
    }
}
