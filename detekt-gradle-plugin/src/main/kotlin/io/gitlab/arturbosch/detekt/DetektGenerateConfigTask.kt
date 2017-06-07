package io.gitlab.arturbosch.detekt

import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektGenerateConfigTask : DefaultTask() {

	init {
		description = "Generate a detekt configuration file inside your project."
	}

	@TaskAction
	fun generateConfig() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		val configuration = project.buildscript.configurations.maybeCreate("detektConfig")
		project.buildscript.dependencies.add(configuration.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-cli", detektExtension.version))

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = configuration
			it.args("--project", project.projectDir.absolutePath, "--generate-config")
		}
	}
}