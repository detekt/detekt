package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config

class ReportConfig(private val delegate: Config = Config.empty) : Config by delegate {

    val consoleReport: ConsoleReportConfig
        get() = ConsoleReportConfig(delegate.subConfig(CONSOLE_REPORTS))

    val outputReport: OutputReportConfig
        get() = OutputReportConfig(delegate.subConfig(OUTPUT_REPORTS))

    override fun subConfig(key: String) = when (key) {
        CONSOLE_REPORTS -> consoleReport
        OUTPUT_REPORTS -> outputReport
        else -> delegate.subConfig(key)
    }

    companion object {
        private const val CONSOLE_REPORTS = "console-reports"
        private const val OUTPUT_REPORTS = "output-reports"
    }
}

abstract class BaseReportsConfig(protected val delegate: Config) : Config by delegate {
    val active: Boolean get() = valueOrDefault("active", true)
    val includes: Set<String> get() = valueOrDefault("include", emptyList<String>()).toSet()
    val excludes: Set<String> get() = valueOrDefault("exclude", emptyList<String>()).toSet()
    val showProgress: Boolean get() = valueOrDefault("showProgress", true)
}

class ConsoleReportConfig(delegate: Config = Config.empty) : BaseReportsConfig(delegate)

class OutputReportConfig(delegate: Config = Config.empty) : BaseReportsConfig(delegate)
