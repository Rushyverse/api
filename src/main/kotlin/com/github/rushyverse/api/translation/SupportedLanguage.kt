package com.github.rushyverse.api.translation

import java.util.*

/**
 * Enumerates the set of languages supported for translation.
 *
 * Each entry in this enum represents a specific language and locale combination,
 * providing both a human-readable display name and a [Locale] object for internal use.
 */
public enum class SupportedLanguage(
    /**
     * A human-readable display name for the language.
     */
    public val displayName: String,

    /**
     * Locale object representing the language and country code combination.
     * This locale can be used with localization and internationalization libraries
     * to format and retrieve translations.
     */
    public val locale: Locale
) {
    /**
     * English language for the United Kingdom.
     */
    ENGLISH("English", Locale("en", "gb")),

    /**
     * French language for France.
     */
    FRENCH("Français", Locale("fr", "fr")),

    /**
     * Spanish language for Spain.
     */
    SPANISH("Español", Locale("es", "es")),

    /**
     * German language for Germany.
     */
    GERMAN("Deutsch", Locale("de", "de")),

    /**
     * Chinese language for China.
     */
    CHINESE("Chinese", Locale("zh", "ch"))
}
