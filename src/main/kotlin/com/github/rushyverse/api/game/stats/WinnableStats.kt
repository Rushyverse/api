package com.github.rushyverse.api.game.stats

public open class WinnableStats(
    public var wins: Int = 0,
    public var loses: Int = 0
) : Stats {

    override fun calculateScore(): Int {
        val score = wins - loses
        if (score < 0)
            return 0
        return score
    }
}