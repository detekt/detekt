package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.cli.filterAutoCorrectedIssues

class FileBasedFindingsReport : ConsoleReport() {

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

        val totalDebt = DebtSumming()
        return with(StringBuilder()) {
            findings.values
                .flatten()
                .groupBy { it.entity.location.file }
                .forEach { (filename, issues) ->
                    val fileDebt = DebtSumming(issues)
                    val debt = fileDebt.calculateDebt()
                    if (debt != null) {
                        totalDebt.add(debt)
                        append("$filename - $debt debt".format())
                        val issuesString = issues.joinToString("") {
                            it.compact().format("\t")
                        }
                        append(issuesString.yellow())
                    } else {
                        append("\n")
                    }
                }
            val debt = totalDebt.calculateDebt()
            if (debt != null) {
                append("Overall debt: $debt".format("\n"))
            }
            toString()
        }
    }
}
