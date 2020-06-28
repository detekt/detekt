package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.config.extractUris
import io.gitlab.arturbosch.detekt.core.config.loadConfiguration
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.PrintStream

internal fun <R> ProcessingSpec.withSettings(execute: ProcessingSettings.() -> R): R {
    val plugins = extensionsSpec.plugins.run {
        check(this?.loader == null) { "Using a custom classloader not yet supported." }
        this?.paths?.toList() ?: emptyList()
    }
    return ProcessingSettings(
        projectSpec.inputPaths.toList(),
        this.loadConfiguration(),
        PathFilters.of(projectSpec.includes.toList(), projectSpec.excludes.toList()),
        executionSpec.parallelAnalysis,
        extensionsSpec.disableDefaultRuleSets,
        plugins,
        compilerSpec.classpathEntries(),
        compilerSpec.parseLanguageVersion(),
        compilerSpec.parseJvmTarget() ?: JvmTarget.JVM_1_8,
        executionSpec.executorService,
        loggingSpec.outputChannel as? PrintStream ?: error("PrintStream required for now."),
        loggingSpec.errorChannel as? PrintStream ?: error("PrintStream required for now."),
        rulesSpec.autoCorrect,
        loggingSpec.debug,
        configSpec.extractUris()
    ).use { execute(it) }
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
