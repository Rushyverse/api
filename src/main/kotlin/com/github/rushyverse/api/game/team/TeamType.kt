package com.github.rushyverse.api.game.team

import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.Translator
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import java.util.*

/**
 * Enum that defines the supported team types for a game, each associated with a specific color.
 *
 * @property color The color associated with the team, represented by a `TextColor` object.
 */
public enum class TeamType(
    public val color: TextColor
) {

    WHITE(NamedTextColor.WHITE),
    RED(NamedTextColor.RED),
    BLUE(NamedTextColor.BLUE),
    GREEN(NamedTextColor.GREEN),
    YELLOW(NamedTextColor.YELLOW),
    PURPLE(NamedTextColor.LIGHT_PURPLE),
    AQUA(NamedTextColor.AQUA),
    ORANGE(NamedTextColor.GOLD),
    BLACK(NamedTextColor.BLACK);

    /**
     * Provides the translated name of the team based on the provided locale.
     *
     * @param translator The translation provider that fetches translations from a bundle of files.
     * @param locale The target locale for the translation, with a default of English.
     * @return The translated name of the team.
     */
    public fun name(
        translator: Translator,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String = translator.translate("team.${name.lowercase()}", locale, BUNDLE_API)
}
