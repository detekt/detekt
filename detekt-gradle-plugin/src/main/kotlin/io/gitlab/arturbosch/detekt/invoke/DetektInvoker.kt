package io.gitlab.arturbosch.detekt.invoke

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
        debug: Boolean = false
    ) {
        val cliArguments = arguments.flatMap(CliArgument::toArgument)

        if (debug) println(cliArguments)
        project.javaexec {
            it.main = DETEKT_MAIN
            it.classpath = classpath
            it.args = cliArguments
        }
    }
}

private const val DETEKT_MAIN = "io.gitlab.arturbosch.detekt.cli.Main"
