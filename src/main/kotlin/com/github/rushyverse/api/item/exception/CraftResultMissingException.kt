package com.github.rushyverse.api.item.exception

/**
 * Exception when the result item is not defined.
 */
public class CraftResultMissingException : IllegalStateException("The result item for the craft must be defined")