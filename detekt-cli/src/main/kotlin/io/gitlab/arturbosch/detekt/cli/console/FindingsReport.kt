package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.format

/**
 * @author Artur Bosch
 * @author schalkms
 */
class FindingsReport : ConsoleReport() {

	override val priority: Int = 40

	override fun render(detektion: Detektion): String? {
		val findings = detektion.findings
		val totalDebt = DebtSumming()
		return with(StringBuilder()) {
			findings.forEach {
				val debtSumming = DebtSumming()
				val issuesString = it.value.joinToString("") {
					debtSumming.add(it.issue.debt)
					it.compact().format("\t")
				}
				val debt = debtSumming.calculateDebt()
				val debtString =
						if (debt != null) {
							totalDebt.add(debt)
							" - $debt debt".format()
						} else {
							"\n"
						}
				append(it.key.format(prefix = "Ruleset: ", suffix = debtString))
				append(issuesString)
			}
			val debt = totalDebt.calculateDebt()
			if (debt != null) {
				append("Overall debt: $debt".format("\n"))
			}
			toString()
		}
	}
}
