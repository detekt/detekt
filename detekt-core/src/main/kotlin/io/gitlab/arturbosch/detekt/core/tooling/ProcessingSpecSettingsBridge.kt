package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_CREATION_KEY
import io.gitlab.arturbosch.detekt.core.baseline.DETEKT_BASELINE_PATH_KEY
import io.gitlab.arturbosch.detekt.core.config.AllRulesConfig
import io.gitlab.arturbosch.detekt.core.config.CompositeConfig
import io.gitlab.arturbosch.detekt.core.config.DisabledAutoCorrectConfig
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import io.gitlab.arturbosch.detekt.core.config.validation.DeprecatedRule
import io.gitlab.arturbosch.detekt.core.config.validation.loadDeprecations
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
import io.gitlab.arturbosch.detekt.core.util.MONITOR_PROPERTY_KEY
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor
import io.gitlab.arturbosch.detekt.core.util.PerformanceMonitor.Phase

internal fun <R> ProcessingSpec.withSettings(execute: ProcessingSettings.() -> R): R {
    val monitor = PerformanceMonitor()
    val configuration = monitor.measure(Phase.LoadConfig) {
        workaroundConfiguration(loadConfiguration())
    }
    val settings = monitor.measure(Phase.CreateSettings) {
        ProcessingSettings(this, configuration).apply {
            baselineSpec.path?.let { register(DETEKT_BASELINE_PATH_KEY, it) }
            register(DETEKT_BASELINE_CREATION_KEY, baselineSpec.shouldCreateDuringAnalysis)
            register(MONITOR_PROPERTY_KEY, monitor)
            register(DETEKT_OUTPUT_REPORT_PATHS_KEY, reportsSpec.reports)
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

internal fun ProcessingSpec.loadConfiguration(): Config = with(configSpec) {
    return when {
        configPaths.isNotEmpty() -> configPaths.map { YamlConfig.load(it) }
        resources.isNotEmpty() -> resources.map { it.openSafeStream().reader().use(YamlConfig::load) }
        else -> listOf(Config.empty)
    }
        .reduce { composite, config -> CompositeConfig(config, composite) }
}

private fun ProcessingSpec.workaroundConfiguration(config: Config): Config {
    var declaredConfig: Config = config

    if (rulesSpec.activateAllRules) {
        val deprecatedRules = loadDeprecations().filterIsInstance<DeprecatedRule>().toSet()
        declaredConfig = AllRulesConfig(declaredConfig, deprecatedRules)
    }

    if (!rulesSpec.autoCorrect) {
        declaredConfig = DisabledAutoCorrectConfig(declaredConfig)
    }

    if (configSpec.useDefaultConfig || config === Config.empty) {
        declaredConfig = CompositeConfig(declaredConfig, getDefaultConfiguration())
    }

    return declaredConfig
}
