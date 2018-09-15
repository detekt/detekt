package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * @author Marvin Ramin
 */
class PlainOutputReport : OutputReport() {

	override val ending: String = "txt"

	override val name = "plain text report"

	override fun render(detektion: Detektion): String {
		val smells = detektion.findings.flatMap { it.value }
		return smells.joinToString("\n") { it.compactWithSignature() }
	}
}
