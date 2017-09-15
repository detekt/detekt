package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektIdeaFormatTask : DefaultTask() {

	init {
		description = "Uses an external idea installation to format your code."
		group = "verification"
	}

	@TaskAction
	fun format() {
		with(project.extensions.getByName("detekt") as DetektExtension) {
			if (debug) println("$ideaExtension")
			startProcess(ideaFormatArgs())
		}
	}
}
