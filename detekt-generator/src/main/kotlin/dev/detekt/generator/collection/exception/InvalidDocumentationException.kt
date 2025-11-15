package dev.detekt.generator.collection.exception

/**
 * Thrown to indicate that rule documentation in KDoc is missing or invalid.
 */
class InvalidDocumentationException(message: String) : RuntimeException(message)
