package io.gitlab.arturbosch.detekt.generator.collection

/**
 * Thrown to indicate that rule documentation in KDoc is missing or invalid.
 *
 * @author Marvin Ramin
 */
class InvalidDocumentationException(message: String)
	: RuntimeException(message)
