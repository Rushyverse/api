package com.github.rushyverse.api.player

import com.github.rushyverse.api.delegate.DelegatePlayer
import com.github.rushyverse.api.extension.asComponent
import com.github.rushyverse.api.koin.inject
import com.github.rushyverse.api.player.exception.PlayerNotFoundException
import com.github.rushyverse.api.player.scoreboard.ScoreboardManager
import com.github.rushyverse.api.translation.SupportedLanguage
import fr.mrmicky.fastboard.adventure.FastBoard
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.entity.Player
import java.util.*

/**
 * Client to store and manage data about player.
 * @property playerUUID Player's uuid.
 * @property player Player linked to the client.
 */
public open class Client(
    public val playerUUID: UUID,
    coroutineScope: CoroutineScope,
    /**
     * The current language of the player.
     */
    public var lang: SupportedLanguage = SupportedLanguage.ENGLISH
) : CoroutineScope by coroutineScope {

    private val scoreboardManager: ScoreboardManager by inject()

    public val player: Player? by DelegatePlayer(playerUUID)

    /**
     * Retrieve the instance of player.
     * If the player is not found from the server, thrown an exception.
     * @return The instance of player.
     */
    public fun requirePlayer(): Player =
        player ?: throw PlayerNotFoundException("The player cannot be retrieved from the server")

    /**
     * Send a message to the player.
     * @param text The message as component.
     */
    public fun send(text: Component): Unit = requirePlayer().sendMessage(text)

    /**
     * Send a message to the player.
     * The message will be converted to [Component] with the standard [TagResolver] of MiniMessage.
     * ```
     *  // Example
     *  send("<green>Hello <rainbow>$playerName")
     * ```
     *
     * @param message The string message.
     */
    public fun send(message: String): Unit = send(message.asComponent())


    /**
     * Retrieve the scoreboard of the player.
     * The scoreboard will be created if it doesn't exist.
     * @return The scoreboard of the player.
     */
    public suspend fun scoreboard(): FastBoard = scoreboardManager.getOrCreate(requirePlayer())

}
