package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.IdeaExtension
import io.gitlab.arturbosch.detekt.invoke.ProcessExecutor
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
open class DetektIdeaInspectionTask : DefaultTask() {

	init {
		description = "Uses an external idea installation to inspect your code."
		group = LifecycleBasePlugin.VERIFICATION_GROUP
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	@SkipWhenEmpty
	lateinit var input: FileCollection

	@Internal
	@Optional
	var debugOrDefault: Boolean = false

	@Internal
	lateinit var ideaExtension: IdeaExtension

	@TaskAction
	fun inspect() {
		if (debugOrDefault) println("Running inspection task in debug mode")

		if (debugOrDefault) println("$ideaExtension")
		ProcessExecutor.startProcess(ideaExtension.inspectArgs(input.asPath))
	}
}
