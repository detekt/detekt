package io.gitlab.arturbosch.detekt.invoke

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

private const val NORMAL_RUN = 0
private const val UNEXPECTED_RUN = 1
private const val ISSUE_THRESHOLD_MET = 2
private const val INVALID_CONFIG = 3

private class DefaultCliInvoker(private val project: Project) : DetektInvoker {

    override fun invokeCli(
        arguments: List<CliArgument>,
        classpath: FileCollection,
        taskName: String,
        ignoreFailures: Boolean
    ) {
        val detektTmpDir = project.mkdir("${project.buildDir}/tmp/detekt")
        val argsFile = project.file("$detektTmpDir/$taskName.args")

        val cliArguments = arguments.flatMap(CliArgument::toArgument)

        argsFile.writeText(cliArguments.joinToString("\n"))

        project.logger.debug(cliArguments.joinToString(" "))

        val proc = project.javaexec {
            it.main = DETEKT_MAIN
            it.classpath = classpath
            it.args = listOf("@${argsFile.absolutePath}")
            it.isIgnoreExitValue = true
        }
        val exitValue = proc.exitValue
        project.logger.debug("Detekt finished with exit value $exitValue")

        when (exitValue) {
            NORMAL_RUN -> return
            UNEXPECTED_RUN -> throw GradleException("There was a problem running detekt.")
            ISSUE_THRESHOLD_MET -> if (!ignoreFailures) {
                throw GradleException("MaxIssues or failThreshold count was reached.")
            }
            INVALID_CONFIG -> throw GradleException("Invalid detekt configuration file detected.")
            else -> throw GradleException("Unexpected detekt exit with code '${exitValue}'.")
        }
    }

    companion object {
        private const val DETEKT_MAIN = "io.gitlab.arturbosch.detekt.cli.Main"
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
