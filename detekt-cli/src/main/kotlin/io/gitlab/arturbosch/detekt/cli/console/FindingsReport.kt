package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class FindingsReport : ConsoleReport() {

    override val priority: Int = 40

    override fun render(detektion: Detektion): String? {
        val totalDebt = DebtSumming()
        val debtSummingPrinter = DebtSummingPrinter()
        return with(StringBuilder()) {
            debtSummingPrinter.printDebtInformation(detektion.findings, totalDebt)?.let {
                append(it)
                val debt = totalDebt.calculateDebt()
                if (debt != null) {
                    append("Overall debt: $debt".format("\n"))
                }
                toString()
            }
        }
    }
}
