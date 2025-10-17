package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.OutputReport
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.extensions.loadExtensions
import dev.detekt.tooling.api.spec.ReportsSpec
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

class OutputFacade(
    private val settings: ProcessingSettings,
    private val showReports: Boolean,
) {
    private val reports: Map<String, ReportsSpec.Report> = settings.spec.reportsSpec.reports.associateBy { it.type }

    init {
        reports.values.groupBy { it.path }
            .forEach { (path: Path, reports: List<ReportsSpec.Report>) ->
                check(reports.count() == 1) {
                    "The path $path is defined in multiple reports: ${reports.map { it.type }}"
                }
            }
    }

    fun run(result: Detektion) {
        handleOutputReports(result)
        handleConsoleReports(result)
    }

    private fun handleConsoleReports(result: Detektion) {
        val extensions = ConsoleReportLocator(settings).load()
        for (extension in extensions) {
            val output = extension.render(result)
            if (!output.isNullOrBlank()) {
                settings.outputChannel.appendLine(output)
            }
        }
    }

    private fun handleOutputReports(result: Detektion) {
        val extensions = loadExtensions<OutputReport>(settings)
        for (report in extensions) {
            val filePath = reports[report.id]?.path
            if (filePath != null) {
                report.write(filePath, result)
                if (showReports) {
                    settings.outputChannel.appendLine("Successfully generated ${report.id} at ${filePath.toUri()}")
                }
            }
        }
    }
}

/*
 * Renders result and writes it to the given [filePath].
 */
private fun OutputReport.write(filePath: Path, detektion: Detektion) {
    val reportData = render(detektion)
    if (reportData != null) {
        filePath.createParentDirectories().writeText(reportData)
    }
}
