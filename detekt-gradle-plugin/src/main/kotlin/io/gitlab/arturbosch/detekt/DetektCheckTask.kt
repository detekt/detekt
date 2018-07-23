package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.INPUT_PARAMETER
import io.gitlab.arturbosch.detekt.extensions.OUTPUT_PARAMETER
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Olivier Lemasle
 */
open class DetektCheckTask : DefaultTask() {

	val input: File?

	val output: File?

	private val classpath: FileCollection

	init {
		description = "Analyze your kotlin code with detekt."
		group = "verification"

		val detektExtension = project.extensions.getByName("detekt") as DetektExtension
		val arguments = detektExtension.resolveArguments(project)
		val inputIndex = arguments.indexOf(INPUT_PARAMETER)
		input = File(arguments[inputIndex + 1])

		val outputIndex = arguments.indexOf(OUTPUT_PARAMETER)
		output = File(arguments[outputIndex + 1])

		classpath = detektExtension.resolveClasspath(project)
		dependsOn(classpath)
	}

	@TaskAction
	fun check() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.cli.Main"
			it.classpath = classpath
			it.args(detektExtension.resolveArguments(project))
		}
	}
}
