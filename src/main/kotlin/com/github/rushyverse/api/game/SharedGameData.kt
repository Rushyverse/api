package com.github.rushyverse.api.game

/**
 * Represents a shared container for game data across multiple games.
 * It provides utility functions to fetch player count, game state, and
 * allows listeners to be notified of changes in the game data.
 */
public class SharedGameData {

    /**
     * A list to hold the data for all active games.
     */
    public val games: MutableList<GameData> = mutableListOf()

    // A set of listeners that get called when there's a change in the game data.
    private val onChange: MutableSet<() -> Unit> = mutableSetOf()

    /**
     * Calculates the total number of players across all games.
     *
     * @return Total number of players.
     */
    public fun players(): Int = games.sumOf { it.players }

    /**
     * Calculates the total number of players in a specific type of game.
     *
     * @param gameType The type of game to filter by.
     * @return Total number of players for the specified game type.
     */
    public fun players(gameType: String): Int = games.filter { it.type == gameType }.sumOf { it.players }

    /**
     * Retrieves the number of players in a specific game identified by its type and ID.
     *
     * @param gameType The type of game.
     * @param gameId The unique ID of the game.
     * @return Number of players in the specified game, or 0 if the game is not found.
     */
    public fun players(gameType: String, gameId: Int): Int =
        games.firstOrNull { it.type == gameType && it.id == gameId }?.players ?: 0

    /**
     * Retrieves the state of a specific game identified by its type and ID.
     *
     * @param gameType The type of game.
     * @param gameId The unique ID of the game.
     * @return GameState of the specified game, or GameState.WAITING if the game is not found.
     */
    public fun state(gameType: String, gameId: Int): GameState =
        games.distinctBy { it.type == gameType }.firstOrNull { it.id == gameId }?.state ?: GameState.WAITING

    /**
     * Counts the number of games for a specific type.
     *
     * @param gameType The type of game to filter by.
     * @return The count of games of the specified type.
     */
    public fun games(gameType: String): Int = games.filter { it.type == gameType }.size

    /**
     * Subscribes a listener that gets called when the game data changes.
     *
     * @param unit The callback function to be invoked upon changes.
     */
    public fun subscribeOnChange(unit: () -> Unit) {
        onChange.add(unit)
    }

    /**
     * Invokes all the registered listeners to notify about a change in game data.
     */
    public fun callOnChange() {
        onChange.forEach { it.invoke() }
    }

    /**
     * Updates an existing game's data or adds a new game to the list.
     * After, it notifies all registered listeners about the change.
     *
     * @param gameData The game data to be updated or added.
     */
    public fun saveUpdate(gameData: GameData) {
        val index = games.indexOf(gameData)
        if (index == -1) {
            games.add(gameData)
        } else {
            games[index] = gameData
        }

        callOnChange()
    }
}
