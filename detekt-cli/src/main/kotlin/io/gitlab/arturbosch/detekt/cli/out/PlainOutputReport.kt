package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

class PlainOutputReport : OutputReport() {

	override fun render(detektion: Detektion): String {
		val smells = detektion.findings.flatMap { it.value }
		return smells.map { it.compactWithSignature() }.joinToString("\n")
	}
}
