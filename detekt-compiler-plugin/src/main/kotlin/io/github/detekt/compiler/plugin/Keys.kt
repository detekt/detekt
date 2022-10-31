package io.github.detekt.compiler.plugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object Options {

    const val isEnabled: String = "isEnabled"
    const val debug: String = "debug"
    const val config = "config"
    const val configDigest: String = "configDigest"
    const val baseline: String = "baseline"
    const val useDefaultConfig: String = "useDefaultConfig"
    const val allRules: String = "allRules"
    const val disableDefaultRuleSets: String = "disableDefaultRuleSets"
    const val parallel: String = "parallel"
    const val rootPath = "rootDir"
    const val excludes = "excludes"
    const val report = "report"
}

object Keys {

    val DEBUG = CompilerConfigurationKey.create<Boolean>(Options.debug)
    val IS_ENABLED = CompilerConfigurationKey.create<Boolean>(Options.isEnabled)
    val CONFIG = CompilerConfigurationKey.create<List<Path>>(Options.config)
    val CONFIG_DIGEST = CompilerConfigurationKey.create<String>(Options.configDigest)
    val BASELINE = CompilerConfigurationKey.create<Path>(Options.baseline)
    val USE_DEFAULT_CONFIG = CompilerConfigurationKey.create<Boolean>(Options.useDefaultConfig)
    val ALL_RULES = CompilerConfigurationKey.create<Boolean>(Options.allRules)
    val DISABLE_DEFAULT_RULE_SETS = CompilerConfigurationKey.create<Boolean>(Options.disableDefaultRuleSets)
    val PARALLEL = CompilerConfigurationKey.create<Boolean>(Options.parallel)
    val ROOT_PATH = CompilerConfigurationKey.create<Path>(Options.rootPath)
    val EXCLUDES = CompilerConfigurationKey.create<List<String>>(Options.excludes)
    val REPORTS = CompilerConfigurationKey.create<Map<String, Path>>(Options.report)
}
