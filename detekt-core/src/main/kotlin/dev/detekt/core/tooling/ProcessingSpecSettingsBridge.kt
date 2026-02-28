package dev.detekt.core.tooling

import dev.detekt.api.Config
import dev.detekt.core.ProcessingSettings
import dev.detekt.core.baseline.DETEKT_BASELINE_CREATION_KEY
import dev.detekt.core.baseline.DETEKT_BASELINE_PATH_KEY
import dev.detekt.core.config.AllRulesConfig
import dev.detekt.core.config.CompositeConfig
import dev.detekt.core.config.DisabledAutoCorrectConfig
import dev.detekt.core.config.YamlConfig
import dev.detekt.core.config.YamlConfig.Companion.load
import dev.detekt.core.config.validation.DeprecatedRule
import dev.detekt.core.config.validation.loadDeprecations
import dev.detekt.core.util.PerformanceMonitor
import dev.detekt.core.util.PerformanceMonitor.Phase
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.utils.openSafeStream
import kotlin.io.path.exists
import kotlin.io.path.isReadable
import kotlin.io.path.isRegularFile
import kotlin.io.path.reader

internal fun <R> ProcessingSpec.withSettings(execute: ProcessingSettings.() -> R): R {
    val monitor = PerformanceMonitor()
    val configuration = monitor.measure(Phase.LoadConfig) {
        workaroundConfiguration(loadConfiguration())
    }
    val settings = monitor.measure(Phase.CreateSettings) {
        ProcessingSettings(this, configuration, monitor).apply {
            baselineSpec.path?.let { register(DETEKT_BASELINE_PATH_KEY, it) }
            register(DETEKT_BASELINE_CREATION_KEY, baselineSpec.shouldCreateDuringAnalysis)
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

internal fun ProcessingSpec.loadConfiguration(): Config =
    with(configSpec) {
        return when {
            configPaths.isNotEmpty() -> configPaths.map { path ->
                require(path.exists()) { "Configuration does not exist: $path" }
                require(path.isRegularFile()) { "Configuration must be a file: $path" }
                require(path.isReadable()) { "Configuration must be readable: $path" }

                load(path.reader())
            }

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
