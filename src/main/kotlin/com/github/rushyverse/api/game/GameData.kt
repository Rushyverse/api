package com.github.rushyverse.api.game

/**
 * Represents the data of a game.
 * This class can be used to communicate any game information across the framework.
 * @param type The type of the game.
 * @param id The id of the game.
 * @param players The players count in the game.
 * @param state The state of the game. It is [GameState.WAITING] by default.
 */
public data class GameData(
    val type: String,
    val id: Int,
    var players: Int = 0,
    var state: GameState = GameState.WAITING,
)
