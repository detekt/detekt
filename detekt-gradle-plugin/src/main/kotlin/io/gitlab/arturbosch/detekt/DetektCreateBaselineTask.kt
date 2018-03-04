package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektCreateBaselineTask : DefaultTask() {

	init {
		description = "Creates a detekt baseline on the given --baseline path."
		group = "verification"
	}

	private val createBaseline = "--create-baseline"

	@TaskAction
	fun baseline() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		DetektInvoker.createBaseline()
	}

}
