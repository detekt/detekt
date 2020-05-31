package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

class OutputFacade(
    reportPaths: List<ReportPath>,
    private val result: Detektion,
    private val settings: ProcessingSettings
) {

    private val reports: Map<String, ReportPath> = reportPaths.associateBy { it.kind }

    fun run() {
        OutputReportLocator(settings)
            .load()
            .forEach(::handleOutputReport)
        ConsoleReportLocator(settings)
            .load()
            .forEach(::handleConsoleReport)
    }

    private fun handleOutputReport(report: OutputReport) {
        val filePath = reports[report.id]?.path
        if (filePath != null) {
            report.write(filePath, result)
            result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
        }
    }

    private fun handleConsoleReport(report: ConsoleReport) {
        report.print(settings.outPrinter, result)
    }
}
