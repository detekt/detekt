package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.net.URLClassLoader
import java.util.ServiceLoader

class ReportLocator(private val settings: ProcessingSettings) {

    private val consoleSubConfig = settings.config.subConfig("console-reports")
    private val consoleActive = consoleSubConfig.valueOrDefault(ACTIVE, true)
    private val consoleExcludes = consoleSubConfig.valueOrDefault(EXCLUDE, emptyList<String>()).toSet()

    private val outputSubConfig = settings.config.subConfig("output-reports")
    private val outputActive = outputSubConfig.valueOrDefault(ACTIVE, true)
    private val outputExcludes = outputSubConfig.valueOrDefault(EXCLUDE, emptyList<String>()).toSet()

    fun load(): List<Extension> {
        settings.debug { "console-report=$consoleActive" }
        settings.debug { "output-report=$outputActive" }
        val detektLoader = URLClassLoader(settings.pluginUrls, javaClass.classLoader)
        val consoleReports = loadConsoleReports(detektLoader)
        settings.debug { "ConsoleReports: $consoleReports" }
        val outputReports = loadOutputReports(detektLoader)
        settings.debug { "OutputReports: $outputReports" }
        return consoleReports.plus(outputReports)
    }

    private fun loadOutputReports(detektLoader: URLClassLoader) =
        if (outputActive) {
            ServiceLoader.load(OutputReport::class.java, detektLoader)
                .filter { it.id !in outputExcludes }
                .toList()
        } else {
            emptyList<OutputReport>()
        }

    private fun loadConsoleReports(detektLoader: URLClassLoader) =
        if (consoleActive) {
            ServiceLoader.load(ConsoleReport::class.java, detektLoader)
                .filter { it.id !in consoleExcludes }
                .toList()
        } else {
            emptyList<ConsoleReport>()
        }

    companion object {
        private const val ACTIVE = "active"
        private const val EXCLUDE = "exclude"
    }
}
