package fr.rushy.api.translation

import java.util.*

/**
 * Exception used when the system try to use a resource bundle not registered.
 * @param bundleName Name of the bundle.
 * @param locale Locale searched.
 */
public class ResourceBundleNotRegisteredException(public val bundleName: String, public val locale: Locale) :
    RuntimeException("The bundle [$bundleName] for locale [$locale] is not registered.")