package io.gitlab.arturbosch.detekt.api

/**
 * When analyzing sub path 'testData' of the kotlin project, CompositeElement.getText() throws
 * a RuntimeException stating 'Underestimated text length' - #.
 */
fun withPsiTextRuntimeError(defaultValue: () -> String, block: () -> String): String {
	return try {
		block()
	} catch (e: RuntimeException) {
		val message = e.message
		if (message != null && message.contains("Underestimated text length")) {
			return defaultValue() + "!<UnderestimatedTextLengthException>"
		} else throw e
	}
}
