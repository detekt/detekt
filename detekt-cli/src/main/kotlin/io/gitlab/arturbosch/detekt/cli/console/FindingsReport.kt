package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class FindingsReport : ConsoleReport() {

    override val priority: Int = 40

    override fun render(detektion: Detektion): String? {
        val totalDebt = DebtSumming()
        return with(StringBuilder()) {
            detektion.findings
                .filter { it.value.isNotEmpty() }
                .forEach { (ruleSetId, issues) ->
                    val debtSumming = DebtSumming()
                    val issuesString = issues.joinToString("") {
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
                    append(ruleSetId.format(prefix = "Ruleset: ", suffix = debtString))
                    append(issuesString.yellow())
                }
            val debt = totalDebt.calculateDebt()
            if (debt != null) {
                append("Overall debt: $debt".format("\n"))
            }
            toString()
        }
    }
}
