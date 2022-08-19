@file:JvmName("StringUtils")
@file:JvmMultifileClass

package fr.distractic.bukkit.api.extension

import org.bukkit.ChatColor
import java.util.*

/**
 * Length of the value of UUID.
 */
public const val UUID_SIZE: Int = 36

/**
 * First index of the dash.
 */
public const val UUID_INDEX_FIRST_DASH: Int = 8

/**
 * Second index of the dash.
 */
public const val UUID_INDEX_SECOND_DASH: Int = 13

/**
 * Third index of the dash.
 */
public const val UUID_INDEX_THIRD_DASH: Int = 18

/**
 * Fourth index of the dash.
 */
public const val UUID_INDEX_FOURTH_DASH: Int = 23

/**
 * Array containing all indexes of dashes.
 * @return Array with all indexes.
 */
private fun uuidDashIndexes(): Array<Int> =
    arrayOf(UUID_INDEX_FIRST_DASH, UUID_INDEX_SECOND_DASH, UUID_INDEX_THIRD_DASH, UUID_INDEX_FOURTH_DASH)

/**
 * Apply the coloration of Bukkit
 * @see ChatColor.translateAlternateColorCodes
 * @receiver String that will be analyzed to create a String colored
 * @return A new String with the coloration of Bukkit
 */
public fun String.colored(): String = ChatColor.translateAlternateColorCodes('&', this)

/***
 * Encodes the specified byte array into a String using the [Base64] encoding scheme.
 * @receiver String to encode.
 * @return A String containing the resulting Base64 encoded characters.
 */
public fun String.encodeBase64ToString(): String = Base64.getEncoder().encodeToString(this.toByteArray(Charsets.UTF_8))

/***
 * Encodes the specified byte array into a String using the [Base64] encoding scheme.
 * @receiver Array of byte to encode.
 * @return A byte array containing the resulting Base64 encoded characters.
 */
public fun ByteArray.encodeBase64ToString(): String = Base64.getEncoder().encodeToString(this)

/**
 * Decodes a Base64 encoded String into a newly-allocated byte array using the [Base64] encoding scheme.
 * @receiver String to decode.
 * @return A new String containing the decoded bytes.
 */
public fun String.decodeBase64ToString(): String = Base64.getDecoder().decode(this).decodeToString()

/**
 * Decodes a Base64 encoded String into a newly-allocated byte array using the [Base64] encoding scheme.
 * @receiver Array of byte< to decode.
 * @return A byte array containing the decoded bytes.
 */
public fun String.decodeBase64Bytes(): ByteArray = Base64.getDecoder().decode(this)

/**
 * Creates a [UUID] from the string standard.
 * The string must have the strict format of UUID (with dashes).
 * If the string cannot be converted to UUID, returns null.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value or null if the format is not valid.
 */
public fun String.toUUIDStrictOrNull(): UUID? = try {
    toUUIDStrict()
} catch (_: Exception) {
    null
}

/**
 * Creates a [UUID] from the string standard.
 * The string must have the strict format of UUID (with dashes).
 * If the string cannot be converted to UUID, throws an exception.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value.
 * @throws IllegalArgumentException Exception if the value is not a valid uuid.
 */
@Throws(IllegalArgumentException::class)
public fun String.toUUIDStrict(): UUID = UUID.fromString(this)

/**
 * Creates a [UUID] from a string.
 * The string can have dashes or not.
 * If the string cannot be converted to UUID, returns null.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value or null if the format is not valid.
 */
public fun String.toUUIDOrNull(): UUID? = try {
    toUUID()
} catch (_: Exception) {
    null
}

/**
 * Creates a [UUID] from a string.
 * The string can have dashes or not.
 * If the string cannot be converted to UUID, throws an exception.
 * @receiver String with UUID format.
 * @return The UUID instance equals to the string value.
 * @throws IllegalArgumentException Exception if the value is not a valid uuid.
 */
@Throws(IllegalArgumentException::class)
public fun String.toUUID(): UUID {
    if (length == UUID_SIZE) {
        return toUUIDStrict()
    }

    val dashIndexes = uuidDashIndexes()
    // Without dash
    if (length == UUID_SIZE - dashIndexes.size) {
        return StringBuilder(this).apply {
            for (dashIndex in dashIndexes) {
                insert(dashIndex, '-')
            }
        }.toString().toUUIDStrict()
    }

    throw IllegalArgumentException("Invalid UUID string: $this")
}