package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektCheckTask : DefaultTask() {

	init {
		description = "Analyze your kotlin code with detekt."
	}

	@TaskAction
	fun check() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		val configuration = project.buildscript.configurations.maybeCreate("detektCheck")
		project.buildscript.dependencies.add(configuration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektExtension.version))

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = configuration
			it.args(detektExtension.profileArgumentsOrDefault(project))
		}
	}
}