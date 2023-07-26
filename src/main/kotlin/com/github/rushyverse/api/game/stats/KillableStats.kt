package com.github.rushyverse.api.game.stats

public open class KillableStats(
    public var kills: Int = 0,
    public var deaths: Int = 0,

    ) : Stats {

    public override fun calculateScore(): Int {
        val score = kills - deaths
        if (score < 0)
            return 0
        return score
    }
}
