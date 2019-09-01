package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileBasedConsoleReport
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class FileBasedFindingsReport : FileBasedConsoleReport() {

    override val priority: Int = 40

    override fun render(detektion: Detektion): String? {
        val findings = detektion
            .findings
            .filter { it.value.isNotEmpty() }
        if (findings.isEmpty()) {
            return null
        }

        val totalDebt = DebtSumming()
        val debtSummingPrinter = DebtSummingPrinter()
        return with(StringBuilder()) {
            val distinctFileNames = findings.values.flatten().map { it.entity.location.file }.distinct()
            distinctFileNames
                .forEach { filename ->
                    val newRuleSetMap = HashMap<RuleSetId, List<Finding>>()
                    findings.forEach { (key, value) ->
                        newRuleSetMap[key] = value.filter { it.entity.location.file == filename }
                    }
                    val debtInfo = debtSummingPrinter.printDebtInformation(newRuleSetMap, totalDebt)
                    append(debtInfo)
                }
            val debt = totalDebt.calculateDebt()
            if (debt != null) {
                append("Overall debt: $debt".format("\n"))
            }
            toString()
        }
    }
}
