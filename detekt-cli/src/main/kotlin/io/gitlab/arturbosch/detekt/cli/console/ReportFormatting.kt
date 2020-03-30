package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Finding

internal fun printFindings(findings: Map<String, List<Finding>>): String? {
    return with(StringBuilder()) {
        val debtList = mutableListOf<Debt>()
        findings.forEach { (key, issues) ->
            val debt = issues
                .map { it.issue.debt }
                .reduce { acc, d -> acc + d }
            debtList.add(debt)
            append("$key - $debt debt\n")
            issues.forEach {
                append("\t")
                append(it.compact().yellow())
                append("\n")
            }
        }
        val overallDebt = debtList.reduce { acc, d -> acc + d }
        append("\nOverall debt: $overallDebt\n")
        toString()
    }
}
