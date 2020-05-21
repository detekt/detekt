package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

class OutputFacade(
    arguments: CliArgs,
    private val result: Detektion,
    private val settings: ProcessingSettings
) {

    private val config = settings.config
    private val reportPaths = arguments.reportPaths.associateBy { it.kind }

    fun run() {
        val reports = ReportLocator(settings)
            .load()
            .sortedBy { it.priority }
            .asReversed()

        reports.forEach { report ->
            report.init(config)
            report.init(settings)
            when (report) {
                is ConsoleReport -> handleConsoleReport(report, result)
                is OutputReport -> handleOutputReport(report, result)
            }
        }
    }

    private fun handleOutputReport(report: OutputReport, result: Detektion) {
        val filePath = reportPaths[report.id]?.path
        if (filePath != null) {
            report.write(filePath, result)
            result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
        }
    }

    private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
        report.print(settings.outPrinter, result)
    }
}
