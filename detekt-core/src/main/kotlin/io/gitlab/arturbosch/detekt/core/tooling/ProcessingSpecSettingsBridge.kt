package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_CREATION_KEY
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_PATH_KEY
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
import io.gitlab.arturbosch.detekt.core.util.MONITOR_PROPERTY_KEY
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor.Phase

internal fun <R> ProcessingSpec.withSettings(execute: ProcessingSettings.() -> R): R {
    val monitor = PerformanceMonitor()
    val configuration = monitor.measure(Phase.LoadConfig) { loadConfiguration() }
    val settings = monitor.measure(Phase.CreateSettings) {
        ProcessingSettings(this, configuration).apply {
            baselineSpec.path?.let { register(DETEKT_BASELINE_PATH_KEY, it) }
            register(DETEKT_BASELINE_CREATION_KEY, baselineSpec.shouldCreateDuringAnalysis)
            register(DETEKT_OUTPUT_REPORT_PATHS_KEY, reportsSpec.reports)
            register(MONITOR_PROPERTY_KEY, monitor)
        }
    }
    val result = settings.use { execute(it) }
    if (loggingSpec.debug) {
        for ((phase, duration) in monitor.allFinished()) {
            settings.debug { "Phase $phase took $duration" }
        }
    }
    return result
}
