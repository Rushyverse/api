package com.github.rushyverse.api.game.team

import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import java.util.*

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
    BLACK(NamedTextColor.BLACK)
    ;

    public fun name(
        translationProvider: TranslationProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String = translationProvider.translate("team.${name.lowercase()}", locale, BUNDLE_API)

    public fun textName(
        translationProvider: TranslationProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): Component = text(name(translationProvider, locale))

    public fun memberAdjective(
        translationProvider: TranslationProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String {
        val key = "team.${name}.member"
        val result = translationProvider.translate(key, locale, BUNDLE_API)

        if (result == key) {
            return name(translationProvider, locale)
        }

        return result
    }

    public fun textMemberAdjective(
        translationProvider: TranslationProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): Component = text(memberAdjective(translationProvider, locale))
}
