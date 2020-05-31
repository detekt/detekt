package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.ReportingExtension
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.SingleAssign
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.getOrNull
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlin.system.measureTimeMillis

@OptIn(UnstableApi::class)
class OutputFacade : ReportingExtension {

    private var reports: Map<String, ReportPath> by SingleAssign()
    private var settings: ProcessingSettings by SingleAssign()

    override fun init(context: SetupContext) {
        val reportPaths: Collection<ReportPath> =
            context.getOrNull(DETEKT_OUTPUT_REPORT_PATHS_KEY) ?: emptyList()
        reports = reportPaths.associateBy { it.kind }
        settings = context as? ProcessingSettings ?: error("ProcessingSettings expected.")
    }

    override fun onFinalResult(result: Detektion) {
        // always run output reports as they produce notifications
        // which may get printed on the console
        handleOutputReports(result)
        handleConsoleReports(result)
    }

    private fun handleConsoleReports(result: Detektion) {
        val durationConsoleReports = measureTimeMillis {
            val extensions = ConsoleReportLocator(settings).load()
            extensions.forEach { it.print(settings.outPrinter, result) }
        }
        settings.debug { "Writing console results took $durationConsoleReports ms" }
    }

    private fun handleOutputReports(result: Detektion) {
        val durationOutputReports = measureTimeMillis {
            val extensions = OutputReportLocator(settings).load()
            for (report in extensions) {
                val filePath = reports[report.id]?.path
                if (filePath != null) {
                    report.write(filePath, result)
                    result.add(SimpleNotification("Successfully generated ${report.name} at $filePath"))
                }
            }
        }
        settings.debug { "Writing output results took $durationOutputReports ms" }
    }
}
