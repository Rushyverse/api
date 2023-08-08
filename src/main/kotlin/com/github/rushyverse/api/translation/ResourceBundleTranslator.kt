package com.github.rushyverse.api.translation

import com.ibm.icu.text.MessageFormat
import mu.KotlinLogging
import java.util.*

/**
 * Loads the [ResourceBundle] called [bundleName] for all supported locales from [SupportedLanguage].
 * @see ResourceBundleTranslator.registerResourceBundle
 */
public fun ResourceBundleTranslator.registerResourceBundleForSupportedLocales(
    bundleName: String,
    loader: (String, Locale) -> ResourceBundle
) {
    SupportedLanguage.entries.forEach {
        registerResourceBundle(bundleName, it.locale, loader)
    }
}

private val logger = KotlinLogging.logger { }

/**
 * Translation provider backed by Java's [ResourceBundle]s. This makes use of `.properties` files that are standard
 * across the Java ecosystem.
 */
public open class ResourceBundleTranslator(defaultBundle: String) : Translator(defaultBundle) {

    private val bundles: MutableMap<Pair<String, Locale>, ResourceBundle> = mutableMapOf()

    override fun translate(
        key: String,
        locale: Locale,
        arguments: Array<Any>,
        bundleName: String
    ): String {
        val string = try {
            getBundle(locale, bundleName).getString(key)
        } catch (e: MissingResourceException) {
            logger.error("Unable to find translation for key '$key' in bundles: '$bundleName'", e)
            return key
        }

        return MessageFormat(string, locale).format(arguments)
    }

    /**
     * Retrieve the registered bundle or load it.
     * @param locale Locale.
     * @param bundleName Name of the bundle.
     * @return The loaded instance of [ResourceBundle].
     */
    protected open fun getBundle(locale: Locale, bundleName: String): ResourceBundle {
        return bundles[createKey(bundleName, locale)]
            ?: throw ResourceBundleNotRegisteredException(bundleName, locale)
    }

    /**
     * Loads the [ResourceBundle] called [bundleName] for [locale] and register it in [bundles]
     * @param bundleName Name of the bundle.
     * @param locale Locale.
     * @return The loaded instance of [ResourceBundle].
     */
    public fun registerResourceBundle(
        bundleName: String,
        locale: Locale,
        loader: (String, Locale) -> ResourceBundle
    ): ResourceBundle {
        logger.info("Getting bundle $bundleName for locale $locale")
        return loader(bundleName, locale).apply {
            bundles[createKey(bundleName, locale)] = this
        }
    }

    /**
     * Create the key to retrieve a bundle.
     * @param bundleName Name of the bundle.
     * @param locale Locale.
     * @return The key created.
     */
    private fun createKey(bundleName: String, locale: Locale) = bundleName to locale
}
