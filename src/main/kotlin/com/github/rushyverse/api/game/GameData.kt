package com.github.rushyverse.api.game

public data class GameData(
    val type: String,
    val id: Int,
    var players: Int = 0,
    var state: GameState = GameState.WAITING,
)