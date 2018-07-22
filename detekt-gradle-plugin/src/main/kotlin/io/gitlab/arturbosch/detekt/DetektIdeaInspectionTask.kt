package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektIdeaInspectionTask : DefaultTask() {

	lateinit var detekt: Detekt
	var ideaExtension: IdeaExtension? = null

	init {
		description = "Uses an external idea installation to inspect your code."
		group = "verification"
	}

	@TaskAction
	fun inspect() {
		if (ideaExtension == null) {
			throw GradleException("idea extension is not defined. It is required to run detekt idea tasks.")
		}

		if (detekt.debugOrDefault) println("$ideaExtension")
		ProcessExecutor.startProcess(ideaExtension!!.formatArgs(detekt.input.get().asPath))
	}
}
