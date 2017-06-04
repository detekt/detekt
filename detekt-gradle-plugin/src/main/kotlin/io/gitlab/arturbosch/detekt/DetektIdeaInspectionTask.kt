package io.gitlab.arturbosch.detekt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektIdeaInspectionTask : DefaultTask() {

	init {
		description = "Uses an extern idea installation to inspect your code."
	}

	@TaskAction
	fun format() {
		println("Hello from idea inspect!")
	}
}