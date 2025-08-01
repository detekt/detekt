package io.gitlab.arturbosch.detekt.core.reporting

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Extension
import dev.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.extensions.loadExtensions
import io.gitlab.arturbosch.detekt.core.util.isActiveOrDefault

internal sealed class ReportLocator<T : Extension>(configKey: String, protected val settings: ProcessingSettings) {

    private val config = settings.config.subConfig(configKey)
    private val isActive = config.isActiveOrDefault(true)
    protected val excludes = config.valueOrDefault("exclude", emptyList<String>()).toSet()

    fun load(): List<T> {
        if (!isActive) {
            return emptyList()
        }
        return loadReports()
    }

    protected abstract fun loadReports(): List<T>
}

internal class ConsoleReportLocator(settings: ProcessingSettings) :
    ReportLocator<ConsoleReport>("console-reports", settings) {

    override fun loadReports(): List<ConsoleReport> = loadExtensions(settings) { it.id !in excludes }
}

internal class OutputReportLocator(settings: ProcessingSettings) :
    ReportLocator<OutputReport>("output-reports", settings) {

    override fun loadReports(): List<OutputReport> = loadExtensions(settings) { it.id !in excludes }
}
