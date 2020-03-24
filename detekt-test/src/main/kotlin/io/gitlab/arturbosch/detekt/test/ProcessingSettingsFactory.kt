package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
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
    inputPath: Path,
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
    configUris: Collection<URI> = emptyList()
) = ProcessingSettings(
    inputPaths = listOf(inputPath),
    config = config,
    pathFilters = pathFilters,
    parallelCompilation = parallelCompilation,
    excludeDefaultRuleSets = excludeDefaultRuleSets,
    pluginPaths = pluginPaths,
    classpath = classpath,
    languageVersion = languageVersion,
    jvmTarget = jvmTarget,
    executorService = executorService,
    outPrinter = outPrinter,
    errPrinter = errPrinter,
    autoCorrect = autoCorrect,
    debug = debug,
    configUris = configUris
)
