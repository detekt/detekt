package dev.detekt.core.reporting

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion
import dev.detekt.api.Notification
import dev.detekt.api.Notification.Level
import dev.detekt.api.OutputReport
import dev.detekt.api.getOrNull
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.extensions.loadExtensions
import dev.detekt.core.util.isActiveOrDefault
import dev.detekt.tooling.api.spec.ReportsSpec

class OutputFacade(
    private val settings: ProcessingSettings,
) {

    private val reports: Map<String, ReportsSpec.Report> =
        settings.getOrNull<Collection<ReportsSpec.Report>>(DETEKT_OUTPUT_REPORT_PATHS_KEY)
            ?.associateBy { it.type }
            .orEmpty()

    fun run(result: Detektion) {
        // Always run output reports first.
        // They produce notifications which may get printed on the console.
        handleOutputReports(result)
        handleConsoleReports(result)
    }

    private fun handleConsoleReports(result: Detektion) {
        val extensions = loadConsoleReport(settings)
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
            val filePath = reports[defaultReportMapping(report)]?.path
            if (filePath != null) {
                report.write(filePath, result)
                result.add(Notification("Successfully generated ${report.id} at ${filePath.toUri()}", Level.Error))
            }
        }
    }
}

internal fun loadConsoleReport(settings: ProcessingSettings): List<ConsoleReport> {
    val config = settings.config.subConfig("console-reports")
    val isActive = config.isActiveOrDefault(true)
    return if (!isActive) {
        emptyList()
    } else {
        val excludes = config.valueOrDefault("exclude", emptyList<String>()).toSet()
        loadExtensions(settings) { it.id !in excludes }
    }
}
