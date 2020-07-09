package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_CREATION_KEY
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_PATH_KEY
import io.gitlab.arturbosch.detekt.core.config.extractUris
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
import io.gitlab.arturbosch.detekt.core.util.MONITOR_PROPERTY_KEY
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor.Phase
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion

internal fun <R> ProcessingSpec.withSettings(execute: ProcessingSettings.() -> R): R {
    val monitor = PerformanceMonitor()
    val plugins = extensionsSpec.plugins.run {
        check(this?.loader == null) { "Using a custom classloader not yet supported." }
        this?.paths?.toList() ?: emptyList()
    }
    val configuration = monitor.measure(Phase.LoadConfig) { loadConfiguration() }
    val settings = monitor.measure(Phase.CreateSettings) {
        ProcessingSettings(
            projectSpec.inputPaths.toList(),
            configuration,
            executionSpec.parallelAnalysis,
            extensionsSpec.disableDefaultRuleSets,
            plugins,
            compilerSpec.classpathEntries(),
            compilerSpec.parseLanguageVersion(),
            compilerSpec.parseJvmTarget() ?: JvmTarget.JVM_1_8,
            executionSpec.executorService,
            rulesSpec.autoCorrect,
            configSpec.extractUris(),
            this
        ).apply {
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

private fun CompilerSpec.parseJvmTarget(): JvmTarget? {
    fun parse(value: String) =
        checkNotNull(JvmTarget.fromString(value)) { "Invalid value passed to --jvm-target" }
    return jvmTarget?.let(::parse)
}

private fun CompilerSpec.classpathEntries(): List<String> =
    classpath?.split(":;") ?: emptyList() // support both windows : and unix ;

private fun CompilerSpec.parseLanguageVersion(): LanguageVersion? {
    fun parse(value: String): LanguageVersion {
        val version = LanguageVersion.fromFullVersionString(value)
        return checkNotNull(version) { "Invalid value passed as language version." }
    }
    return languageVersion?.let(::parse)
}
