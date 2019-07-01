package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.api.notCompatibleClasses
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade
import io.gitlab.arturbosch.detekt.cli.console.BuildFailureReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class OutputFacade(
    arguments: CliArgs,
    private val detektion: Detektion,
    private val settings: ProcessingSettings
) {

    private val config = settings.config
    private val baselineFacade = arguments.baseline?.let { BaselineFacade(it) }
    private val createBaseline = arguments.createBaseline
    private val reportPaths = arguments.reportPaths.toHashMap({ it.kind }, { it.path })

    fun run() {
        if (createBaseline) {
            val smells = detektion.findings.flatMap { it.value }
            baselineFacade?.create(smells)
        }

        val result = if (baselineFacade != null) {
            FilteredDetectionResult(detektion, baselineFacade)
        } else detektion

        val reportConfig = ReportConfig(config)
        val reports = ReportLocator(settings, reportConfig)
            .load()
            .filterNot { createBaseline && it is BuildFailureReport }
            .sortedBy { it.priority }
            .asReversed()

        checkReportsCompatibility(reports)

        reports.forEach { report ->
            report.init(config)
            when (report) {
                is ConsoleReport -> handleConsoleReport(report, result)
                is OutputReport -> handleOutputReport(report, result, reportConfig.outputReport)
            }
        }
    }

    private fun handleOutputReport(report: OutputReport, result: Detektion, outputReportsConfig: OutputReportConfig) {
        val filePath = reportPaths[report.id]
        if (filePath != null) {
            report.write(filePath, result)
            if (outputReportsConfig.showProgress) {
                settings.info("Successfully generated ${report.name} at $filePath")
            }
        }
    }

    private fun handleConsoleReport(report: ConsoleReport, result: Detektion) {
        report.print(settings.outPrinter, result)
    }

    private fun checkReportsCompatibility(reports: List<Extension>) {
        reports.forEach { report ->
            val notCompatibleClasses = report.javaClass.notCompatibleClasses
            reports.forEach { extension ->
                if (notCompatibleClasses.any { it.isInstance(extension) }) {
                    settings.errorPrinter.println(
                        "%s report is not compatible with %s".format(
                            report.javaClass.simpleName,
                            extension.javaClass.simpleName
                        )
                    )
                }
            }
        }
    }
}
