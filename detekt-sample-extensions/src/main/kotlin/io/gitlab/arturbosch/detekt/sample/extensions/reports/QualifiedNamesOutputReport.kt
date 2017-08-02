package io.gitlab.arturbosch.detekt.sample.extensions.reports

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport

/**
 * @author Artur Bosch
 */
class QualifiedNamesOutputReport : OutputReport() {

	override val fileName: String = "fqNames.txt"

	override fun render(detektion: Detektion): String? {
		return qualifiedNamesReport(detektion)
	}
}
