package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

/**
 * @author Artur Bosch
 */
class ComplexityReport : ConsoleReport() {

	override val priority: Int = 20

	override fun render(detektion: Detektion): String? {
		val reportGenerator = ReportGenerator.create(detektion)
		return reportGenerator.generate()
	}

}
