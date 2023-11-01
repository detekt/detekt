package io.github.detekt.compiler.plugin

import org.jetbrains.kotlin.config.CompilerConfigurationKey
import java.nio.file.Path

object Options {

    @Suppress("NonBooleanPropertyPrefixedWithIs")
    const val IS_ENABLED: String = "isEnabled"
    const val DEBUG: String = "debug"
    const val CONFIG = "config"
    const val CONFIG_DIGEST: String = "configDigest"
    const val BASELINE: String = "baseline"
    const val USE_DEFAULT_CONFIG: String = "useDefaultConfig"
    const val ALL_RULES: String = "allRules"
    const val DISABLE_DEFAULT_RULE_SETS: String = "disableDefaultRuleSets"
    const val PARALLEL: String = "parallel"
    const val ROOT_PATH = "rootDir"
    const val EXCLUDES = "excludes"
    const val REPORT = "report"
}

object Keys {

    val DEBUG = CompilerConfigurationKey.create<Boolean>(Options.DEBUG)
    val IS_ENABLED = CompilerConfigurationKey.create<Boolean>(Options.IS_ENABLED)
    val CONFIG = CompilerConfigurationKey.create<List<Path>>(Options.CONFIG)
    val CONFIG_DIGEST = CompilerConfigurationKey.create<String>(Options.CONFIG_DIGEST)
    val BASELINE = CompilerConfigurationKey.create<Path>(Options.BASELINE)
    val USE_DEFAULT_CONFIG = CompilerConfigurationKey.create<Boolean>(Options.USE_DEFAULT_CONFIG)
    val ALL_RULES = CompilerConfigurationKey.create<Boolean>(Options.ALL_RULES)
    val DISABLE_DEFAULT_RULE_SETS = CompilerConfigurationKey.create<Boolean>(Options.DISABLE_DEFAULT_RULE_SETS)
    val PARALLEL = CompilerConfigurationKey.create<Boolean>(Options.PARALLEL)
    val ROOT_PATH = CompilerConfigurationKey.create<Path>(Options.ROOT_PATH)
    val EXCLUDES = CompilerConfigurationKey.create<List<String>>(Options.EXCLUDES)
    val REPORTS = CompilerConfigurationKey.create<Map<String, Path>>(Options.REPORT)
}
