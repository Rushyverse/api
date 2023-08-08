package com.github.rushyverse.api.translation

import com.github.rushyverse.api.utils.randomString
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

private const val BUNDLE_NAME = "test_bundle"

private const val SECOND_BUNDLE_NAME = "test_bundle_2"

class ResourceBundleTranslatorTest {

    private lateinit var provider: ResourceBundleTranslator

    @BeforeTest
    fun onBefore() {
        provider = ResourceBundleTranslator(BUNDLE_NAME)
    }

    @Nested
    inner class RegisterResourceBundle {

        @Test
        fun `should load a resource bundle`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.get("test1", locale, bundleName = BUNDLE_NAME) shouldBe "english_value_1"
            provider.get("test2", locale, bundleName = BUNDLE_NAME) shouldBe "english_value_2"
        }

        @Test
        fun `should load a resource bundle for all supported locales`() {
            provider.registerResourceBundleForSupportedLocales(BUNDLE_NAME, ResourceBundle::getBundle)
            SupportedLanguage.entries.forEach {
                val displayName = it.displayName.lowercase()
                provider.get("test1", it.locale, bundleName = BUNDLE_NAME) shouldBe "${displayName}_value_1"
                provider.get("test2", it.locale, bundleName = BUNDLE_NAME) shouldBe "${displayName}_value_2"
            }
        }

        @Test
        fun `should load multiple resource bundles`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.registerResourceBundle(SECOND_BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.get("test1", locale, bundleName = BUNDLE_NAME) shouldBe "english_value_1"
            provider.get("simple_value", locale, bundleName = SECOND_BUNDLE_NAME) shouldBe "English value"
        }
    }

    @Nested
    inner class TranslateValue {

        @Test
        fun `should return the value for the given key`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get("test1", SupportedLanguage.ENGLISH.locale, bundleName = BUNDLE_NAME) shouldBe "english_value_1"
        }

        @Test
        fun `should return the value for the given key with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get(
                "test_args", SupportedLanguage.ENGLISH.locale, arrayOf(
                    "with arguments"
                ), BUNDLE_NAME
            ) shouldBe "english_value with arguments"
        }

        @Test
        fun `should return the key if the bundle is not registered`() {
            val locale = SupportedLanguage.ENGLISH.locale
            val ex = assertThrows<ResourceBundleNotRegisteredException> {
                provider.get("test1", locale, bundleName = BUNDLE_NAME)
            }
            ex.bundleName shouldBe BUNDLE_NAME
            ex.locale shouldBe locale
        }

        @Test
        fun `should return the key if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            provider.get(key, SupportedLanguage.ENGLISH.locale, bundleName = BUNDLE_NAME) shouldBe key
        }

        @Test
        fun `should return the key if the value is not defined for language with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            provider.get(key, SupportedLanguage.ENGLISH.locale, arrayOf("test"), BUNDLE_NAME) shouldBe key
        }

        @Test
        fun `should return the default value if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get("test_undefined", SupportedLanguage.ENGLISH.locale, bundleName = BUNDLE_NAME) shouldBe "default_value"
        }

        @Test
        fun `should return the value with template for args if no replacement args are defined`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get("test_args", SupportedLanguage.ENGLISH.locale, bundleName = BUNDLE_NAME) shouldBe "english_value {0}"
        }

        @Test
        fun `should return the value with plural syntax`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                arrayOf(2),
                BUNDLE_NAME
            ) shouldBe "Need 2 players."
        }

        @Test
        fun `should return the value with singular syntax`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)

            provider.get(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                arrayOf(1),
                BUNDLE_NAME
            ) shouldBe "Need 1 player."

            provider.get(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                arrayOf(0),
                BUNDLE_NAME
            ) shouldBe "Need 0 player."
        }

        @Test
        fun `should return the UTF-8 value`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.FRENCH.locale, ResourceBundle::getBundle)
            provider.get("test1", SupportedLanguage.FRENCH.locale, bundleName = BUNDLE_NAME) shouldBe "français_value_1"
        }

        @Test
        fun `should use default bundle if no bundle is specified`() {
            provider = ResourceBundleTranslator(BUNDLE_NAME)
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)

            provider.get("test1", SupportedLanguage.ENGLISH.locale) shouldBe "english_value_1"
            // Key not found in default bundle
            provider.get("simple_value", SupportedLanguage.ENGLISH.locale) shouldBe "simple_value"

            provider = ResourceBundleTranslator(SECOND_BUNDLE_NAME)
            provider.registerResourceBundle(
                SECOND_BUNDLE_NAME,
                SupportedLanguage.ENGLISH.locale,
                ResourceBundle::getBundle
            )

            provider.get("simple_value", SupportedLanguage.ENGLISH.locale) shouldBe "English value"
            // Key not found in default bundle
            provider.get("test1", SupportedLanguage.ENGLISH.locale) shouldBe "test1"
        }

    }
}
