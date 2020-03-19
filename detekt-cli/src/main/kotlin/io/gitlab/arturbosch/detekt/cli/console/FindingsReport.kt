package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.cli.filterAutoCorrectedIssues

class FindingsReport : ConsoleReport() {

    private var config: Config by SingleAssign()

    override val priority: Int = 40

    override fun init(config: Config) {
        this.config = config
    }

    override fun render(detektion: Detektion): String? {
        val findings = detektion
            .filterAutoCorrectedIssues(config)
            .filter { it.value.isNotEmpty() }

        if (findings.isEmpty()) {
            return null
        }

        return with(StringBuilder()) {
            val totalDebt = DebtSumming()
            findings.forEach { (ruleSetId, issues) ->
                val debtSumming = DebtSumming(issues)
                val debt = debtSumming.calculateDebt()
                totalDebt.add(debt)
                append("Ruleset: $ruleSetId - $debt debt".format())
                val issuesString = issues.joinToString("") {
                    it.compact().format("\t")
                }
                append(issuesString.yellow())
            }
            val debt = totalDebt.calculateDebt()
            append("Overall debt: $debt".format("\n"))
            toString()
        }
    }
}
