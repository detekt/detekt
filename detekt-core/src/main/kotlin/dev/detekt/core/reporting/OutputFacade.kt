package dev.detekt.core.reporting

import dev.detekt.api.Detektion
import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.api.OutputReport
import dev.detekt.core.ProcessingSettings
import dev.detekt.tooling.api.spec.ReportsSpec
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.writeText

class OutputFacade(
    private val settings: ProcessingSettings,
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
        // Always run output reports first.
        // They produce notifications which may get printed on the console.
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
        val extensions = OutputReportLocator(settings).load()
        for (report in extensions) {
            val filePath = reports[defaultReportMapping(report)]?.path
            if (filePath != null) {
                report.write(filePath, result)
                result.add(Notification("Successfully generated ${report.id} at ${filePath.toUri()}", Level.Error))
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
