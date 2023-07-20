package com.github.rushyverse.api.game

public class SharedGameData {

    public val games: MutableList<GameData> = mutableListOf()
    private val onChange: MutableSet<() -> Unit> = mutableSetOf()

    /**
     * Get the total of players into games
     */
    public fun players(): Int = games.sumOf { it.players }

    /**
     * Get the total of players into a specific type of games
     */
    public fun players(gameType: String): Int = games.filter { it.type == gameType }.sumOf { it.players }

    /**
     * Get the players of a specific game
     */
    public fun players(gameType: String, gameId: Int): Int =
        games.firstOrNull { it.type == gameType && it.id == gameId }?.players ?: 0

    /**
     * Get the state of a specific game
     */
    public fun state(gameType: String, gameId: Int): GameState =
        games.distinctBy { it.type == gameType }.firstOrNull { it.id == gameId }?.state ?: GameState.WAITING


    /**
     * Count all games of a specific type of game
     */
    public fun games(gameType: String): Int = games.filter { it.type == gameType }.size

    public fun subscribeOnChange(unit: () -> Unit) {
        onChange.add(unit)
    }

    public fun callOnChange() {
        onChange.forEach { it.invoke() }
    }

    public fun saveUpdate(gameData: GameData) {
        val index = games.indexOf(gameData)
        if (index == -1) {
            games.add(gameData)
        } else {
            games[index] = gameData
        }

        println("Shared memory data : $games")

        callOnChange()
    }
}