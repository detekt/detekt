package io.gitlab.arturbosch.detekt.invoke

import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT
import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

/**
 * @author Marvin Ramin
 */
object DetektInvoker {
	internal fun invokeCli(project: Project, arguments: List<CliArgument>, debug: Boolean = false) {
		val cliArguments = arguments.map(CliArgument::toArgument).flatten()

		if (debug) println(cliArguments)
		project.javaexec {
			it.main = DETEKT_MAIN
			it.classpath = getConfigurations(project, debug)
			it.args = cliArguments
		}
	}

	private fun getConfigurations(project: Project, debug: Boolean = false): FileCollection {
		val detektConfigurations = setOf(CONFIGURATION_DETEKT_PLUGINS, CONFIGURATION_DETEKT)
		val configurations = project.configurations.filter { detektConfigurations.contains(it.name) }

		val files = project.files(configurations)
		if (debug) files.forEach { println(it) }
		return files
	}
}

private const val DETEKT_MAIN = "io.gitlab.arturbosch.detekt.cli.Main"
