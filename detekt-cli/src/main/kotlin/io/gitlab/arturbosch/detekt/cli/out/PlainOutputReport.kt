package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

class PlainOutputReport : OutputReport() {

	override var fileName = "detekt-plain"
	override val ending: String = "txt"

	override fun render(detektion: Detektion): String {
		val smells = detektion.findings.flatMap { it.value }
		return smells.joinToString("\n") { it.compactWithSignature() }
	}
}
