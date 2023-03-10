package com.github.rushyverse.api.image.exception

/**
 * Exception when an issue occurs with an image.
 */
public open class ImageException(message: String) : Exception(message)

/**
 * Exception thrown when an image is already loaded.
 */
public open class ImageAlreadyLoadedException(message: String) : ImageException(message)

/**
 * Exception thrown when an image is not loaded.
 */
public open class ImageNotLoadedException(message: String) : ImageException(message)