package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektGenerateConfigTask : DefaultTask() {

	lateinit var detekt: Detekt

	init {
		description = "Generate a detekt configuration file inside your project."
		group = "verification"
	}

	@TaskAction
	fun generateConfig() {
		DetektInvoker.generateConfig(detekt)
	}
}
