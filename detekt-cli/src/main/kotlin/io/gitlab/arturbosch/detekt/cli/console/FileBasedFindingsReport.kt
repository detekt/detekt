package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileBasedConsoleReport
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class FileBasedFindingsReport : FileBasedConsoleReport() {

    override val priority: Int = 40

    override fun render(detektion: Detektion): String? {
        val totalDebt = DebtSumming()
        val debtSummingPrinter = DebtSummingPrinter()
        return with(StringBuilder()) {
            val ruleSetMap = detektion.findings.filter { it.value.isNotEmpty() }
            val distinctFileNames = ruleSetMap.values.flatten().map { it.entity.location.file }.distinct()
            distinctFileNames
                .forEach { filename ->
                    val newRuleSetMap = HashMap<RuleSetId, List<Finding>>()
                    ruleSetMap.forEach { (key, value) ->
                        newRuleSetMap[key] = value.filter { it.entity.location.file == filename }
                    }
                    debtSummingPrinter.printDebtInformation(newRuleSetMap, totalDebt)?.let {
                        append(it)
                    }
                }
            val debt = totalDebt.calculateDebt()
            if (debt != null) {
                append("Overall debt: $debt".format("\n"))
            }
            if (toString().isBlank()) {
                return null
            }
            toString()
        }
    }
}
