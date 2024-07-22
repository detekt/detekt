package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ReportsSpec
import io.github.detekt.tooling.dsl.ProcessingSpecBuilder
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.reporting.DETEKT_OUTPUT_REPORT_PATHS_KEY
import java.io.PrintStream
import java.nio.file.Path
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

/**
 * Single project input path constructor.
 */
fun createProcessingSettings(
    inputPath: Path? = null,
    config: Config = Config.empty,
    reportPaths: Collection<ReportsSpec.Report> = emptyList(),
    outputChannel: PrintStream = NullPrintStream(),
    init: ProcessingSpecBuilder.() -> Unit = { /* no-op */ },
): ProcessingSettings {
    val spec = ProcessingSpec {
        project {
            inputPaths = listOfNotNull(inputPath)
            basePath = basePath
        }
        logging {
            debug = true
            this.outputChannel = outputChannel
            errorChannel = NullPrintStream()
        }
        config {
            // Use an empty config in tests to be compatible with old ProcessingSettings config default.
            // This was in particular done due to all console reports being active with an empty config.
            // These outputs are used to assert test conditions.
            configPaths = listOf(resourceAsPath("configs/empty.yml"))
        }
        execution {
            executorService = DirectExecutor() // run in the same thread
        }
        init.invoke(this)
    }
    return ProcessingSettings(spec, config).apply {
        register(DETEKT_OUTPUT_REPORT_PATHS_KEY, reportPaths)
    }
}

fun createNullLoggingSpec(
    init: ProcessingSpecBuilder.() -> Unit = { /* no-op */ },
): ProcessingSpec =
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
        execution {
            executorService = DirectExecutor() // run in the same thread
        }
        init.invoke(this)
    }

class DirectExecutor : AbstractExecutorService() {

    override fun execute(command: Runnable): Unit = command.run()
    override fun shutdown() = Unit
    override fun shutdownNow(): MutableList<Runnable> = mutableListOf()
    override fun isShutdown(): Boolean = true
    override fun isTerminated(): Boolean = true
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = true
}
