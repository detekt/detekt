package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.buildRunner
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

internal interface DetektInvoker {
    fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean = false
    )

    companion object {
        fun create(project: Project): DetektInvoker =
            if (project.isDryRunEnabled()) {
                DryRunInvoker(project)
            } else {
                DefaultCliInvoker(project)
            }

        private fun Project.isDryRunEnabled(): Boolean {
            return hasProperty(DRY_RUN_PROPERTY) && property(DRY_RUN_PROPERTY) == "true"
        }

        private const val DRY_RUN_PROPERTY = "detekt-dry-run"
    }
}

private class DefaultCliInvoker(private val project: Project) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)

        project.logger.debug(cliArguments.joinToString(" "))

        @Suppress("TooGenericExceptionCaught")
        try {
            buildRunner(cliArguments.toTypedArray()).execute()
            return
        } catch (ignored: InvalidConfig) {
            throw GradleException("Invalid detekt configuration file detected.")
        } catch (ignored: BuildFailure) {
            if (!ignoreFailures) {
                throw GradleException("MaxIssues or failThreshold count was reached.")
            }
            return
        } catch (e: Exception) {
            throw GradleException("There was a problem running detekt.", e)
        }
    }
}

private class DryRunInvoker(private val project: Project) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)
        project.logger.info("Invoking detekt with dry-run.")
        project.logger.info("Task: $taskName")
        project.logger.info("Arguments: ${cliArguments.joinToString(" ")}")
        project.logger.info("Classpath: ${classpath.files}")
        project.logger.info("Ignore failures: $ignoreFailures")
    }
}
