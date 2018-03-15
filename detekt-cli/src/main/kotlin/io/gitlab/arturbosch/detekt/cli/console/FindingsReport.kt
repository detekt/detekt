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
		val totalDebt = DebtSumming()
		return with(StringBuilder()) {
			findings.forEach {
				append(it.key.format("Ruleset: "))
				val index = length
				val debtSumming = DebtSumming()
				it.value.forEach {
					debtSumming.add(it.issue.debt)
					append(it.compact().format("\t"))
				}
				val debt = debtSumming.calculateDebt()
				if (debt != null) {
					insert(index, " - $debt debt")
					totalDebt.add(debt)
				}
			}
			val debt = totalDebt.calculateDebt()
			if (debt != null) {
				append("\nOverall debt: $debt\n")
			}
			toString()
		}
	}
}
