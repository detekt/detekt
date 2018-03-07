package io.gitlab.arturbosch.detekt

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektCreateBaselineTask : DefaultTask() {

	lateinit var detekt: Detekt

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = "verification"
	}

	@TaskAction
	fun baseline() {
		DetektInvoker.createBaseline(detekt)
	}
}
