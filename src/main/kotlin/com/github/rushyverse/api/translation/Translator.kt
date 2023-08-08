package com.github.rushyverse.api.translation

import java.util.*

/**
 * Translation provider interface, in charge of taking string keys and returning translated strings.
 * @property defaultBundle The default bundle to use for translations.
 */
public abstract class Translator(public val defaultBundle: String) {

    /**
     * Get a formatted translation using the provided arguments.
     */
    public abstract fun translate(
        key: String,
        locale: Locale,
        bundleName: String = defaultBundle,
        arguments: Array<Any> = emptyArray()
    ): String
}
