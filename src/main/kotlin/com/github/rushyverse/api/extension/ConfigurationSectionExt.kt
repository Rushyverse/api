package com.github.rushyverse.api.extension

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

private fun ConfigurationSection.sectionException(): Nothing =
    throw NullPointerException("ConfigurationSection not found: $currentPath")

private fun ConfigurationSection.fieldException(): Nothing =
    throw NullPointerException("Configuration Field not found: $currentPath")

public fun ConfigurationSection.getSectionOrException(sectionName: String): ConfigurationSection {
    return getConfigurationSection(sectionName)
        ?: sectionException()
}

public fun ConfigurationSection.getIntOrException(fieldPath: String): Int =
    getInt(fieldPath) ?: fieldException()


public fun ConfigurationSection.getStringOrException(fieldPath: String): String =
    getString(fieldPath) ?: fieldException()

public fun ConfigurationSection.getMaterialOrException(fieldPath: String): Material =
    Material.getMaterial(getStringOrException(fieldPath)) ?: fieldException()