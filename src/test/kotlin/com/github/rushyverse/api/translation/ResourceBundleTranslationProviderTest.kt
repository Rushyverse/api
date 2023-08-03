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

class ResourceBundleTranslationProviderTest {

    private lateinit var provider: ResourceBundleTranslationProvider

    @BeforeTest
    fun onBefore() {
        provider = ResourceBundleTranslationProvider()
    }

    @Nested
    inner class RegisterResourceBundle {

        @Test
        fun `should load a resource bundle`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.get("test1", locale, BUNDLE_NAME) shouldBe "english_value_1"
            provider.get("test2", locale, BUNDLE_NAME) shouldBe "english_value_2"
        }

        @Test
        fun `should load a resource bundle for all supported locales`() {
            provider.registerResourceBundleForSupportedLocales(BUNDLE_NAME, ResourceBundle::getBundle)
            SupportedLanguage.entries.forEach {
                val displayName = it.displayName.lowercase()
                provider.get("test1", it.locale, BUNDLE_NAME) shouldBe "${displayName}_value_1"
                provider.get("test2", it.locale, BUNDLE_NAME) shouldBe "${displayName}_value_2"
            }
        }

        @Test
        fun `should load multiple resource bundles`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.registerResourceBundle(SECOND_BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.get("test1", locale, BUNDLE_NAME) shouldBe "english_value_1"
            provider.get("simple_value", locale, SECOND_BUNDLE_NAME) shouldBe "English value"
        }
    }

    @Nested
    inner class GetValue {

        @Test
        fun `should throw an exception if the bundle is not registered`() {
            val locale = SupportedLanguage.ENGLISH.locale
            val ex = assertThrows<ResourceBundleNotRegisteredException> {
                provider.get("test1", locale, BUNDLE_NAME)
            }
            ex.bundleName shouldBe BUNDLE_NAME
            ex.locale shouldBe locale
        }

        @Test
        fun `should throw an exception if the key is not found`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertThrows<MissingResourceException> {
                provider.get(randomString(), SupportedLanguage.ENGLISH.locale, BUNDLE_NAME)
            }
        }

        @Test
        fun `should return the value for the given key`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get("test1", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe "english_value_1"
        }

        @Test
        fun `should return the default value if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.get("test_undefined", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe "default_value"
        }
    }

    @Nested
    inner class TranslateValue {

        @Test
        fun `should return the value for the given key`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.translate("test1", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe "english_value_1"
        }

        @Test
        fun `should return the value for the given key with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.translate(
                "test_args", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, arrayOf(
                    "with arguments"
                )
            ) shouldBe "english_value with arguments"
        }

        @Test
        fun `should return the key if the bundle is not registered`() {
            val locale = SupportedLanguage.ENGLISH.locale
            val ex = assertThrows<ResourceBundleNotRegisteredException> {
                provider.translate("test1", locale, BUNDLE_NAME)
            }
            ex.bundleName shouldBe BUNDLE_NAME
            ex.locale shouldBe locale
        }

        @Test
        fun `should return the key if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            provider.translate(key, SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe key
        }

        @Test
        fun `should return the key if the value is not defined for language with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            provider.translate(key, SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, arrayOf("test")) shouldBe key
        }

        @Test
        fun `should return the default value if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.translate("test_undefined", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe "default_value"
        }

        @Test
        fun `should return the value with template for args if no replacement args are defined`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.translate("test_args", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME) shouldBe "english_value {0}"
        }

        @Test
        fun `should return the value with plural syntax`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            provider.translate(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                BUNDLE_NAME,
                arrayOf(2)
            ) shouldBe "Need 2 players."
        }

        @Test
        fun `should return the value with singular syntax`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)

            provider.translate(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                BUNDLE_NAME,
                arrayOf(1)
            ) shouldBe "Need 1 player."

            provider.translate(
                "test_plural",
                SupportedLanguage.ENGLISH.locale,
                BUNDLE_NAME,
                arrayOf(0)
            ) shouldBe "Need 0 player."
        }

        @Test
        fun `should return the UTF-8 value`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.FRENCH.locale, ResourceBundle::getBundle)
            provider.translate("test1", SupportedLanguage.FRENCH.locale, BUNDLE_NAME) shouldBe "français_value_1"
        }

    }
}
