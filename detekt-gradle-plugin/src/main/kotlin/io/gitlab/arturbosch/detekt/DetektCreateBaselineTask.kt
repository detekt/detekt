package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektCreateBaselineTask : DefaultTask() {

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = "verification"
	}

	private val createBaseline = "--create-baseline"

	@TaskAction
	fun baseline() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		val configuration = project.buildscript.configurations.maybeCreate("detektBaseline")
		project.buildscript.dependencies.add(configuration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektExtension.version))

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = configuration
			it.args(detektExtension.resolveArguments(project).plus(createBaseline))
		}
	}

}
