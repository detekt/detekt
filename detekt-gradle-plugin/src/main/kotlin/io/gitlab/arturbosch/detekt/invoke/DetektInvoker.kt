package io.gitlab.arturbosch.detekt.invoke

import org.gradle.api.Project

/**
 * @author Marvin Ramin
 */
object DetektInvoker {
	internal fun invokeCli(project: Project, arguments: List<CliArgument>, debug: Boolean = false) {
		val args = arguments.map(CliArgument::toArgument).flatten()

		if (debug) println(args)
		project.javaexec {
			main = "io.gitlab.arturbosch.detekt.cli.Main"
			classpath(project.configurations.getAt("detekt"))
			args(args)
		}
	}
}
