package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.cli.ConsoleReportConfig
import io.gitlab.arturbosch.detekt.cli.ReportConfig

/**
 * @author Artur Bosch
 * @author schalkms
 */
class VerboseFindingsReport : ConsoleReport() {

    override val priority: Int = 40

    private var config: ConsoleReportConfig by SingleAssign()

    override fun init(config: Config) {
        super.init(config)
        this.config = ReportConfig(config).consoleReport
    }

    override fun render(detektion: Detektion) = buildString {
        val totalDebt = DebtSumming()
        detektion.findings.forEach { (rule, findings) ->
            val debtSumming = DebtSumming()
            findings.forEach { debtSumming.add(it.issue.debt) }

            val debt = debtSumming.calculateDebt()
            if (config.showProgress || findings.isNotEmpty() || debt != null) {
                append("Ruleset: $rule")
                if (debt != null) {
                    totalDebt.add(debt)
                    append(" - $debt debt")
                }
                append("\n")
            }

            findings.forEach { finding ->
                finding.messages.forEach {
                    append("\t${it.yellow()}\n")
                }
            }
        }

        val debt = totalDebt.calculateDebt()
        if (debt != null) {
            append("\nOverall debt: $debt\n")
        }
    }

    private val Finding.messages: List<String>
        get() = mutableListOf<String>().apply {
            add(compact())

            if (config.showMessages) {
                if (message.isNotEmpty()) {
                    add("\t$message")
                }

                val description = issue.description
                if (description.isNotEmpty()) {
                    add("\t$description")
                }
            }
        }
}
