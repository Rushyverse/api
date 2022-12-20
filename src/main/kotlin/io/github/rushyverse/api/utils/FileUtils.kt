package io.github.rushyverse.api.utils

import java.io.File

/**
 * Get the current directory where is executed the program.
 */
public val workingDirectory: File
    get() = File(System.getProperty("user.dir"))