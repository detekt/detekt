package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor.startProcess
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.org.jline.utils.Log

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektIdeaFormatTask : DefaultTask() {

	lateinit var detekt: Detekt
	var ideaExtension: IdeaExtension? = null

	init {
		description = "Uses an external idea installation to format your code."
		group = "verification"
	}

	@TaskAction
	fun format() {
		if (ideaExtension == null) {
			throw GradleException("idea extension is not defined. It is required to run detekt idea tasks.")
		}

		if (detekt.debug) Log.info("$ideaExtension")
		startProcess(ideaExtension!!.formatArgs(detekt.source.asFileTree.asPath))
	}
}
