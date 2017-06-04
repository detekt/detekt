package io.gitlab.arturbosch.detekt

import org.gradle.api.DefaultTask
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektFormatTask : DefaultTask() {

	init {
		description = "Format your kotlin code with detekt."
	}

	private val formatString = "--format"
	private val disableDefaults = "--disable-default-rulesets"

	@TaskAction
	fun format() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		val formatting = project.buildscript.configurations.maybeCreate("detektFormat")
		project.buildscript.dependencies.add(formatting.name, DefaultExternalModuleDependency(
				"io.gitlab.arturbosch.detekt", "detekt-formatting", detektExtension.version))

		project.javaexec {
			it.main = "io.gitlab.arturbosch.detekt.formatting.Formatting"
			it.classpath = formatting
			it.args(detektExtension.detektArgs.plus(listOf(formatString, disableDefaults)))
		}
	}

}