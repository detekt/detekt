package io.gitlab.arturbosch.detekt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektIdeaFormatTask : DefaultTask() {

	init {
		description = "Uses an extern idea installation to format your code."
	}

	@TaskAction
	fun format() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension
		println("Hello from idea format! ${detektExtension.ideaExtension}")
	}
}