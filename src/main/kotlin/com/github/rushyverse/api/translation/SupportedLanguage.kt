package com.github.rushyverse.api.translation

import java.util.*


/**
 * List of supported locales to translate keys.
 */
public enum class SupportedLanguage(public val displayName: String, public val locale: Locale) {

    ENGLISH("English", Locale("en", "gb")),
    FRENCH("Français", Locale("fr", "fr"))

}