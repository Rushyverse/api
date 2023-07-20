package com.github.rushyverse.api.game.team

import com.github.rushyverse.api.Plugin.Companion.BUNDLE_API
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationsProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import java.util.*

public enum class TeamType {

    WHITE,
    RED,
    BLUE,
    GREEN,
    YELLOW,
    PURPLE,
    AQUA,
    ORANGE,
    BLACK
    ;

    public fun name(
        translationsProvider: TranslationsProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String = translationsProvider.translate("team.${name}", locale, BUNDLE_API)

    public fun textName(
        translationsProvider: TranslationsProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): Component = text(name(translationsProvider, locale))

    public fun memberAdjective(
        translationsProvider: TranslationsProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String {
        val key = "team.${name}.member"
        val result = translationsProvider.translate(key, locale, BUNDLE_API)

        if (result == key) {
            return name(translationsProvider, locale)
        }

        return result
    }

    public fun textMemberAdjective(
        translationsProvider: TranslationsProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): Component = text(memberAdjective(translationsProvider, locale))
}