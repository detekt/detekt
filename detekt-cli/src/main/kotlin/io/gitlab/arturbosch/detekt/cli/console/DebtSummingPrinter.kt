package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

fun printDebtInformation(
    issues: Map<RuleSetId, List<Finding>>,
    totalDebt: DebtSumming
): String? {
    with(StringBuilder()) {
        issues
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
        return toString()
    }
}

fun printFileBasedDebtInformation(
    issues: Map<RuleSetId, List<Finding>>,
    fileDebt: DebtSumming,
    totalDebt: DebtSumming
): String? {
    with(StringBuilder()) {
        issues
            .filter { it.value.isNotEmpty() }
            .forEach { (_, issues) ->
                val debtSumming = DebtSumming()
                val issuesString = issues.joinToString("") {
                    debtSumming.add(it.issue.debt)
                    it.compact().format("\t")
                }
                val debt = debtSumming.calculateDebt()
                debt?.let {
                    fileDebt.add(debt)
                    totalDebt.add(debt)
                } ?: append("\n")
                append(issuesString.yellow())
            }
        return toString()
    }
}
