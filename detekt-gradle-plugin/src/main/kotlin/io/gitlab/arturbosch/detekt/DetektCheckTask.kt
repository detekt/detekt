package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.INPUT_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.OUTPUT_PARAMETER
import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektCheckTask : DefaultTask() {

	@InputDirectory
	val input: File?

	@OutputDirectory
	val output: File?

	init {
		description = "Analyze your kotlin code with detekt."
		group = "verification"

		val detektExtension = project.extensions.getByName("detekt") as DetektExtension
		val arguments = detektExtension.resolveArguments(project)
		val inputIndex = arguments.indexOf(INPUT_PARAMETER)
		input = File(arguments[inputIndex + 1])

		val outputIndex = arguments.indexOf(OUTPUT_PARAMETER)
		output = File(arguments[outputIndex + 1])
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
			it.args(detektExtension.resolveArguments(project))
		}
	}
}
