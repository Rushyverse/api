package com.github.rushyverse.api.game.team

import com.github.rushyverse.api.APIPlugin.Companion.BUNDLE_API
import com.github.rushyverse.api.translation.SupportedLanguage
import com.github.rushyverse.api.translation.TranslationProvider
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
        translationProvider: TranslationProvider,
        locale: Locale = SupportedLanguage.ENGLISH.locale
    ): String = translationProvider.translate("team.${name}", locale, BUNDLE_API)

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
