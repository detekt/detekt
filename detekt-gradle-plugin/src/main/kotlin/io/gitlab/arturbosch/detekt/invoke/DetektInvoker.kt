package io.gitlab.arturbosch.detekt.invoke

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

/**
 * @author Marvin Ramin
 * @author Matthew Haughton
 */
object DetektInvoker {
    internal fun invokeCli(
        project: Project,
        arguments: List<CliArgument>,
        classpath: FileCollection,
        debug: Boolean = false,
        ignoreFailures: Boolean = false
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)

        if (debug) println(cliArguments)

        val proc = project.javaexec {
            it.main = DETEKT_MAIN
            it.classpath = classpath
            it.args = cliArguments
            it.isIgnoreExitValue = true
        }
        val exitValue = proc.exitValue
        if (debug) println("Detekt finished with exit value $exitValue")

        when (exitValue) {
            1 -> throw GradleException("There was a problem running detekt.")
            2 -> if (!ignoreFailures) throw GradleException("MaxIssues or failThreshold count was reached.")
        }
    }
}

private const val DETEKT_MAIN = "io.gitlab.arturbosch.detekt.cli.Main"
