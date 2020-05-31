package io.gitlab.arturbosch.detekt.core.reporting

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Extension
import io.gitlab.arturbosch.detekt.api.OutputReport
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.extensions.loadExtensions

internal sealed class ReportLocator<T : Extension>(configKey: String, protected val settings: ProcessingSettings) {

    private val config = settings.config.subConfig(configKey)
    private val isActive = config.valueOrDefault("active", true)
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
