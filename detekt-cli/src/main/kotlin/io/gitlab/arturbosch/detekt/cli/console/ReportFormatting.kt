package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Finding

internal const val PREFIX = "\t- "

internal fun Any.format(prefix: String = "", suffix: String = "\n") = "$prefix$this$suffix"

internal fun printFindings(findings: Map<String, List<Finding>>): String? {
    return with(StringBuilder()) {
        val debtList = mutableListOf<Debt>()
        findings.forEach { (key, issues) ->
            val debt = issues
                .map { it.issue.debt }
                .reduce { acc, d -> acc + d }
            debtList.add(debt)
            append("$key - $debt debt".format())
            val issuesString = issues.joinToString("") {
                it.compact().format("\t")
            }
            append(issuesString.yellow())
        }
        val overallDebt = debtList.reduce { acc, d -> acc + d }
        append("Overall debt: $overallDebt".format("\n"))
        toString()
    }
}
