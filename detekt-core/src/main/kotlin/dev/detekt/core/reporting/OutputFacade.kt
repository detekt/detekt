package dev.detekt.core.reporting

import dev.detekt.tooling.api.spec.ReportsSpec
import dev.detekt.api.Detektion
import dev.detekt.api.getOrNull
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.util.SimpleNotification

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
                result.add(SimpleNotification("Successfully generated ${report.id} at ${filePath.toUri()}"))
            }
        }
    }
}
