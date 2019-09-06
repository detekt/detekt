package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.CorrectableCodeSmell
import io.gitlab.arturbosch.detekt.api.Detektion

class FindingsReport : ConsoleReport() {

    override val priority: Int = 40

    override fun render(detektion: Detektion): String? {
        val findings = detektion
            .findings
            .filter { it.value.isNotEmpty() }
            .filter {
                val correctableCodeSmell = it as? CorrectableCodeSmell
                correctableCodeSmell == null || !correctableCodeSmell.autoCorrectEnabled
            }
        if (findings.isEmpty()) {
            return null
        }

        val totalDebt = DebtSumming()
        return with(StringBuilder()) {
            val debtInfo = totalDebt.printDebtInformation(findings, totalDebt)
            append(debtInfo)
            val debt = totalDebt.calculateDebt()
            if (debt != null) {
                append("Overall debt: $debt".format("\n"))
            }
            toString()
        }
    }
}
