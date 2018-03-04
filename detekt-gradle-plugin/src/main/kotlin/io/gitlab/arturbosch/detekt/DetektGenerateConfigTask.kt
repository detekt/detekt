package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 */
open class DetektGenerateConfigTask : DefaultTask() {

	init {
		description = "Generate a detekt configuration file inside your project."
		group = "verification"
	}

	@TaskAction
	fun generateConfig() {
		val detektExtension = project.extensions.getByName("detekt") as DetektExtension

		DetektInvoker.generateConfig()
	}
}
