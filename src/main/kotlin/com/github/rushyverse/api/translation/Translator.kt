package com.github.rushyverse.api.translation

import java.util.*

/**
 * Translation provider interface, in charge of taking string keys and returning translated strings.
 */
public interface Translator {

    /**
     * Get a formatted translation using the provided arguments.
     */
    public fun translate(
        key: String,
        locale: Locale,
        bundleName: String,
        arguments: Array<Any> = emptyArray()
    ): String
}
