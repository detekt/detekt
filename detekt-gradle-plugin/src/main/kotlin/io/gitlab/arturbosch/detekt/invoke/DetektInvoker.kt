package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.gradle.worker.ExecuteDetektAction
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.workers.WorkerExecutor

internal interface DetektInvoker {

    fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean = false
    )

    companion object {
        fun create(project: Project, executor: WorkerExecutor): DetektInvoker =
            if (project.isDryRunEnabled()) {
                DryRunInvoker(project.logger)
            } else {
                DefaultCliInvoker(executor)
            }

        private fun Project.isDryRunEnabled(): Boolean {
            return hasProperty(DRY_RUN_PROPERTY) && property(DRY_RUN_PROPERTY) == "true"
        }

        private const val DRY_RUN_PROPERTY = "detekt-dry-run"
    }
}

private class DefaultCliInvoker(private val executor: WorkerExecutor) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        val workQueue = executor.classLoaderIsolation {
            it.classpath.setFrom(classpath)
        }
        workQueue.submit(ExecuteDetektAction::class.java) {
            it.cliArguments.set(cliArguments)
            it.ignoreFailures.set(ignoreFailures)
        }
    }
}

private class DryRunInvoker(private val logger: Logger) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        logger.info("Invoking detekt with dry-run.")
        logger.info("Task: $taskName")
        logger.info("Arguments: ${cliArguments.joinToString(" ")}")
        logger.info("Classpath: ${classpath.files}")
        logger.info("Ignore failures: $ignoreFailures")
    }
}
