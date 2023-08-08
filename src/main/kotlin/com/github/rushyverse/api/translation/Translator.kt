package com.github.rushyverse.api.translation

import com.github.rushyverse.api.extension.asComponent
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Get a translation using the provided arguments.
 * @receiver Translator The translator to use.
 * @param key Key in the bundle to find the translation for.
 * @param locale Language to translate to.
 * @param bundleName Name of the bundle to use, by default [Translator.defaultBundle].
 * @param modifier Modifier to apply to the component.
 * @return The translated string or the key if no translation was found in a [Component].
 */
public inline fun Translator.getComponent(
    key: String,
    locale: Locale,
    bundleName: String = defaultBundle,
    modifier: Component.() -> Component = { this }
): Component = getComponent(key, locale, emptyArray(), bundleName, modifier)

/**
 * Get a translation using the provided arguments.
 * @receiver Translator The translator to use.
 * @param key Key in the bundle to find the translation for.
 * @param locale Language to translate to.
 * @param arguments Arguments to format the translation with.
 * @param bundleName Name of the bundle to use, by default [Translator.defaultBundle].
 * @param modifier Modifier to apply to the component.
 * @return The translated string or the key if no translation was found in a [Component].
 */
public inline fun Translator.getComponent(
    key: String,
    locale: Locale,
    arguments: Array<Any> = emptyArray(),
    bundleName: String = defaultBundle,
    modifier: Component.() -> Component = { this }
): Component = get(key, locale, arguments, bundleName).asComponent(modifier = modifier)

/**
 * Translation provider interface, in charge of taking string keys and returning translated strings.
 * @property defaultBundle The default bundle to use for translations.
 */
public abstract class Translator(public val defaultBundle: String) {

    /**
     * Get a translation using the provided arguments.
     * @param key Key in the bundle to find the translation for.
     * @param locale Language to translate to.
     * @param bundleName Name of the bundle to use, by default [defaultBundle].
     * @return The translated string or the key if no translation was found.
     */
    public fun get(
        key: String,
        locale: Locale,
        bundleName: String = defaultBundle
    ): String = get(key, locale, emptyArray(), bundleName)

    /**
     * Get a formatted translation using the provided arguments.
     * @param key Key in the bundle to find the translation for.
     * @param locale Language to translate to.
     * @param arguments Arguments to format the translation with.
     * @param bundleName Name of the bundle to use, by default [defaultBundle].
     * @return The translated string or the key if no translation was found.
     */
    public abstract fun get(
        key: String,
        locale: Locale,
        arguments: Array<Any> = emptyArray(),
        bundleName: String = defaultBundle
    ): String
}
