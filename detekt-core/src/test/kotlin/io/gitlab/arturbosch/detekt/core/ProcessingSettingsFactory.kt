package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ReportsSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.PrintStream
import java.net.URI
import java.nio.file.Path
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool

/**
 * Single project input path constructor.
 */
@Suppress("LongParameterList")
fun createProcessingSettings(
    inputPath: Path? = null,
    config: Config = Config.empty,
    pathFilters: PathFilters? = null,
    parallelCompilation: Boolean = false,
    excludeDefaultRuleSets: Boolean = false,
    pluginPaths: List<Path> = emptyList(),
    classpath: List<String> = emptyList(),
    languageVersion: LanguageVersion = LanguageVersion.LATEST_STABLE,
    jvmTarget: JvmTarget = JvmTarget.DEFAULT,
    executorService: ExecutorService = ForkJoinPool.commonPool(),
    outPrinter: PrintStream = NullPrintStream(),
    errPrinter: PrintStream = NullPrintStream(),
    autoCorrect: Boolean = false,
    debug: Boolean = false,
    configUris: Collection<URI> = emptyList(),
    reportPaths: Collection<ReportsSpec.Report> = emptyList()
) = ProcessingSettings(
    inputPaths = inputPath?.let(::listOf) ?: emptyList(),
    config = config,
    pathFilters = pathFilters,
    parallelCompilation = parallelCompilation,
    excludeDefaultRuleSets = excludeDefaultRuleSets,
    pluginPaths = pluginPaths,
    classpath = classpath,
    languageVersion = languageVersion,
    jvmTarget = jvmTarget,
    executorService = executorService,
    outputChannel = outPrinter,
    errorChannel = errPrinter,
    autoCorrect = autoCorrect,
    debug = debug,
    configUris = configUris,
    spec = ProcessingSpec { }
).apply {
    register(DETEKT_OUTPUT_REPORT_PATHS_KEY, reportPaths)
}
