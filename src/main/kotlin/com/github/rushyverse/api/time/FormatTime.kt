package com.github.rushyverse.api.time

import com.github.rushyverse.api.APIPlugin
import com.github.rushyverse.api.extension.toIntOrString
import com.github.rushyverse.api.translation.Translator
import java.util.*
import kotlin.time.Duration

/**
 * Data class that represents a time format.
 * It provides properties and companion functions to create time formats.
 *
 * @property second A function that formats the seconds.
 * @property minute A function that formats the minutes.
 * @property hour A function that formats the hours.
 * @property day A function that formats the days.
 * @constructor Creates a new instance of FormatTime.
 */
public data class FormatTime(
    val second: FormatPartTime? = null,
    val minute: FormatPartTime? = null,
    val hour: FormatPartTime? = null,
    val day: FormatPartTime? = null,
) {

    public companion object {

        /**
         * Constructs a FormatTime object for displaying time in long format.
         * The long format is used for displaying time in a more readable way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @return A FormatTime object configured for long format.
         */
        public fun long(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.long", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.long", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.long", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.long", locale, arrayOf(it.toIntOrString()), bundle) },
        )

        /**
         * Constructs a FormatTime object for displaying time in short format.
         * The short format is used for displaying time in a more compact way.
         * @param translator The translator used for retrieving translations.
         * @param locale The desired locale for the translations.
         * @param bundle The name of the translation bundle to use.
         * @return A FormatTime object configured for short format.
         */
        public fun short(
            translator: Translator,
            locale: Locale,
            bundle: String = APIPlugin.BUNDLE_API,
        ): FormatTime = FormatTime(
            second = { translator.get("time.second.short", locale, arrayOf(it.toIntOrString()), bundle) },
            minute = { translator.get("time.minute.short", locale, arrayOf(it.toIntOrString()), bundle) },
            hour = { translator.get("time.hour.short", locale, arrayOf(it.toIntOrString()), bundle) },
            day = { translator.get("time.day.short", locale, arrayOf(it.toIntOrString()), bundle) },
        )
    }
}

/**
 * Type of function used to format a [Duration] part to a string.
 */
public typealias FormatPartTime = (String) -> String
