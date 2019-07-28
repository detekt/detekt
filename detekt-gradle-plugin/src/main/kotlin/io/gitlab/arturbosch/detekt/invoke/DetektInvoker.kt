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
            1 -> throw GradleException("There was a problem running detekt.")
            2 -> if (!ignoreFailures) throw GradleException("MaxIssues or failThreshold count was reached.")
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
