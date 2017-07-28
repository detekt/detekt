package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.format

/**
 * @author Artur Bosch
 */
class FindingsReport : ConsoleReport() {

	override val priority: Int = 40

	override fun render(detektion: Detektion): String? {
		val findings = detektion.findings
		return with(StringBuilder()) {
			findings.forEach {
				append(it.key.format("Ruleset: "))
				it.value.forEach {
					append(it.compact().format("\t"))
				}
			}
			toString()
		}
	}
}
