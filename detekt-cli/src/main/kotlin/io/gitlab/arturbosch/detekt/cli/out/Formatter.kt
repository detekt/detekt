package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.OutputFormat

/**
 * @author Artur Bosch
 */
enum class Formatter {
	PLAIN,
	XML;

	fun create(): OutputFormat = when (this) {
		PLAIN -> PlainOutputFormat()
		XML -> XmlOutputFormat()
	}
}
