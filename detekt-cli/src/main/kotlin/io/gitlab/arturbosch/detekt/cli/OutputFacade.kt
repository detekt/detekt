package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.cli.console.BuildFailureReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

class OutputFacade(
    arguments: CliArgs,
    private val result: Detektion,
    private val settings: ProcessingSettings
) {

    private val config = settings.config
    private val createBaseline = arguments.createBaseline
    private val reportPaths = arguments.reportPaths.toHashMap({ it.kind }, { it.path })

    fun run() {
        val reports = ReportLocator(settings)
            .load()
            .filterNot { createBaseline && it is BuildFailureReport }
            .sortedBy { it.priority }
            .asReversed()

        reports.forEach { report ->
            report.init(config)
            when (report) {
                is ConsoleReport -> handleConsoleReport(report, result)
                is OutputReport -> handleOutputReport(report, result)
            }
        }
    }

    private fun handleOutputReport(report: OutputReport, result: Detektion) {
        val filePath = reportPaths[report.id]
        if (filePath != null) {
            report.write(filePath, result)
            result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
        }
    }

    private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
        report.print(settings.outPrinter, result)
    }
}
