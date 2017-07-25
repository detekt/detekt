package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * @author Artur Bosch
 */
enum class Formatter {
	PLAIN,
	XML;

	fun create(): OutputReport = when (this) {
		PLAIN -> PlainOutputReport()
		XML -> XmlOutputReport()
	}
}
