package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektIdeaInspectionTask : DefaultTask() {

	init {
		description = "Uses an external idea installation to inspect your code."
		group = "verification"
	}

	@TaskAction
	fun inspect() {
		with(project.extensions.getByName("detekt") as DetektExtension) {
			if (debug) println("$ideaExtension")
			startProcess(ideaInspectArgs())
		}
	}
}
