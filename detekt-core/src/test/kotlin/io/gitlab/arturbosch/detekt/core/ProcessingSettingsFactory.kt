package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ReportsSpec
import io.github.detekt.tooling.dsl.ProcessingSpecBuilder
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
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
    parallelCompilation: Boolean = false,
    excludeDefaultRuleSets: Boolean = false,
    pluginPaths: List<Path> = emptyList(),
    executorService: ExecutorService = ForkJoinPool.commonPool(),
    autoCorrect: Boolean = false,
    configUris: Collection<URI> = emptyList(),
    reportPaths: Collection<ReportsSpec.Report> = emptyList(),
    spec: ProcessingSpec = createNullLoggingSpec()
) = ProcessingSettings(
    inputPaths = inputPath?.let(::listOf) ?: emptyList(),
    config = config,
    parallelCompilation = parallelCompilation,
    excludeDefaultRuleSets = excludeDefaultRuleSets,
    pluginPaths = pluginPaths,
    executorService = executorService,
    autoCorrect = autoCorrect,
    configUris = configUris,
    spec = spec
).apply {
    register(DETEKT_OUTPUT_REPORT_PATHS_KEY, reportPaths)
}

fun createNullLoggingSpec(init: (ProcessingSpecBuilder.() -> Unit)? = null): ProcessingSpec =
    ProcessingSpec {
        logging {
            outputChannel = NullPrintStream()
            errorChannel = NullPrintStream()
        }
        config {
            // Use an empty config in tests to be compatible with old ProcessingSettings config default.
            // This was in particular done due to all console reports being active with an empty config.
            // These outputs are used to assert test conditions.
            configPaths = listOf(resourceAsPath("configs/empty.yml"))
        }
        init?.invoke(this)
    }
