package com.github.rushyverse.api.player

import com.github.rushyverse.api.API
import fr.mrmicky.fastboard.FastBoard
import com.github.rushyverse.api.delegate.DelegatePlayer
import com.github.rushyverse.api.player.exception.PlayerNotFoundException
import com.github.rushyverse.api.translation.SupportedLanguage
import kotlinx.coroutines.CoroutineScope
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.entity.Player
import java.util.*

/**
 * Client to store and manage data about player.*
 * @property playerUUID Player's uuid.
 * @property player Player linked to the client.
 */
public open class Client(
    pluginId: String,
    public val playerUUID: UUID,
    coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope {

    public val player: Player? by DelegatePlayer(pluginId, playerUUID)

    public val fastBoard: FastBoard by lazy { API.getOrInitFastBoard(requirePlayer()) }

    public var lang: SupportedLanguage = SupportedLanguage.ENGLISH

    public val locale: Locale get() = lang.locale

    /**
     * Retrieve the instance of player.
     * If the player is not found from the server, thrown an exception.
     * @return The instance of player.
     */
    public fun requirePlayer(): Player =
        player ?: throw PlayerNotFoundException("The player cannot be retrieved from the server")

    public fun send(text: Component): Unit = requirePlayer().sendMessage(text)

    public fun send(message: String): Unit = send(text(message))

}