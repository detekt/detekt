package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * @author Artur Bosch
 */
class ReportLocator(
    private val settings: ProcessingSettings,
    reportConfig: ReportConfig
) {

    private val consoleSubConfig = reportConfig.consoleReport
    private val consoleActive = consoleSubConfig.active
    private val consoleIncludes = consoleSubConfig.includes
    private val consoleExcludes = consoleSubConfig.excludes

    private val outputSubConfig = reportConfig.outputReport
    private val outputActive = outputSubConfig.active
    private val outputIncludes = outputSubConfig.includes
    private val outputExcludes = outputSubConfig.excludes

    fun load(): List<Extension> {
        val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)

        settings.debug { "console-report=$consoleActive" }
        val consoleReports = loadConsoleReports(detektLoader)
        settings.debug { "ConsoleReports: $consoleReports" }

        settings.debug { "output-report=$outputActive" }
        val outputReports = loadOutputReports(detektLoader)
        settings.debug { "OutputReports: $outputReports" }

        return consoleReports + outputReports
    }

    private fun loadConsoleReports(detektLoader: URLClassLoader) =
        if (consoleActive) {
            ServiceLoader.load(ConsoleReport::class.java, detektLoader)
                .filter { consoleIncludes.isEmpty() || it.id in consoleIncludes }
                .filter { it.id !in consoleExcludes }
                .toList()
        } else {
            emptyList<ConsoleReport>()
        }

    private fun loadOutputReports(detektLoader: URLClassLoader) =
        if (outputActive) {
            ServiceLoader.load(OutputReport::class.java, detektLoader)
                .filter { outputIncludes.isEmpty() || it.id in outputIncludes }
                .filter { it.id !in outputExcludes }
                .toList()
        } else {
            emptyList<OutputReport>()
        }
}
