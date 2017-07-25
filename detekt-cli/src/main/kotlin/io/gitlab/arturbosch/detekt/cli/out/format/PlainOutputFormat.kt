package io.gitlab.arturbosch.detekt.cli.out.format

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.OutputFormat

class PlainOutputFormat : OutputFormat() {

	override fun render(smells: List<Finding>): String
			= smells.map { it.compactWithSignature() }.joinToString("\n")
}
