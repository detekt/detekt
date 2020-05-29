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

    private val reportPaths: Map<String, ReportPath> = arguments.reportPaths.associateBy { it.kind }

    fun run() {
        OutputReportLocator(settings)
            .load()
            .forEach(::handleOutputReport)
        ConsoleReportLocator(settings)
            .load()
            .forEach(::handleConsoleReport)
    }

    private fun handleOutputReport(report: OutputReport) {
        val filePath = reportPaths[report.id]?.path
        if (filePath != null) {
            report.write(filePath, result)
            result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
        }
    }

    private fun handleConsoleReport(report: ConsoleReport) {
        report.print(settings.outPrinter, result)
    }
}
