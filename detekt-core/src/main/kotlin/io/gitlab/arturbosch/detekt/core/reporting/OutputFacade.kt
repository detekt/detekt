package io.gitlab.arturbosch.detekt.core.reporting

import io.github.detekt.tooling.api.spec.ReportsSpec
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

@OptIn(UnstableApi::class)
class OutputFacade(
    private val settings: ProcessingSettings
) {

    private var reports: Map<String, ReportsSpec.Report> =
        settings.getOrNull<Collection<ReportsSpec.Report>>(DETEKT_OUTPUT_REPORT_PATHS_KEY)
            ?.associateBy { it.type }
            ?: emptyMap()

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
            val filePath = reports[defaultReportMapping(report.id)]?.path
            if (filePath != null) {
                report.write(filePath, result)
                result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
            }
        }
    }
}
