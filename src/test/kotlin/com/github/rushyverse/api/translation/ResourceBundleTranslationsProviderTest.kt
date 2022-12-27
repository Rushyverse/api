package com.github.rushyverse.api.translation

import com.github.rushyverse.api.utils.randomString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

private const val BUNDLE_NAME = "test_bundle"

private const val SECOND_BUNDLE_NAME = "test_bundle_2"

class ResourceBundleTranslationsProviderTest {

    private lateinit var provider: ResourceBundleTranslationsProvider

    @BeforeTest
    fun onBefore() {
        provider = ResourceBundleTranslationsProvider()
    }

    @Nested
    inner class RegisterResourceBundle {

        @Test
        fun `should load a resource bundle`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            assertEquals("english_value_1", provider.get("test1", locale, BUNDLE_NAME))
            assertEquals("english_value_2", provider.get("test2", locale, BUNDLE_NAME))
        }

        @Test
        fun `should load a resource bundle for all supported locales`() {
            provider.registerResourceBundleForSupportedLocales(BUNDLE_NAME, ResourceBundle::getBundle)
            SupportedLanguage.values().forEach {
                val displayName = it.displayName.lowercase()
                assertEquals("${displayName}_value_1", provider.get("test1", it.locale, BUNDLE_NAME))
                assertEquals("${displayName}_value_2", provider.get("test2", it.locale, BUNDLE_NAME))
            }
        }

        @Test
        fun `should load multiple resource bundles`() {
            val locale = SupportedLanguage.ENGLISH.locale
            provider.registerResourceBundle(BUNDLE_NAME, locale, ResourceBundle::getBundle)
            provider.registerResourceBundle(SECOND_BUNDLE_NAME, locale, ResourceBundle::getBundle)
            assertEquals("english_value_1", provider.get("test1", locale, BUNDLE_NAME))
            assertEquals("English value", provider.get("simple_value", locale, SECOND_BUNDLE_NAME))
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
            assertEquals(BUNDLE_NAME, ex.bundleName)
            assertEquals(locale, ex.locale)
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
            assertEquals("english_value_1", provider.get("test1", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME))
        }

        @Test
        fun `should return the default value if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals("default_value", provider.get("test_undefined", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME))
        }
    }

    @Nested
    inner class TranslateValue {

        @Test
        fun `should return the value for the given key`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals("english_value_1", provider.translate("test1", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME))
        }

        @Test
        fun `should return the value for the given key with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals(
                "english_value with arguments", provider.translate(
                    "test_args", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, arrayOf(
                        "with arguments"
                    )
                )
            )
        }

        @Test
        fun `should return the value for the given key with the given list arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals(
                "english_value with arguments", provider.translate(
                    "test_args", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, listOf(
                        "with arguments"
                    )
                )
            )
        }

        @Test
        fun `should return the key if the bundle is not registered`() {
            val locale = SupportedLanguage.ENGLISH.locale
            val ex = assertThrows<ResourceBundleNotRegisteredException> {
                assertEquals("test1", provider.translate("test1", locale, BUNDLE_NAME))
            }
            assertEquals(BUNDLE_NAME, ex.bundleName)
            assertEquals(locale, ex.locale)
        }

        @Test
        fun `should return the key if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            assertEquals(key, provider.translate(key, SupportedLanguage.ENGLISH.locale, BUNDLE_NAME))
        }

        @Test
        fun `should return the key if the value is not defined for language with the given array arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            assertEquals(key, provider.translate(key, SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, arrayOf("test")))
        }

        @Test
        fun `should return the key if the value is not defined for language with the given list arguments`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            val key = randomString()
            assertEquals(key, provider.translate(key, SupportedLanguage.ENGLISH.locale, BUNDLE_NAME, listOf("test")))
        }

        @Test
        fun `should return the default value if the value is not defined for language`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals(
                "default_value",
                provider.translate("test_undefined", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME)
            )
        }

        @Test
        fun `should return the value with template for args if no replacement args are defined`() {
            provider.registerResourceBundle(BUNDLE_NAME, SupportedLanguage.ENGLISH.locale, ResourceBundle::getBundle)
            assertEquals(
                "english_value {0}",
                provider.translate("test_args", SupportedLanguage.ENGLISH.locale, BUNDLE_NAME)
            )
        }
    }
}